package com.sistr.scarlethill.entity.projectile;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

//跳ね返せる
//着地点にドラゴンブレスのようなものを発生させる。
//触れるとダメージを受けるが、紅の加護を付けていると逆に加護の時間を延ばす
public class ScarletProjectileEntity extends AbstractProjectile implements IRendersAsItem {

    public ScarletProjectileEntity(EntityType<? extends AbstractProjectile> type, World world) {
        super(type, world);
    }

    public ScarletProjectileEntity(LivingEntity shooter, World world) {
        super(Registration.SCARLET_PROJECTILE.get(), shooter, world);
    }

    @Override
    public float getGravity() {
        return 0;
    }

    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        Entity shooter = this.getShooter();
        if (shooter == null) {
            return;
        }
        if (result.getType() != RayTraceResult.Type.ENTITY || !((EntityRayTraceResult) result).getEntity().isEntityEqual(shooter)) {
            if (!this.world.isRemote) {
                List<LivingEntity> aroundEntity = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(4.0D, 2.0D, 4.0D));
                AreaEffectCloudEntity areaEffect = new AreaEffectCloudEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ());
                areaEffect.setOwner((LivingEntity) shooter);
                areaEffect.setParticleData(ParticleTypes.FLAME);
                areaEffect.setRadius(3.0F);
                areaEffect.setRadiusOnUse(-0.5F);
                areaEffect.setWaitTime(10);
                areaEffect.setRadiusPerTick(-areaEffect.getRadius() / areaEffect.getDuration());
                areaEffect.addEffect(new EffectInstance(Registration.SCARLET_BLAZING_EFFECT.get(), 20, 0));
                if (!aroundEntity.isEmpty()) {
                    for (LivingEntity livingentity : aroundEntity) {
                        double distanceSq = this.getDistanceSq(livingentity);
                        if (distanceSq < 16.0D) {
                            areaEffect.setPosition(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ());
                            break;
                        }
                    }
                }

                //this.world.playEvent(2006, new BlockPos(this), 0);
                this.world.playSound(null, this.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 2, 1.5F);
                this.world.addEntity(areaEffect);
                this.remove();
            }

        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    public float getCollisionBorderSize() {
        return 1.0F;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.markVelocityChanged();
            if (source.getTrueSource() != null) {
                Vec3d vec3d = source.getTrueSource().getLookVec();
                this.setMotion(vec3d);
                if (source.getTrueSource() instanceof LivingEntity) {
                    setShooter(source.getTrueSource());
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public float getBrightness() {
        return 1.0F;
    }

    //todo 緋色の炎の描画
    @Override
    public ItemStack getItem() {
        return Registration.SCARLET_GEM_ITEM.get().getDefaultInstance();
    }
}
