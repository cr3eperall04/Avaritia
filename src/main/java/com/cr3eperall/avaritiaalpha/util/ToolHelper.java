package com.cr3eperall.avaritiaalpha.util;

import codechicken.lib.raytracer.RayTracer;
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
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

import java.util.*;

public class ToolHelper {

    public static Set<Material> materialsPick = Sets.newHashSet( Material.ROCK, Material.IRON, Material.ICE, Material.GLASS, Material.PISTON, Material.ANVIL );
    public static Set<Material> materialsShovel = Sets.newHashSet( Material.ORGANIC, Material.EARTH, Material.SAND, Material.SNOW, Material.SNOW_BLOCK, Material.CLAY );
    public static Set<Material> materialsAxe = Sets.newHashSet(Material.CORAL, Material.LEAVES, Material.PLANTS, Material.TALL_PLANTS, Material.WOOD, Material.LEAVES);

    public static void aoeBlocks(PlayerEntity player, ItemStack stack, World world, BlockPos origin, BlockPos min, BlockPos max, Block target, Set<Material> validMaterials, boolean filterTrash, boolean checkHarvestability) {

        AvaritiaAlphaEventHandler.enableItemCapture();

        for (int lx = min.getX(); lx < max.getX(); lx++) {
            for (int ly = min.getY(); ly < max.getY(); ly++) {
                for (int lz = min.getZ(); lz < max.getZ(); lz++) {
                    BlockPos pos = origin.add(lx, ly, lz);
                    removeBlockWithDrops(player, stack, world, pos, target, validMaterials, checkHarvestability);
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

    public static void removeBlockWithDrops(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Block target, Set<Material> validMaterials, boolean checkHarvestability) {
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

            if (checkHarvestability) {
                if (!block.canHarvestBlock(state, world, pos, player) || !validMaterials.contains(material)) {
                    return;
                }
                world.destroyBlock(pos,!player.isCreative());
            }else{
                removeBlockWithOPDrop(player,stack,world,pos);
            }
        }
    }

    public static void removeBlockWithOPDrop(PlayerEntity player, ItemStack stack, World world, BlockPos pos){
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        List<ItemStack> drops=block.getDrops(state, new LootContext.Builder(world.getServer().func_71218_a(player.dimension)).withParameter(LootParameters.POSITION,pos).withParameter(LootParameters.TOOL,stack));
        if (drops.isEmpty() || drops.get(0).isEmpty()) {
            ItemStack drop =block.getPickBlock(state, RayTracer.retrace(player,10, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE), world, pos, player);
            if (drop.isEmpty()) {
                drop=new ItemStack(block, 1);
            }
            ItemUtils.dropItem(world,pos,drop);
        } else {
            block.harvestBlock(world, player, pos, state, null, stack);
        }
        world.destroyBlock(pos,false);
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
