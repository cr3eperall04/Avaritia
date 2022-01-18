package com.cr3eperall.avaritiaalpha.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ImmortalItemEntity extends ItemEntity {
    public ImmortalItemEntity(World world, Entity original, ItemStack stack) {
        this(world,original.posX,original.posY,original.posZ,stack);
        setPickupDelay(20);
        setMotion(original.getMotion());
        setItem(stack);
    }

    public ImmortalItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
        setItem(stack);
    }

    public ImmortalItemEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void dealFireDamage(int amount) {
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getDamageType().equals(DamageSource.OUT_OF_WORLD.getDamageType())){
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
    }
}
