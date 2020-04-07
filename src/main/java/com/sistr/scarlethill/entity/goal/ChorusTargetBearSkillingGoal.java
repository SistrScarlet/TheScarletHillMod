package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.IChorusable;
import com.sistr.scarlethill.entity.IGroupable;
import com.sistr.scarlethill.entity.ScarletBearEntity;
import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;

//連呼を避けるため、!skilling -> skillingの時のみ反応する
public class ChorusTargetBearSkillingGoal<E extends MobEntity & IGroupable<E> & IChorusable> extends Goal {
    private final E mob;
    private final int chorusLevel;
    private boolean prevSkilling;

    public ChorusTargetBearSkillingGoal(E mob, int chorusLevel) {
        this.mob = mob;
        this.chorusLevel = chorusLevel;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity target = this.mob.getAttackTarget();
        if (!(target instanceof ScarletBearEntity)) {
            return false;
        }
        if (target.isAlive() && ((ScarletBearEntity) target).isStanding()) {
            boolean canExecute = !this.prevSkilling;
            this.prevSkilling = true;
            return this.mob.getChorusLevel() < this.chorusLevel && canExecute;
        }
        this.prevSkilling = false;
        return false;
    }

    @Override
    public void startExecuting() {
        this.mob.playSound(Registration.CRIMSONIAN_ZAN.get(), 1.0F, 1.0F);
        //メンバー全員をコーラスさせる
        this.mob.getGroupController().ifPresent(controller -> controller.getMembers().forEach(id -> {
            Entity member = ((ServerWorld) this.mob.world).getEntityByUuid(id);
            if (member instanceof IChorusable && member instanceof MobEntity) {
                ((MobEntity) member).setAttackTarget(this.mob.getAttackTarget());
                ((IChorusable) member).setChorus(10, this.chorusLevel);
            }
        }));
    }
}
