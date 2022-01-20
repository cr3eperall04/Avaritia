package com.cr3eperall.avaritiaalpha.util;

import codechicken.lib.util.ItemUtils;
import com.cr3eperall.avaritiaalpha.handler.AvaritiaAlphaEventHandler;
import com.cr3eperall.avaritiaalpha.items.MatterCluster;
import com.cr3eperall.avaritiaalpha.items.ModItems;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class ToolHelper {

    public static Material[] materialsPick = new Material[] { Material.ROCK, Material.IRON, Material.ICE, Material.GLASS, Material.PISTON, Material.ANVIL };
    public static Material[] materialsShovel = new Material[] { Material.ORGANIC, Material.EARTH, Material.SAND, Material.SNOW, Material.SNOW_BLOCK, Material.CLAY };
    public static Set<Material> materialsAxe = Sets.newHashSet(Material.CORAL, Material.LEAVES, Material.PLANTS, Material.TALL_PLANTS, Material.WOOD, Material.LEAVES);

    public static void aoeBlocks(PlayerEntity player, ItemStack stack, World world, BlockPos origin, BlockPos min, BlockPos max, Block target, Set<Material> validMaterials, boolean filterTrash) {

        AvaritiaAlphaEventHandler.enableItemCapture();

        for (int lx = min.getX(); lx < max.getX(); lx++) {
            for (int ly = min.getY(); ly < max.getY(); ly++) {
                for (int lz = min.getZ(); lz < max.getZ(); lz++) {
                    BlockPos pos = origin.add(lx, ly, lz);
                    removeBlockWithDrops(player, stack, world, pos, target, validMaterials);
                }
            }
        }

        AvaritiaAlphaEventHandler.stopItemCapture();
        AvaritiaAlphaEventHandler.dropCapturedItems(world,stack,origin,filterTrash);//drops on end of tick
    }

    public static void dropClusters(World world, ItemStack stack, BlockPos origin, Set<ItemStack> drops, boolean filterTrash){
        if (filterTrash) {
            drops = removeTrash(stack, drops);
        }
        if (!world.isRemote()) {
            List<ItemStack> clusters = MatterCluster.makeClusters(drops);
            for (ItemStack cluster : clusters) {
                ItemUtils.dropItem(world, origin, cluster);
            }
        }
    }

    public static void removeBlockWithDrops(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Block target, Set<Material> validMaterials) {
        if (!world.isBlockLoaded(pos)) {
            return;
        }
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!world.isRemote) {
            if ((target != null && target != state.getBlock()) || block.isAir(state, world, pos)) {
                return;
            }
            Material material = state.getMaterial();
            if (block == Blocks.GRASS_BLOCK && stack.getItem() == ModItems.INFINITYAXE) {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            }
            //if (block == Blocks.GRASS && stack.getItem() == ModItems.INFINITYAXE) {
            //    world.destroyBlock(pos,false);
            //}
            if (!block.canHarvestBlock(state, world, pos, player) || !validMaterials.contains(material)) {
                return;
            }
            //BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
            //MinecraftForge.EVENT_BUS.post(event);
            //if (!event.isCanceled()) {
                world.destroyBlock(pos,!player.isCreative());
            //}
        }
    }

    public static Set<ItemStack> removeTrash(ItemStack holdingStack, Set<ItemStack> drops) {
        Set<ItemStack> trashItems = new HashSet<>();
        for (ItemStack drop : drops) {
            if (isTrash(holdingStack, drop)) {
                //Lumberjack.info("Removing: " + drop.toString());
                trashItems.add(drop);
            }
        }
        drops.removeAll(trashItems);
        return drops;
    }

    private static boolean isTrash(ItemStack holdingStack, ItemStack suspect) {
        for (ResourceLocation id : ItemTags.getCollection().getOwningTags(suspect.getItem())) {
            for (String ore : AvaritiaAlphaEventHandler.defaultTrashOres) {
                if (id.equals(ResourceLocation.tryCreate(ore))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<ItemStack> collateDropList(Set<ItemStack> input) {
        return collateMatterClusterContents(collateMatterCluster(input));
    }

    public static List<ItemStack> collateMatterClusterContents(Map<ItemStackWrapper, Integer> input) {
        List<ItemStack> collated = new ArrayList<>();

        for (Map.Entry<ItemStackWrapper, Integer> e : input.entrySet()) {
            int count = e.getValue();
            ItemStackWrapper wrap = e.getKey();

            int size = wrap.stack.getMaxStackSize();
            int fullstacks = (int) Math.floor(count / size);

            for (int i = 0; i < fullstacks; i++) {
                count -= size;
                ItemStack stack = wrap.stack.copy();
                stack.setCount(size);
                collated.add(stack);
            }

            if (count > 0) {
                ItemStack stack = wrap.stack.copy();
                stack.setCount(count);
                collated.add(stack);
            }
        }

        return collated;
    }

    public static Map<ItemStackWrapper, Integer> collateMatterCluster(Set<ItemStack> input) {
        Map<ItemStackWrapper, Integer> counts = new HashMap<>();

        if (input != null) {
            for (ItemStack stack : input) {
                ItemStackWrapper wrap = new ItemStackWrapper(stack);
                if (!counts.containsKey(wrap)) {
                    counts.put(wrap, 0);
                }

                counts.put(wrap, counts.get(wrap) + stack.getCount());
            }
        }

        return counts;
    }
}
