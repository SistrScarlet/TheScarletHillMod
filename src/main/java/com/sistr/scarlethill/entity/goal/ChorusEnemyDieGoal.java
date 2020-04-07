package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.IChorusable;
import com.sistr.scarlethill.entity.IGroupable;
import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;

public class ChorusEnemyDieGoal<E extends MobEntity & IGroupable<E> & IChorusable> extends Goal {
    private final E mob;
    private final int chorusLevel;
    private LivingEntity prevTarget;

    public ChorusEnemyDieGoal(E mob, int chorusLevel) {
        this.mob = mob;
        this.chorusLevel = chorusLevel;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity target = this.mob.getAttackTarget();
        if (target != null && !target.isAlive()) {
            this.prevTarget = target;
            return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        this.mob.playSound(Registration.CRIMSONIAN_NID.get(), 1.0F, 1.0F);
        this.mob.setAttackTarget(null);
        //ターゲットが同じメンバー全員をコーラスさせる
        this.mob.getGroupController().ifPresent(controller -> controller.getMembers().forEach(id -> {
            Entity member = ((ServerWorld) this.mob.world).getEntityByUuid(id);
            if (member instanceof IChorusable && member instanceof MobEntity && ((MobEntity) member).getAttackTarget() == this.prevTarget) {
                ((MobEntity) member).setAttackTarget(null);
                ((IChorusable) member).setChorus(20, this.chorusLevel);
            }
        }));
    }
}
