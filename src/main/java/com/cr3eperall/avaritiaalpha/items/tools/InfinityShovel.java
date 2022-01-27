package com.cr3eperall.avaritiaalpha.items.tools;

import codechicken.lib.raytracer.RayTracer;
import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.entity.ImmortalItemEntity;
import com.cr3eperall.avaritiaalpha.util.ToolHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class InfinityShovel extends ShovelItem {
    public InfinityShovel() {
        super(InfinityMaterial.get(), 0, 7, new Properties().rarity(InfinityMaterial.COSMIC_RARITY).group(AvaritiaAlpha.setup.itemGroup));
        setRegistryName("infinity_shovel");
        addPropertyOverride(new ResourceLocation(AvaritiaAlpha.MOD_ID + ":" + "destroyer"), new IItemPropertyGetter() {
            @Override
            public float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity holder) {
                return (stack.getTag() != null && stack.getTag().getBoolean("destroyer")) ? 1 : 0;
            }
        });
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (stack.getTag() != null && stack.getTag().getBoolean("destroyer")) {
            return 5.0F;
        }
        for (ToolType type : getToolTypes(stack)) {
            if (state.getBlock().isToolEffective(state, type)) {
                return efficiency;
            }
        }
        return Math.max(super.getDestroySpeed(stack, state), 1.0F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            CompoundNBT tags = stack.getTag();
            if (tags == null) {
                tags = new CompoundNBT();
                stack.setTag(tags);
            }
            tags.putBoolean("destroyer", !tags.getBoolean("destroyer"));
            player.swingArm(hand);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        if (stack.getTag() != null && stack.getTag().getBoolean("destroyer")) {
            BlockRayTraceResult traceResult = RayTracer.retrace(player, 10, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE);
            if (traceResult != null) {
                breakOtherBlock(player, stack, pos, traceResult.getFace());
            }
        }
        return false;
    }

    public void breakOtherBlock(PlayerEntity player, ItemStack stack, BlockPos pos, Direction sideHit) {

        World world = player.world;
        BlockState state = world.getBlockState(pos);
        Material mat = state.getMaterial();
        if (!InfinityPickaxe.MATERIALS.contains(mat)) {
            return;
        }

        if (state.getBlock().isAir(state, world, pos)) {
            return;
        }

        boolean doY = sideHit.getAxis() != Direction.Axis.Y;

        int range = 8;
        BlockPos minOffset = new BlockPos(-range, doY ? -1 : -range, -range);
        BlockPos maxOffset = new BlockPos(range, doY ? range * 2 - 2 : range, range);

        ToolHelper.aoeBlocks(player, stack, world, pos, minOffset, maxOffset, null, ToolHelper.materialsShovel, true, true);

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
