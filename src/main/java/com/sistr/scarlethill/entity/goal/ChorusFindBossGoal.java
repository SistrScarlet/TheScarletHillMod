package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.IChorusable;
import com.sistr.scarlethill.entity.IGroupable;
import com.sistr.scarlethill.entity.IPlaySoundController;
import com.sistr.scarlethill.entity.SoundData;
import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;

public class ChorusFindBossGoal<E extends MobEntity & IChorusable & IGroupable<E> & IPlaySoundController> extends Goal {
    private final E mob;
    private final int chorusLevel;
    private LivingEntity prevAttacker;


    public ChorusFindBossGoal(E mob, int chorusLevel) {
        this.mob = mob;
        this.chorusLevel = chorusLevel;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity target = this.mob.getAttackTarget();
        if (this.mob.getChorusLevel() < this.chorusLevel && this.prevAttacker != target) {
            this.prevAttacker = target;
            return target != null && !target.isNonBoss() && target.isAlive();
        }
        this.prevAttacker = target;
        return false;
    }

    @Override
    public void startExecuting() {
        this.mob.playSound(Registration.CRIMSONIAN_SIG.get(), 2.0F, 1.0F);
        this.mob.addPlaySound(new SoundData(this.mob, Registration.CRIMSONIAN_SCARLET.get(), 2.0F, 1.0F), this.mob.ticksExisted, 5);
        //メンバー全員コーラスさせる
        this.mob.getGroupController().ifPresent(controller -> controller.getMembers().forEach(id -> {
            Entity member = ((ServerWorld) this.mob.world).getEntityByUuid(id);
            if (member instanceof IChorusable && member instanceof MobEntity) {
                ((MobEntity) member).setAttackTarget(this.mob.getAttackTarget());
                ((IChorusable) member).setChorus(20, this.chorusLevel);
            }
        }));
    }
}
