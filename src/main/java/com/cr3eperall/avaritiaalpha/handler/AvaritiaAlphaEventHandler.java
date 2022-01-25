package com.cr3eperall.avaritiaalpha.handler;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.ItemUtils;
import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.config.Config;
import com.cr3eperall.avaritiaalpha.items.InfinityArmor;
import com.cr3eperall.avaritiaalpha.items.MatterCluster;
import com.cr3eperall.avaritiaalpha.items.ModItems;
import com.cr3eperall.avaritiaalpha.items.tools.InfinityPickaxe;
import com.cr3eperall.avaritiaalpha.items.tools.InfinitySword;
import com.cr3eperall.avaritiaalpha.items.tools.SkullfireSword;
import com.cr3eperall.avaritiaalpha.util.TextUtils;
import com.cr3eperall.avaritiaalpha.util.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WitherSkeletonSkullBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;


@Mod.EventBusSubscriber(modid = AvaritiaAlpha.MOD_ID)
public class AvaritiaAlphaEventHandler {

    private static Map<Integer, List<AOECrawlerTask>> crawlerTasks = new HashMap<>();

    private static Set<ItemStack> capturedDrops = new LinkedHashSet<>();
    private static boolean doItemCapture = false;
    private static boolean doItemCaptureStop = false;

    private static BlockPos dropLocation=null;
    private static World world=null;
    private static ItemStack holding=null;
    private static boolean filterTrash=false;

    //These are defaults, loaded from config.
    public static final Set<String> defaultTrashOres = new HashSet<>();

    public static boolean isInfinite(PlayerEntity player) {
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getSlotType() != EquipmentSlotType.Group.ARMOR) {
                continue;
            }
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof InfinityArmor)) {
                return false;
            }
        }
        return true;
    }

    //region ItemEntity capture.
    public static void enableItemCapture() {
        doItemCapture = true;
    }

    public static void stopItemCapture() {
        doItemCaptureStop = true;
    }

    public static boolean isItemCaptureEnabled() {
        return doItemCapture;
    }

    public static Set<ItemStack> getCapturedDrops() {
        Set<ItemStack> dropsCopy = new LinkedHashSet<>(capturedDrops);
        capturedDrops.clear();
        return dropsCopy;
    }

    public static void dropCapturedItems(World world, ItemStack holding, BlockPos dropLocation, boolean filterTrash) {
        AvaritiaAlphaEventHandler.world=world;
        AvaritiaAlphaEventHandler.dropLocation=dropLocation;
        AvaritiaAlphaEventHandler.holding=holding;
        AvaritiaAlphaEventHandler.filterTrash=filterTrash;
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (doItemCapture) {
            if (event.getEntity() instanceof ItemEntity) {
                //System.out.println("joined "+((ItemEntity) event.getEntity()).getItem().getTranslationKey());
                ItemStack stack = ((ItemEntity) event.getEntity()).getItem();
                //System.out.println("cancelled event: "+stack.getItem().getTranslationKey());
                capturedDrops.add(stack);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onTickEnd(TickEvent.WorldTickEvent event) {//TODO, clamp at specific num ops per tick.
        if (event.phase == TickEvent.Phase.END) {
            if (doItemCaptureStop){
                //System.out.println("stopped item capture");
                doItemCaptureStop=false;
                doItemCapture=false;
                if (dropLocation!=null) {
                    ToolHelper.dropClusters(world,holding,dropLocation,getCapturedDrops(),filterTrash);
                    dropLocation=null;
                    world=null;
                    holding=null;
                    filterTrash=false;
                }
            }
            int dim = event.world.getDimension().getType().getId();
            if (crawlerTasks.containsKey(dim)) {
                List<AOECrawlerTask> swappers = crawlerTasks.get(dim);
                List<AOECrawlerTask> swappersSafe = new ArrayList<>(swappers);
                swappers.clear();
                for (AOECrawlerTask s : swappersSafe) {
                    if (s != null) {
                        s.tick();
                    }
                }
            }
        }
    }

    //endregion

    public static AOECrawlerTask startCrawlerTask(World world, PlayerEntity player, ItemStack stack, BlockPos coords, int steps, boolean leaves, boolean force, Set<BlockPos> posChecked) {
        AOECrawlerTask swapper = new AOECrawlerTask(world, player, stack, coords, steps, leaves, force, posChecked);
        int dim = world.getDimension().getType().getId();
        if (!crawlerTasks.containsKey(dim)) {
            crawlerTasks.put(dim, new ArrayList<>());
        }
        crawlerTasks.get(dim).add(swapper);
        return swapper;
    }

    @SubscribeEvent
    public static void onPlayerMine(PlayerInteractEvent.LeftClickBlock event) {
        if (!Config.bedrockBreaker.get() || event.getFace() == null || event.getWorld().isRemote || event.getItemStack().isEmpty() || event.getPlayer().isCreative()) {
            return;
        }
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (state.getBlockHardness(world, event.getPos()) < 0 && (event.getItemStack().getItem() instanceof InfinityPickaxe) && (state.getMaterial() == Material.ROCK || state.getMaterial() == Material.IRON)) {

            if (event.getItemStack().getTag() != null && event.getItemStack().getTag().getBoolean("hammer")) {
                event.getItemStack().onBlockStartBreak(event.getPos(), event.getPlayer());
            } else {
                ToolHelper.removeBlockWithOPDrop(event.getPlayer(),event.getItemStack(),event.getWorld(),event.getPos());
                event.getWorld().playSound(event.getPlayer(), pos, block.getSoundType(state,world,pos, event.getPlayer()).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    @SubscribeEvent
    public static void handleExtraLuck(BlockEvent.HarvestDropsEvent event) {//TODO check
        if (event.getHarvester() == null) {
            return;
        }
        ItemStack mainHand = event.getHarvester().getHeldItem(Hand.MAIN_HAND);

        if (!mainHand.isEmpty() && mainHand.getItem() == ModItems.infinity_pickaxe) {
            applyLuck(event, 4);
        }
    }

    public static void applyLuck(BlockEvent.HarvestDropsEvent event, int multiplier) {//TODO
        //Only do stuff on rock.
        if (event.getState().getMaterial() == Material.ROCK) {
            for (ItemStack drop : event.getDrops()) {
                //We are a drop that is not the same as the Blocks BlockItem and the drop itself is not an BlockItem, AKA, Redstone, Lapis.
                if (drop.getItem() != Item.getItemFromBlock(event.getState().getBlock()) && !(drop.getItem() instanceof BlockItem)) {
                    //Apply standard Luck modifier
                    drop.setCount(Math.min(drop.getCount() * multiplier, drop.getMaxStackSize()));
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof InfinitySword) {
            for (int x = 0; x < event.getToolTip().size(); x++) {
                if (event.getToolTip().get(x).getFormattedText().contains(I18n.format("attribute.name.generic.attackDamage")) || event.getToolTip().get(x).getFormattedText().contains("Attack Damage")) {
                    event.getToolTip().set(x, TextUtils.makeFabulous(I18n.format("tip.infinity")).appendSibling(new StringTextComponent(" "+I18n.format("attribute.name.generic.attackDamage")).applyTextStyle(TextFormatting.BLUE)));
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onGetHurt(LivingHurtEvent event) {//TODO check
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == ModItems.infinity_sword && player.isHandActive()) {//TODO Blocking? Maybe add a shield? --- maybe not
            event.setCanceled(true);
        }
        if (isInfinite(player) && !event.getSource().damageType.equals("infinity")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAttacked(LivingAttackEvent event) {//TODO check
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }
        if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof PlayerEntity) {
            return;
        }
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        if (isInfinite(player) && !event.getSource().damageType.equals("infinity")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {//TODO check
        if (event.isRecentlyHit() && event.getEntityLiving() instanceof AbstractSkeletonEntity && event.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
            if (!player.getHeldItem(Hand.MAIN_HAND).isEmpty() && player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof SkullfireSword) {
                addDrop(event, new ItemStack(Items.WITHER_SKELETON_SKULL, 1));
            }
        }
    }

    @SubscribeEvent
    public static void diggity(PlayerEvent.BreakSpeed event) {
        if (!event.getEntityLiving().getHeldItem(Hand.MAIN_HAND).isEmpty()) {
            ItemStack held = event.getEntityLiving().getHeldItem(Hand.MAIN_HAND);
            if (held.getItem() == ModItems.infinity_pickaxe || held.getItem() == ModItems.infinity_shovel) {
                if (!event.getEntityLiving().onGround) {
                    event.setNewSpeed(event.getNewSpeed() * 5);
                }
                if (!event.getEntityLiving().isInWater() && !EnchantmentHelper.hasAquaAffinity(event.getEntityLiving())) {
                    event.setNewSpeed(event.getNewSpeed() * 5);
                }
                if (held.getTag() != null) {
                    if (held.getTag().getBoolean("hammer") || held.getTag().getBoolean("destroyer")) {
                        event.setNewSpeed(event.getNewSpeed() * 0.5F);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void canHarvest(PlayerEvent.HarvestCheck event) {//TODO check
        if (!event.getEntityLiving().getHeldItem(Hand.MAIN_HAND).isEmpty()) {
            ItemStack held = event.getEntityLiving().getHeldItem(Hand.MAIN_HAND);
            if (held.getItem() == ModItems.infinity_shovel && event.getTargetBlock().getMaterial() == Material.ROCK) {
                if (held.getTag() != null && held.getTag().getBoolean("destroyer") && isGarbageBlock(event.getTargetBlock().getBlock())) {
                    event.setResult(Event.Result.ALLOW);
                }
            }
        }
    }

    private static boolean isGarbageBlock(Block block) {
        if (block.equals(Blocks.COBBLESTONE) || block.equals(Blocks.STONE) || block.equals(Blocks.NETHERRACK)) {
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {//TODO check
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if (isInfinite(player) && !event.getSource().getDamageType().equals("infinity")) {
                event.setCanceled(true);
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    private static void addDrop(LivingDropsEvent event, ItemStack drop) {
        ItemEntity itemEntity = new ItemEntity(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, drop);
        itemEntity.setDefaultPickupDelay();
        event.getDrops().add(itemEntity);
    }

    @SubscribeEvent
    public static void clusterClustererererer(EntityItemPickupEvent event) {//TODO check
        if (event.getPlayer() != null && event.getItem().getItem().getItem() == ModItems.MATTERCLUSTER) {
            ItemStack stack = event.getItem().getItem();
            PlayerEntity player = event.getPlayer();

            for (ItemStack slot : player.inventory.mainInventory) {
                if (stack.isEmpty()) {
                    break;
                }
                if (slot.getItem() == ModItems.MATTERCLUSTER) {
                    MatterCluster.mergeClusters(stack, slot);
                }
            }
        }
    }

}
