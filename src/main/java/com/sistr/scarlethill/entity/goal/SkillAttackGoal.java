package com.sistr.scarlethill.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.Vec3d;

//技を使った攻撃の抽象クラス。
//非攻撃スキルを想定して分けている
abstract public class SkillAttackGoal extends SkillGoal {
    protected final int chance;
    protected final float damage;
    protected final float range;
    protected final float minDistance;
    protected final float maxDistance;

    public SkillAttackGoal(MobEntity attacker, int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float minDistance, float maxDistance) {
        super(attacker, startupLength, actionLength, freezeLength);
        this.chance = chance;
        this.damage = damage;
        this.range = range;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    protected boolean checkTargetDistance() {
        LivingEntity target = this.goalOwner.getAttackTarget();
        if (target == null) return false;
        return checkDistance(target.getPositionVec());
    }

    protected boolean checkDistance(Vec3d targetPos) {
        if (targetPos == null) return false;
        float distanceSq = (float) this.goalOwner.getDistanceSq(targetPos);
        return this.minDistance * this.minDistance < distanceSq && distanceSq < this.maxDistance * this.maxDistance;
    }
}
