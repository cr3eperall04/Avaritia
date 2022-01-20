package com.cr3eperall.avaritiaalpha.items.tools;

import codechicken.lib.raytracer.RayTracer;
import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.config.Config;
import com.cr3eperall.avaritiaalpha.entity.ImmortalItemEntity;
import com.cr3eperall.avaritiaalpha.handler.AvaritiaAlphaEventHandler;
import com.cr3eperall.avaritiaalpha.util.ToolHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.function.Consumer;

public class InfinityAxe extends AxeItem {
    public InfinityAxe() {
        super(InfinityMaterial.get(), 0, -3F, new Properties().group(AvaritiaAlpha.setup.itemGroup).rarity(InfinityMaterial.COSMIC_RARITY));
        setRegistryName("infinity_axe");
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (super.getDestroySpeed(stack, state) > 1.0F || state.getMaterial() == Material.LEAVES) {
            return efficiency;
        }
        return Math.max(super.getDestroySpeed(stack, state), 4.0F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            player.swingArm(hand);

            int range = Config.INFINITYAXE_RANGE.get();
            BlockPos min = new BlockPos(-range, -3, -range);
            BlockPos max = new BlockPos(range, range * 2 - 3, range);
            ToolHelper.aoeBlocks(player, stack, world, player.getPosition(), min, max, null, ToolHelper.materialsAxe, false);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {

        RayTraceResult traceResult = RayTracer.retrace(player, 10, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE);
        if (traceResult != null) {
            breakOtherBlock(player, stack, pos);
        }
        return false;
    }

    public void breakOtherBlock(PlayerEntity player, ItemStack stack, BlockPos pos) {
        if (player.isSneaking()) {
            return;
        }
        AvaritiaAlphaEventHandler.startCrawlerTask(player.world, player, stack, pos, 32, false, true, new HashSet<>());
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

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        return true;
    }
}
