package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.IChorusable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;

public class ChorusGoal<E extends Entity & IChorusable> extends Goal {
    private final E mob;
    private boolean prevResult = false;

    public ChorusGoal(E mob) {
        this.mob = mob;
    }

    @Override
    public boolean shouldExecute() {
        boolean isCool = this.mob.isCoolTime();
        if (this.prevResult && !isCool) {
            this.prevResult = false;
            return true;
        }
        this.prevResult = isCool;
        return false;
    }

    @Override
    public void startExecuting() {
        this.mob.playSound(this.mob.getChorus(), 1.0F, 0.9F + this.mob.world.rand.nextFloat() * 0.2F);
        this.mob.resetLevel();
    }
}
