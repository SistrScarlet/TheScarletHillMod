package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.IChorusable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class ChorusLeaveGoal<E extends CreatureEntity & IChorusable> extends Goal {

    private final E mob;
    private final float leaveSpeed;
    protected final PathNavigator navigation;
    private LivingEntity avoidTarget;
    private int timeToRecalcPath;
    private final int leaveChorusLevel;
    private final float leaveDistSq;
    private int timer;
    private final int leaveTime;

    public ChorusLeaveGoal(E mob, float leaveSpeed, int leaveChorusLevel, float leaveDist, int leaveTime) {
        this.mob = mob;
        this.leaveSpeed = leaveSpeed;
        this.navigation = mob.getNavigator();
        this.leaveChorusLevel = leaveChorusLevel;
        this.leaveDistSq = leaveDist * leaveDist;
        this.leaveTime = leaveTime;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean shouldExecute() {
        LivingEntity target = this.mob.getAttackTarget();
        if (target == null) {
            return false;
        }
        if (this.leaveChorusLevel <= this.mob.getChorusLevel() && this.mob.getDistanceSq(target) < this.leaveDistSq) {
            this.avoidTarget = target;
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.timer < this.leaveTime && this.mob.getDistanceSq(this.avoidTarget) < this.leaveDistSq;
    }

    @Override
    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.timer = 0;
    }

    @Override
    public void tick() {
        this.timer++;
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
