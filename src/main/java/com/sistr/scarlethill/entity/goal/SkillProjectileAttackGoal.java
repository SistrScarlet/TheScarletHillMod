package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.projectile.AbstractProjectile;
import com.sistr.scarlethill.util.MathUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public abstract class SkillProjectileAttackGoal<T extends AbstractProjectile> extends SkillAttackGoal {
    protected final T projectile = this.createProjectile();

    public SkillProjectileAttackGoal(MobEntity attacker, int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float minDistance, float maxDistance) {
        super(attacker, startupLength, actionLength, freezeLength, chance, damage, range, minDistance, maxDistance);
    }

    @Override
    protected boolean shouldStart() {
        if (this.goalOwner.getRNG().nextInt(this.chance) != 0) return false;
        LivingEntity target = this.goalOwner.getAttackTarget();
        if (target != null) {
            return checkTargetDistance();
        }
        return false;
    }

    @Override
    protected void actionStart() {
        Optional<Vec3d> optionalTargetPos = this.getTargetPos();
        if (!optionalTargetPos.isPresent()) {
            this.setStatus(SkillStatus.FREEZE);
            return;
        }
        Vec3d targetPos = optionalTargetPos.get();
        Vec3d shootPos = this.getShootPos();

        Vec3d angle = getAngle(shootPos, targetPos);
        shooting(shootPos, angle);
    }

    //やり方が泥臭い
    protected Vec3d getAngle(Vec3d shootPos, Vec3d targetPos) {
        float velocity = this.projectile.getVelocity();
        float drag = this.projectile.getAirDrag();
        float gravity = this.projectile.getGravity();
        double distanceSq;
        double nearDistanceSq = -1;
        int nearAngle = -1;
        Vec3d baseAngle = targetPos.subtract(shootPos).normalize();
        //弾道の終点と対象との距離が1以下になるまで角度を変えて試行
        getAngle:
        for (int testAngle = -90; testAngle < 90; testAngle++) {
            Vec3d motion = MathUtil.rotatePitch(baseAngle, testAngle).normalize().scale(velocity);
            Vec3d checkPoint = shootPos;
            double inLoopNearDistanceSq = -1;
            //ある角度から発射された弾道の計算
            //ループ回数=ヒットまでの所要tick数
            for (int tick = 0; tick < 200; tick++) {
                distanceSq = checkPoint.squareDistanceTo(targetPos);
                //ある角度での対象との距離が一定以下になった場合は終了
                if (distanceSq < 0.5) {
                    nearAngle = testAngle;
                    break getAngle;
                }
                //接近している場合
                if (inLoopNearDistanceSq < 0 || distanceSq < inLoopNearDistanceSq) {
                    inLoopNearDistanceSq = distanceSq;
                    //今までの試行での至近距離が初期値であるか、distanceSqがより近い場合は、最近角度を更新
                    if (nearDistanceSq < 0 || distanceSq < nearDistanceSq) {
                        nearDistanceSq = distanceSq;
                        nearAngle = testAngle;
                    }
                } else {//targetPosから遠ざかっている場合
                    break;
                }

                //次回のチェックに備える
                checkPoint = checkPoint.add(motion);
                motion = motion.scale(drag).add(0, -gravity, 0);
            }
        }
        return MathUtil.rotatePitch(baseAngle, nearAngle);
    }

    protected void shooting(Vec3d shootPos, Vec3d angle) {
        T newProjectile = createProjectile();
        newProjectile.setPosition(shootPos.getX(), shootPos.getY(), shootPos.getZ());
        newProjectile.setDamage(this.damage);
        newProjectile.shoot(angle.x, angle.y, angle.z, addVelocity(), addBlurAngle());
        this.goalOwner.world.addEntity(newProjectile);
    }

    protected float addVelocity() {
        return 0;
    }

    protected float addBlurAngle() {
        return 0;
    }

    protected abstract T createProjectile();

    public Vec3d getShootPos() {
        return new Vec3d(this.goalOwner.getPosX(), this.goalOwner.getPosYEye(), this.goalOwner.getPosZ());
    }

    public Optional<Vec3d> getTargetPos() {
        LivingEntity target = this.goalOwner.getAttackTarget();
        return target == null ? Optional.empty() : Optional.of(new Vec3d(target.getPosX(), target.getPosYEye(), target.getPosZ()));
    }
}
