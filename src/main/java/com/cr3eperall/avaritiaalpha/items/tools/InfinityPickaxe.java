package com.cr3eperall.avaritiaalpha.items.tools;

import codechicken.lib.math.MathHelper;
import codechicken.lib.raytracer.RayTracer;
import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.entity.ImmortalItemEntity;
import com.cr3eperall.avaritiaalpha.handler.AvaritiaAlphaEventHandler;
import com.cr3eperall.avaritiaalpha.util.ToolHelper;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Set;

public class InfinityPickaxe extends PickaxeItem {

    public static final Set<Material> MATERIALS = Sets.newHashSet(Material.ROCK, Material.IRON, Material.ICE, Material.GLASS, Material.PISTON, Material.ANVIL, Material.ORGANIC, Material.EARTH, Material.SAND, Material.SNOW, Material.SNOW_BLOCK, Material.CLAY);


    public InfinityPickaxe() {
        super(InfinityMaterial.get(), 0, 6, new Properties().rarity(InfinityMaterial.COSMIC_RARITY).group(AvaritiaAlpha.setup.itemGroup));
        setRegistryName("infinity_pickaxe");
        addPropertyOverride(new ResourceLocation(AvaritiaAlpha.MOD_ID + ":" + "hammer"), new IItemPropertyGetter() {
            @Override
            public float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity holder) {
                return (stack.getTag() != null && stack.getTag().getBoolean("hammer")) ? 1 : 0;
            }
        });
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            ItemStack item=new ItemStack(this);
            item.addEnchantment(Enchantments.FORTUNE,10);
            items.add(item);
        }
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (stack.getTag() != null && stack.getTag().getBoolean("hammer")) {
            return 5.0F;
        }
        for (ToolType type : getToolTypes(stack)) {
            if (state.getBlock().isToolEffective(state, type)) {
                return efficiency;
            }
        }
        return Math.max(super.getDestroySpeed(stack, state), 6.0F);
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
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack) < 10) {
                stack.addEnchantment(Enchantments.FORTUNE, 10);
            }
            tags.putBoolean("hammer", !tags.getBoolean("hammer"));
            player.swingArm(hand);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity victim, LivingEntity player) {
        if (stack.getTag() != null) {
            if (stack.getTag().getBoolean("hammer")) {
                if (!(victim instanceof PlayerEntity && AvaritiaAlphaEventHandler.isInfinite((PlayerEntity) victim))) {
                    int i = 10;
                    victim.addVelocity(-MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F) * i * 0.5F, 2.0D, MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0F) * i * 0.5F);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        if (stack.getTag() != null && stack.getTag().getBoolean("hammer")) {
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
        if (!MATERIALS.contains(mat)) {
            return;
        }

        if (state.getBlock().isAir(state, world, pos)) {
            return;
        }

        boolean doY = sideHit.getAxis() != Direction.Axis.Y;

        int range = 8;
        BlockPos minOffset = new BlockPos(-range, doY ? -1 : -range, -range);
        BlockPos maxOffset = new BlockPos(range, doY ? range * 2 - 2 : range, range);

        ToolHelper.aoeBlocks(player, stack, world, pos, minOffset, maxOffset, null, MATERIALS, true, false);

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
