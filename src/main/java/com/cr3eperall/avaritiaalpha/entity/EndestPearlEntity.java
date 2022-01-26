package com.cr3eperall.avaritiaalpha.entity;

import codechicken.lib.vec.Vector3;
import com.cr3eperall.avaritiaalpha.items.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EndestPearlEntity extends ThrowableEntity implements IRendersAsItem {

    public EndestPearlEntity(EntityType<? extends ThrowableEntity> entityType, double x, double y, double z, World world) {
        super(entityType, x, y, z, world);
    }

    public EndestPearlEntity(EntityType<? extends ThrowableEntity> entityType, LivingEntity ent, World world) {
        super(entityType,ent,world);
    }

    public EndestPearlEntity(EntityType<? extends ThrowableEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected void onImpact(RayTraceResult pos) {
        if (pos.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult)pos).getEntity();
            if (entity == this.getThrower()) {
                return;
            }

            entity.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
        }

        for (int i = 0; i < 100; ++i) {
            world.addParticle(ParticleTypes.PORTAL, posX, posY, posZ, rand.nextGaussian() * 3, rand.nextGaussian() * 3, rand.nextGaussian() * 3);
        }

        if (!world.isRemote) {
            if (pos.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockpos = ((BlockRayTraceResult)pos);
                Entity ent = new GapingVoidEntity(ModEntities.gapingVoidEntityEntityType,world);
                Direction dir = blockpos.getFace();
                Vector3 offset = Vector3.ZERO.copy();
                if (blockpos.getFace() != null) {
                    offset = new Vector3(dir.getXOffset(), dir.getYOffset(), dir.getZOffset());
                }
                ent.setLocationAndAngles(posX + offset.x * 0.25, posY + offset.y * 0.25, posZ + offset.z * 0.25, rotationYaw, 0.0F);
                world.addEntity(ent);
            }

            remove();
        }
    }

    @Override
    protected void registerData() {
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(ModItems.endest_pearl);
    }
}
