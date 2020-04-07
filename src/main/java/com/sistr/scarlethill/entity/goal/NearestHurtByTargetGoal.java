package com.sistr.scarlethill.entity.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;

//攻撃してきたやつが、今攻撃してるやつより近かったら、そっちに敵対する
public class NearestHurtByTargetGoal extends HurtByTargetGoal {

    public NearestHurtByTargetGoal(CreatureEntity creatureIn, Class<?>... p_i50317_2_) {
        super(creatureIn, p_i50317_2_);
    }

    //trueを返すとRevengeTargetがAttackTargetにされる
    @Override
    public boolean shouldExecute() {
        if (super.shouldExecute()) {
            //復讐対象が居ない場合false、居て攻撃対象が居ないならtrue、どちらも居る場合は距離が近ければtrue
            LivingEntity revengeTarget = this.goalOwner.getRevengeTarget();
            if (revengeTarget == null) return false;
            LivingEntity attackTarget = this.goalOwner.getAttackTarget();
            if (attackTarget == null) return true;
            if (attackTarget == revengeTarget) return false;
            return this.goalOwner.getDistanceSq(revengeTarget) < this.goalOwner.getDistanceSq(attackTarget);
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        LivingEntity revenge = this.goalOwner.getRevengeTarget();
        //復讐対象が存在してなおかつ現在のターゲットと異なる場合
        if (revenge != null && revenge != this.target) {
            return this.shouldExecute();
        }
        return super.shouldContinueExecuting();
    }
}
