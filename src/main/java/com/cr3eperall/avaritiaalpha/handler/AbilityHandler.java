package com.cr3eperall.avaritiaalpha.handler;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.items.InfinityArmor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static net.minecraft.inventory.EquipmentSlotType.*;

/**
 * Handles all abilities for ANY LivingEntity.
 * Some abilities are player specific, but just don't give a zombie your boots..
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = AvaritiaAlpha.MOD_ID)
public class AbilityHandler {

    //@formatter:off
    public static final Set<String> entitiesWithHelmets =     new HashSet<>();
    public static final Set<String> entitiesWithChestplates = new HashSet<>();
    public static final Set<String> entitiesWithLeggings =    new HashSet<>();
    public static final Set<String> entitiesWithBoots =       new HashSet<>();
    public static final Set<String> entitiesWithFlight =      new HashSet<>();
    //@formatter:on

    public static boolean isPlayerWearing(LivingEntity entity, EquipmentSlotType slot, Predicate<Item> predicate) {
        ItemStack stack = entity.getItemStackFromSlot(slot);
        return !stack.isEmpty() && predicate.test(stack.getItem());
    }

    @SubscribeEvent
    //Updates all ability states for an entity, Handles firing updates and state changes.
    public static void updateAbilities(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        String key = entity.getCachedUniqueIdString() + "|" + entity.world.isRemote;

        boolean hasHelmet = isPlayerWearing(event.getEntityLiving(), HEAD, item -> item instanceof InfinityArmor);
        boolean hasChestplate = isPlayerWearing(event.getEntityLiving(), CHEST, item -> item instanceof InfinityArmor);
        boolean hasLeggings = isPlayerWearing(event.getEntityLiving(), LEGS, item -> item instanceof InfinityArmor);
        boolean hasBoots = isPlayerWearing(event.getEntityLiving(), FEET, item -> item instanceof InfinityArmor);

        //Helmet toggle.
        if (hasHelmet) {
            entitiesWithHelmets.add(key);
            handleHelmetStateChange(entity, true);
        }
        if (!hasHelmet) {
            entitiesWithHelmets.remove(key);
            handleHelmetStateChange(entity, false);
        }

        //Chestplate toggle.
        if (hasChestplate) {
            entitiesWithChestplates.add(key);
            handleChestplateStateChange(entity, true);
        }
        if (!hasChestplate) {
            entitiesWithChestplates.remove(key);
            handleChestplateStateChange(entity, false);
        }

        //Leggings toggle.
        if (hasLeggings) {
            entitiesWithLeggings.add(key);
            handleLeggingsStateChange(entity, true);
        }
        if (!hasLeggings) {
            entitiesWithLeggings.remove(key);
            handleLeggingsStateChange(entity, false);
        }

        //Boots toggle.
        if (hasBoots) {
            handleBootsStateChange(entity);
            entitiesWithBoots.add(key);
        }
        if (!hasBoots) {
            handleBootsStateChange(entity);
            entitiesWithBoots.remove(key);
        }

        //Active ability ticking.
        if (entitiesWithHelmets.contains(key)) {
            tickHelmetAbilities(entity);
        }
        if (entitiesWithChestplates.contains(key)) {
            tickChestplateAbilities(entity);
        }
        if (entitiesWithLeggings.contains(key)) {
            tickLeggingsAbilities(entity);
        }
        if (entitiesWithBoots.contains(key)) {
            tickBootsAbilities(entity);
        }
    }

    /**
     * Strips all Abilities from an entity if the entity had any special abilities.
     *
     * @param entity LivingEntity we speak of.
     */
    private static void stripAbilities(LivingEntity entity) {
        String key = entity.getCachedUniqueIdString() + "|" + entity.world.isRemote;

        if (entitiesWithHelmets.remove(key)) {
            handleHelmetStateChange(entity, false);
        }

        if (entitiesWithChestplates.remove(key)) {
            handleChestplateStateChange(entity, false);
        }

        if (entitiesWithLeggings.remove(key)) {
            handleLeggingsStateChange(entity, false);
        }

        if (entitiesWithBoots.remove(key)) {
            handleBootsStateChange(entity);
        }
    }

    //region StateChanging
    private static void handleHelmetStateChange(LivingEntity entity, boolean isNew) {
        //TODO, Helmet abilities? Water breathing, NightVision, Auto Eat or No Hunger, No bad effects.
    }

    private static void handleChestplateStateChange(LivingEntity entity, boolean isNew) {
        String key = entity.getCachedUniqueIdString() + "|" + entity.world.isRemote;
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = ((PlayerEntity) entity);
            if (isNew) {
                player.abilities.allowFlying = true;
                entitiesWithFlight.add(key);
            } else {
                if (!player.abilities.isCreativeMode && entitiesWithFlight.contains(key)) {
                    player.abilities.allowFlying = false;
                    player.abilities.isFlying = false;
                    entitiesWithFlight.remove(key);
                }
            }
        }
    }

    private static void handleLeggingsStateChange(LivingEntity entity, boolean isNew) {

    }

    private static void handleBootsStateChange(LivingEntity entity) {
        String temp_key = entity.getCachedUniqueIdString() + "|" + entity.world.isRemote;
        boolean hasBoots = isPlayerWearing(entity, FEET, item -> item instanceof InfinityArmor);
        if (hasBoots) {
            entity.stepHeight = 1.0625F;//Step 17 pixels, Allows for stepping directly from a path to the top of a block next to the path.
            if (!entitiesWithBoots.contains(temp_key)) {
                entitiesWithBoots.add(temp_key);
            }
        } else {
            if (entitiesWithBoots.contains(temp_key)) {
                entity.stepHeight = 0.5F;
                entitiesWithBoots.remove(temp_key);
            }
        }
    }
    //endregion

    //region Ability Ticking
    private static void tickHelmetAbilities(LivingEntity entity) {

    }

    private static void tickChestplateAbilities(LivingEntity entity) {

    }

    private static void tickLeggingsAbilities(LivingEntity entity) {

    }

    private static void tickBootsAbilities(LivingEntity entity) {
        boolean flying = entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.isFlying;
        boolean swimming = entity.isInWater();
        if (entity.onGround || flying || swimming) {
            boolean sneaking = entity.isSneaking();

            float speed = 0.15f * (flying ? 1.1f : 1.0f)
                    //* (swimming ? 1.2f : 1.0f)
                    * (sneaking ? 0.1f : 1.0f);

            if (entity.moveForward > 0f) {
                entity.moveRelative(speed, new Vec3d(0,0,1));
            } else if (entity.moveForward < 0f) {
                entity.moveRelative(-speed * 0.3f, new Vec3d(0,0,1));
            }

            if (entity.moveStrafing != 0f) {
                entity.moveRelative(speed * 0.5f * Math.signum(entity.moveStrafing), new Vec3d(1,0,0));
            }
        }
    }
    //endregion

    //region Ability Specific Events
    @SubscribeEvent
    public static void jumpBoost(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entitiesWithBoots.contains( entity.getCachedUniqueIdString() + "|" + entity.world.isRemote)) {
            entity.setMotion(entity.getMotion().add(0,0.4,0));
        }
    }
    //endregion

    //region Ability Striping Events
    //These are anything that should strip all abilities from an entity, Anything that creates an entity.
    @SubscribeEvent
    public static void onPlayerDemensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        stripAbilities(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        stripAbilities(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        stripAbilities(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        stripAbilities(event.getPlayer());
    }

    @SubscribeEvent
    public static void entityContstructedEvent(EntityEvent.EntityConstructing event) {
        if (event.getEntity() instanceof LivingEntity) {
            //stripAbilities((LivingEntity) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        stripAbilities(event.getEntityLiving());
    }
    //endregion
}
