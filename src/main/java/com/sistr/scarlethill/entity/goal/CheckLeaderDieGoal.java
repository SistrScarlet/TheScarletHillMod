package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.IGroupController;
import com.sistr.scarlethill.entity.IGroupable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.Optional;

//リーダーが死んだ場合、グループの跡を継ぐ
public class CheckLeaderDieGoal<E extends MobEntity & IGroupable<E>> extends Goal {
    private final E mob;

    public CheckLeaderDieGoal(E mob) {
        this.mob = mob;
    }

    @Override
    public boolean shouldExecute() {
        Optional<IGroupController<E>> optional = this.mob.getGroupController();
        if (!optional.isPresent()) {
            return false;
        }
        Optional<E> leader = optional.get().getLeader();
        return leader.isPresent() && !leader.get().isAlive();
    }

    @Override
    public void startExecuting() {
        Optional<IGroupController<E>> controller = this.mob.getGroupController();
        if (!controller.isPresent()) {
            return;
        }
        controller.get().setLeaderId(this.mob.getUniqueID());
    }
}
