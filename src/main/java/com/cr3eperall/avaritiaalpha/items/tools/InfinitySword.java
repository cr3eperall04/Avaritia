package com.cr3eperall.avaritiaalpha.items.tools;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.entity.ImmortalItemEntity;
import com.cr3eperall.avaritiaalpha.handler.AvaritiaAlphaEventHandler;
import com.cr3eperall.avaritiaalpha.items.ModItems;
import com.cr3eperall.avaritiaalpha.util.DamageSourceInfinitySword;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class InfinitySword extends SwordItem {

    public InfinitySword() {
        super(InfinityMaterial.get(), 9999, -3F, new Properties().rarity(InfinityMaterial.COSMIC_RARITY).group(AvaritiaAlpha.setup.itemGroup));
        setRegistryName("infinity_sword");
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity victim, LivingEntity player) {
        if (player.world.isRemote) {
            return true;
        }
        if (victim instanceof PlayerEntity) {
            PlayerEntity pvp = (PlayerEntity) victim;
            if (AvaritiaAlphaEventHandler.isInfinite(pvp)) {
                victim.attackEntityFrom(new DamageSourceInfinitySword(player).setDamageBypassesArmor(), 4.0F);
                return true;
            }
            if (pvp.getHeldItem(Hand.MAIN_HAND).getItem() == ModItems.INFINITYSWORD && pvp.isHandActive()) {
                return true;
            }
        }

        victim.getCombatTracker().trackDamage(new DamageSourceInfinitySword(player), victim.getHealth(), victim.getHealth());
        victim.setHealth(0);
        victim.onDeath(new EntityDamageSource("infinity", player));
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (!entity.world.isRemote && entity instanceof PlayerEntity) {
            PlayerEntity victim = (PlayerEntity) entity;
            if (victim.isCreative() && victim.isAlive() && victim.getHealth() > 0 && !AvaritiaAlphaEventHandler.isInfinite(victim)) {
                victim.getCombatTracker().trackDamage(new DamageSourceInfinitySword(player), victim.getHealth(), victim.getHealth());
                victim.setHealth(0);
                victim.onDeath(new EntityDamageSource("infinity", player));
                //TODO
                //player.addStat(Achievements.creative_kill, 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new ImmortalItemEntity(world,location,itemstack);
    }


}
