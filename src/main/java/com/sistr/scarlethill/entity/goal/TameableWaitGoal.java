package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.ITameable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class TameableWaitGoal<E extends MobEntity & ITameable> extends Goal {
    private final E mob;

    public TameableWaitGoal(E mob) {
        this.mob = mob;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean shouldExecute() {
        if (!this.mob.getOwnerId().isPresent() || this.mob.isInWaterOrBubbleColumn() || !this.mob.onGround) {
            return false;
        }
        return this.mob.isWait();
    }

    public void startExecuting() {
        this.mob.getNavigator().clearPath();
        this.mob.setWait(true);
    }

    public void resetTask() {
        this.mob.setWait(false);
    }
}
