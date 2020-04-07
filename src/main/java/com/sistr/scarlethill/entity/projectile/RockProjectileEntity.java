package com.sistr.scarlethill.entity.projectile;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class RockProjectileEntity extends AbstractProjectile {
    float explosionRadius;

    public RockProjectileEntity(EntityType<? extends RockProjectileEntity> type, World world) {
        super(type, world);
    }

    public RockProjectileEntity(LivingEntity shooter, World world) {
        super(Registration.ROCK_PROJECTILE.get(), shooter, world);
    }

    @Override
    public float getVelocity() {
        return 0.7F;
    }

    @Override
    public float getGravity() {
        return 0.025F;
    }

    public void setExplosionRadius(float radius) {
        this.explosionRadius = radius;
    }

    public float getExplosionRadius() {
        return this.explosionRadius;
    }

    @Override
    protected void onHit(RayTraceResult result) {
        Vec3d hitPos = result.getHitVec();
        float radius = getExplosionRadius();
        AxisAlignedBB bb = new AxisAlignedBB(hitPos.getX() + radius, hitPos.getY() + radius, hitPos.getZ() + radius,
                hitPos.getX() - radius, hitPos.getY() - radius, hitPos.getZ() - radius);
        List<Entity> aroundEntity = this.world.getEntitiesInAABBexcluding(this, bb, (entity) ->
                !entity.isSpectator() && entity.isAlive() && entity.canBeCollidedWith()
                        && (entity != this.getShooter()) && entity.getDistanceSq(this) < radius * radius);
        aroundEntity.forEach(entity -> {
            float distance = this.getDistance(entity);
            entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getShooter()), this.getDamage() - distance * 2);
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).knockBack(this, 3 * (1 - distance / radius), this.getPosX() - entity.getPosX(), this.getPosZ() - entity.getPosZ());
            }
        });
        this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0, 0);
        this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.5F);
        this.remove();
    }
}
