package com.sistr.scarlethill.entity.goal;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.BowItem;
import net.minecraft.util.Hand;

import java.util.EnumSet;

//カスタム版RangedBowAttackGoal
public class NonMonsterRangedBowAttackGoal<E extends MobEntity & IRangedAttackMob> extends Goal {
    private final E mob;
    private final float maxAttackDistance;
    private final float strafeSpeed;
    private final float forwardSpeed;
    private int attackCooldown;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingBackwards;
    private float strafe;

    public NonMonsterRangedBowAttackGoal(E mob, int attackCooldownIn, float maxAttackDistanceIn, float strafeSpeed, float forwardSpeed) {
        this.mob = mob;
        this.attackCooldown = attackCooldownIn;
        this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        this.strafeSpeed = strafeSpeed;
        this.forwardSpeed = forwardSpeed;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setAttackCooldown(int attackCooldownIn) {
        this.attackCooldown = attackCooldownIn;
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute() {
        if (isBowInMainhand()) {
            return this.mob.getAttackTarget() != null;
        }
        return false;
    }

    protected boolean isBowInMainhand() {
        return this.mob.getHeldItemMainhand().getItem() instanceof BowItem || this.mob.getHeldItemOffhand().getItem() instanceof BowItem;
    }

    public void startExecuting() {
        this.mob.getNavigator().clearPath();
        this.mob.setAggroed(true);
    }

    public void resetTask() {
        this.mob.setAggroed(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.mob.resetActiveHand();
    }

    public void tick() {
        LivingEntity target = this.mob.getAttackTarget();
        if (target == null) {
            return;
        }

        double toTargetDis = this.mob.getDistanceSq(target.getPosX(), target.getPosY(), target.getPosZ());
        boolean canSee = this.mob.getEntitySenses().canSee(target);
        boolean seeTime = this.seeTime > 0;
        if (canSee != seeTime) {
            this.seeTime = 0;
        }

        if (canSee) {
            ++this.seeTime;
        } else {
            --this.seeTime;
        }

        if (this.maxAttackDistance < toTargetDis) {
            this.strafingBackwards = false;
        } else if (toTargetDis < this.maxAttackDistance * 0.75F) {
            this.strafingBackwards = true;
        }

        if (this.mob.ticksExisted % 20 == 0) {
            this.strafe = (this.mob.getRNG().nextFloat() * 2 - 1) * this.forwardSpeed;
        }

        this.mob.getMoveHelper().strafe(this.strafingBackwards ? -this.strafeSpeed : this.strafeSpeed, this.strafe);
        this.mob.faceEntity(target, 30.0F, 30.0F);

        if (this.mob.isHandActive()) {
            if (!canSee && this.seeTime < -60) {
                this.mob.resetActiveHand();
            } else if (canSee) {
                int i = this.mob.getItemInUseMaxCount();
                if (i >= 20) {
                    this.mob.resetActiveHand();
                    this.mob.attackEntityWithRangedAttack(target, BowItem.getArrowVelocity(i));
                    this.attackTime = this.attackCooldown;
                }
            }
            return;
        }

        if (--this.attackTime <= 0 && -60 <= this.seeTime) {
            this.mob.setActiveHand(Hand.MAIN_HAND);
        }


    }


}