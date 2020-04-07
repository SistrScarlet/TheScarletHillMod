package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.IGroupController;
import com.sistr.scarlethill.entity.IGroupable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

//リーダー専用
//編隊に死亡者が出た場合メンバーから外す
public class CheckMemberDieGoal<E extends MobEntity & IGroupable<E>> extends Goal {
    private final E mob;

    public CheckMemberDieGoal(E mob) {
        this.mob = mob;
    }

    @Override
    public boolean shouldExecute() {
        if (this.mob.ticksExisted % 20 != 0) {
            return false;
        }
        Optional<IGroupController<E>> optional = this.mob.getGroupController();
        if (!optional.isPresent()) {
            return false;
        }
        IGroupController<E> controller = optional.get();
        return controller.isLeader(this.mob);
    }

    @Override
    public void startExecuting() {
        this.mob.getGroupController().ifPresent(controller -> controller.getMembers().stream().filter(id -> {
            Entity member = ((ServerWorld) this.mob.world).getEntityByUuid(id);
            return member == null;
        }).forEach(controller::removeMember));
    }
}
