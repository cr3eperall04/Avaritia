package com.cr3eperall.avaritiaalpha.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Random;

public class HeavenArrowEntity extends ArrowEntity {

    public boolean impacted=false;


    public HeavenArrowEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public HeavenArrowEntity(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

    public HeavenArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        rotationPitch = 0;
        rotationYaw = 0;
        super.tick();
        if (!impacted) {
            try {
                if (inGround) {
                    impacted = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (impacted) {
                if (!world.isRemote) {
                    barrage();
                }
            }
        }

        if (inGround && timeInGround >= 100) {
            remove();
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("impacted", impacted);
    }

    @Override
    public void read(CompoundNBT compound) {
        impacted=compound.getBoolean("impacted");
        super.read(compound);
    }


    public void barrage() {//TODO, this logic may be borked.
        Random randy = getEntityWorld().rand;
        Vec3d vec=getPositionVec();
        for (int i = 0; i < 10; i++) {

            double angle = randy.nextDouble() * 2 * Math.PI;
            double dist = randy.nextGaussian() * 0.5;

            double x = Math.sin(angle) * dist + vec.x;
            double z = Math.cos(angle) * dist + vec.y;
            double y = vec.z + 25.0;

            double dangle = randy.nextDouble() * 2 * Math.PI;
            double ddist = randy.nextDouble() * 0.35;
            double dx = Math.sin(dangle) * ddist;
            double dz = Math.cos(dangle) * ddist;

            HeavenSubArrowEntity arrow = new HeavenSubArrowEntity(ModEntities.heavenSubArrowEntityEntityType,world);
            arrow.setPosition(x,y,z);
            arrow.shootingEntity = shootingEntity;
            arrow.addVelocity(dx, -(randy.nextDouble() * 1.85 + 0.15), dz);
            arrow.setDamage(getDamage());
            arrow.setIsCritical(true);
            arrow.pickupStatus = pickupStatus;

            world.addEntity(arrow);
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
