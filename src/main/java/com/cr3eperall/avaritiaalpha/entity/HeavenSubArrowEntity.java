package com.cr3eperall.avaritiaalpha.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class HeavenSubArrowEntity extends ArrowEntity {
    public HeavenSubArrowEntity(EntityType<? extends ArrowEntity> p_i50172_1_, World p_i50172_2_) {
        super(p_i50172_1_, p_i50172_2_);
    }

    public HeavenSubArrowEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public HeavenSubArrowEntity(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

    @Override
    public void tick() {
        rotationPitch = 0;
        rotationYaw = 0;
        super.tick();
        if (inGround && timeInGround >= 20) {
            remove();
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
