package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.*;
import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class ChorusChangeLeaderGoal<E extends MobEntity & IChorusable & IGroupable<E> & IPlaySoundController> extends Goal {
    private final E mob;
    private final int chorusLevel;
    @Nullable
    private UUID prevLeader;

    public ChorusChangeLeaderGoal(E mob, int chorusLevel) {
        this.mob = mob;
        this.chorusLevel = chorusLevel;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        Optional<IGroupController<E>> controller = this.mob.getGroupController();
        if (controller.isPresent()) {
            Optional<UUID> optionalLeaderId = controller.get().getLeaderId();
            if (optionalLeaderId.isPresent()) {
                UUID leaderId = optionalLeaderId.get();
                if (leaderId != this.prevLeader && this.mob.getUniqueID() == leaderId && this.mob.getChorusLevel() < this.chorusLevel) {
                    this.prevLeader = leaderId;
                    return true;
                }
                this.prevLeader = leaderId;
                return false;
            }
        }
        this.prevLeader = null;
        return false;
    }

    @Override
    public void startExecuting() {
        this.mob.playSound(Registration.CRIMSONIAN_GA.get(), 1.0F, 1.0F);
        this.mob.addPlaySound(new SoundData(this.mob, Registration.CRIMSONIAN_MEL.get(), 1.0F, 1.0F), this.mob.ticksExisted, 5);
        //メンバー全員コーラスさせる
        this.mob.getGroupController().ifPresent(controller -> controller.getMembers().forEach(id -> {
            Entity member = ((ServerWorld) this.mob.world).getEntityByUuid(id);
            if (member instanceof IChorusable && member instanceof MobEntity) {
                ((IChorusable) member).setChorus(20, this.chorusLevel);
            }
        }));
    }
}
