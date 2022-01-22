package com.cr3eperall.avaritiaalpha.items.tools;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.entity.ImmortalItemEntity;
import com.cr3eperall.avaritiaalpha.handler.AvaritiaAlphaEventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfinityHoe extends HoeItem {
    public InfinityHoe() {
        super(InfinityMaterial.get(), 20F, new Properties().rarity(InfinityMaterial.COSMIC_RARITY).group(AvaritiaAlpha.setup.itemGroup));
        setRegistryName("infinity_hoe");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player=context.getPlayer();
        World world=context.getWorld();
        BlockPos origin=context.getPos();
        //Direction facing=context.getFace();
        ItemStack stack = player.getHeldItem(context.getHand());
        if (!attemptHoe(context)) {
            if (world.getBlockState(origin).getBlock() != Blocks.FARMLAND) {
                return ActionResultType.FAIL;
            }
        }

        if (player.isSneaking()) {
            return ActionResultType.SUCCESS;
        }

        int aoe_range = 4;
        AvaritiaAlphaEventHandler.enableItemCapture();
        Set<BlockPos> posSet=getAllInBox(origin.add(-aoe_range,0,-aoe_range),origin.add(aoe_range,0,aoe_range));
        for (BlockPos aoePos : posSet) {
            if (aoePos.equals(origin)) {
                continue;
            }

            boolean airOrReplaceable = world.isAirBlock(aoePos) || world.getBlockState(aoePos).getBlock().isReplaceable(world.getBlockState(aoePos), new BlockItemUseContext(context));
            boolean lowerBlockOk = world.isTopSolid(aoePos.down(), player) || world.getBlockState(aoePos.down()).getBlock() == Blocks.FARMLAND;

            if (airOrReplaceable && lowerBlockOk && (player.isCreative() || player.inventory.hasItemStack(new ItemStack(Blocks.DIRT)))) {
                BlockEvent.EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(new BlockSnapshot(world, aoePos, Blocks.DIRT.getDefaultState()), world.getBlockState(aoePos), player);

                if (!event.isCanceled() && (player.isCreative() || consumeStack(new ItemStack(Blocks.DIRT), player.inventory))) {
                    world.setBlockState(aoePos, Blocks.DIRT.getDefaultState());
                }
            }

            boolean canDropAbove = world.getBlockState(aoePos.up()).getBlock() == Blocks.DIRT || world.getBlockState(aoePos.up()).getBlock() == Blocks.GRASS_BLOCK || world.getBlockState(aoePos.up()).getBlock() == Blocks.FARMLAND;
            boolean canRemoveAbove = canDropAbove || world.getBlockState(aoePos.up()).getBlock().isReplaceable(world.getBlockState(aoePos.up()), new BlockItemUseContext(context));
            boolean up2airOrReplaceable = world.isAirBlock(aoePos.up().up()) || world.getBlockState(aoePos.up().up()).getBlock().isReplaceable(world.getBlockState(aoePos.up().up()), new BlockItemUseContext(context));

            if (!world.isAirBlock(aoePos.up()) && canRemoveAbove && up2airOrReplaceable) {
                if (!world.isRemote && canDropAbove) {
                    world.addEntity(new ItemEntity(world, player.posX, player.posY, player.posZ, new ItemStack(Blocks.DIRT)));
                }
                world.destroyBlock(aoePos.up(),false);
            }
            attemptHoe(new ItemUseContext(player,context.getHand(),new BlockRayTraceResult(context.getHitVec(),Direction.UP,aoePos,context.isInside())));
        }

        AvaritiaAlphaEventHandler.stopItemCapture();
        AvaritiaAlphaEventHandler.dropCapturedItems(world,stack,origin,false);

        return ActionResultType.SUCCESS;
    }

    public boolean consumeStack(ItemStack stack, IInventory inventory) {
        if (stack.isEmpty()) {
            return false;
        }

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack s = inventory.getStackInSlot(i);
            if (s.isEmpty()) {
                continue;
            }

            if (ItemStack.areItemsEqual(stack, s) && stack.getDamage() == s.getDamage() && s.getCount() >= stack.getCount()) {
                s.shrink(stack.getCount());
                inventory.markDirty();
                return true;
            }
        }

        return false;
    }

    private Set<BlockPos> getAllInBox(BlockPos a, BlockPos b){
        Set<BlockPos> posSet=new HashSet<>();
        BlockPos min=new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(),b.getZ()));
        BlockPos max=new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(),b.getZ()));

        for (int x = 0; x < max.getX() - min.getX()+1; x++) {
            for (int y = 0; y < max.getY() - min.getY()+1; y++) {
                for (int z = 0; z < max.getZ() - min.getZ()+1; z++) {
                    posSet.add(new BlockPos(min.add(x,y,z)));
                }
            }
        }
        return posSet;
    }

    /**
     * Attempts to hoe a block.
     * Basically carbon copy of vanilla but not really.
     *
     * @return If the hoe operation was successful.
     */
    private boolean attemptHoe(ItemUseContext context) {
        PlayerEntity player=context.getPlayer();
        World world=context.getWorld();
        BlockPos pos=context.getPos();
        Direction face=context.getFace();
        ItemStack hoeStack = player.getHeldItem(context.getHand());
        if (!player.canPlayerEdit(pos, face, hoeStack)) {
            return false;
        }

        int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(context);
        if (hook != 0) {
            return hook > 0;
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (face != Direction.DOWN && world.isAirBlock(pos.up())) {
            if (block == Blocks.GRASS_BLOCK || block == Blocks.GRASS_PATH || block == Blocks.DIRT) {
                setBlock(hoeStack, player, world, pos, Blocks.FARMLAND.getDefaultState());
                return true;
            }

            if(block==Blocks.COARSE_DIRT){
                setBlock(hoeStack, player, world, pos, Blocks.COARSE_DIRT.getDefaultState());
                return true;
            }
        }
        return false;
    }

    protected void setBlock(ItemStack stack, PlayerEntity player, World worldIn, BlockPos pos, BlockState state) {
        worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, state, 0b1011);
        }
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new ImmortalItemEntity(world, location, itemstack);
    }
}
