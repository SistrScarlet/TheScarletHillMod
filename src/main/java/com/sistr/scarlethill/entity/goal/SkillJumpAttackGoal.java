package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.util.EffectUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;

public class SkillJumpAttackGoal extends SkillAttackGoal {
    private LivingEntity jumpTarget;
    private final float jumpMotionY;

    public SkillJumpAttackGoal(MobEntity jumpingEntity, int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float jumpMotionY) {
        super(jumpingEntity, startupLength, actionLength, freezeLength, chance, damage, range, 2, 16);
        this.jumpMotionY = jumpMotionY;
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    public boolean shouldStart() {
        if (this.goalOwner.isBeingRidden() && !this.goalOwner.onGround) {
            return false;
        }

        this.jumpTarget = this.goalOwner.getAttackTarget();
        if (this.jumpTarget == null) {
            return false;
        }

        //nTickに一回処理。おそらく最初に距離で判定するよりも、こっちの方が軽いはず
        if (this.goalOwner.getRNG().nextInt(this.chance) == 0) {
            return checkTargetDistance();
        }

        return false;

    }

    @Override
    protected void actionStart() {
        Vec3d horizonMotion = new Vec3d(this.jumpTarget.getPosX() - this.goalOwner.getPosX(), 0.0D, this.jumpTarget.getPosZ() - this.goalOwner.getPosZ());
        Vec3d jumperMotion = this.goalOwner.getMotion();
        horizonMotion = horizonMotion.scale(0.18D).add(jumperMotion.scale(0.5D));

        this.goalOwner.setMotion(horizonMotion.x, this.jumpMotionY, horizonMotion.z);
    }

    @Override
    protected void actionTick() {
        //着地したら硬直開始
        if (this.timer > 10 && this.goalOwner.onGround) {
            setStatus(SkillStatus.FREEZE);
        }
    }

    @Override
    protected void freezeStart() {
        World world = this.goalOwner.world;
        //地面に接地してないとダメージは発生しない
        world.getEntitiesInAABBexcluding(this.goalOwner, this.goalOwner.getBoundingBox().grow(this.range, 2D, this.range), (entity ->
                entity.onGround && entity.isAlive() && entity.canBeCollidedWith() && !entity.isSpectator())).forEach((entity) -> {
            float damage = this.damage - (float) this.goalOwner.getDistanceSq(entity);
            if (damage > 1) {
                entity.attackEntityFrom(DamageSource.causeMobDamage(this.goalOwner), damage);
                if (entity instanceof LivingEntity) {
                    Vec3d toTargetVec = this.goalOwner.getPositionVec().subtract(entity.getPositionVec()).normalize();
                    ((LivingEntity) entity).knockBack(this.goalOwner, 0.8F, toTargetVec.x, toTargetVec.z);
                }
            }
        });
        world.playSound(null, this.goalOwner.getPosX(), this.goalOwner.getPosY(), this.goalOwner.getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 1.0F, 0.8F + 0.2F * this.goalOwner.getRNG().nextFloat());
        EffectUtil.spawnParticleBox((ServerWorld) world, ParticleTypes.POOF, this.goalOwner.getPosX(), this.goalOwner.getPosY(), this.goalOwner.getPosZ(), 50, this.range);
    }
}
