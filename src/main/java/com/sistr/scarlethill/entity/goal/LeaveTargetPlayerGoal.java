package com.sistr.scarlethill.entity.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class LeaveTargetPlayerGoal extends Goal {

    private final CreatureEntity mob;
    private final float leaveSpeed;
    private final float minDist;
    private final float maxDist;
    protected final PathNavigator navigation;
    private PlayerEntity avoidTarget;
    private int timeToRecalcPath;

    public LeaveTargetPlayerGoal(CreatureEntity mob, float leaveSpeed, float minDist, float maxDist) {
        this.mob = mob;
        this.leaveSpeed = leaveSpeed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.navigation = mob.getNavigator();
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (this.mob.ticksExisted % 20 != 0) {
            return false;
        }
        LivingEntity target = this.mob.getAttackTarget();
        if (!(target instanceof PlayerEntity)) {
            return false;
        }
        double distanceSq = this.mob.getDistanceSq(target);
        if (distanceSq < this.minDist * this.minDist) {
            if (!this.mob.getEntitySenses().canSee(target)) {
                return false;
            }
            Vec3d lookAtVec = target.getLookVec();
            double distance = MathHelper.sqrt(distanceSq);
            lookAtVec = lookAtVec.scale(distance);
            //敵プレイヤーの視線の先が、自身の周囲minDist以内だった場合
            //結構簡易な処理
            if (this.mob.getBoundingBox().grow(this.minDist).contains(target.getPositionVec().add(lookAtVec))) {
                this.avoidTarget = (PlayerEntity) target;
            }
        }
        return this.avoidTarget != null;
    }

    public boolean shouldContinueExecuting() {
        return this.mob.getDistanceSq(this.avoidTarget) < this.maxDist * this.maxDist;
    }

    @Override
    public void startExecuting() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            Vec3d farthestPos = null;
            for (int i = 0; i < 10; ++i) {
                //この位置ランダム生成は処理時間が1ms未満の軽い処理っぽい
                Vec3d randomPos = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.mob, 16, 7, this.avoidTarget.getPositionVec());
                if (randomPos == null) continue;
                if (farthestPos == null || this.avoidTarget.getDistanceSq(farthestPos) < this.avoidTarget.getDistanceSq(randomPos)) {
                    farthestPos = randomPos;
                }
            }
            if (farthestPos != null) {
                this.navigation.tryMoveToXYZ(farthestPos.x, farthestPos.y, farthestPos.z, this.leaveSpeed);
            }
        }
    }

    public void resetTask() {
        this.avoidTarget = null;
        this.navigation.clearPath();
    }

}
