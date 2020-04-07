package com.sistr.scarlethill.entity.goal;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;

import java.util.EnumSet;

public class StopAndRangedBowAttackGoal<T extends MonsterEntity & IRangedAttackMob> extends Goal {
    private final T entity;
    private int attackCooldown;
    private int attackTime = -1;
    private int seeTime;

    public StopAndRangedBowAttackGoal(T mob, int attackCooldownIn) {
        this.entity = mob;
        this.attackCooldown = attackCooldownIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    public void setAttackCooldown(int attackCooldownIn) {
        this.attackCooldown = attackCooldownIn;
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute() {
        return this.entity.getAttackTarget() != null && this.isBowInMainhand();
    }

    protected boolean isBowInMainhand() {
        return this.entity.getHeldItemMainhand().getItem() instanceof BowItem || this.entity.getHeldItemOffhand().getItem() instanceof BowItem;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        return (this.shouldExecute() || !this.entity.getNavigator().noPath()) && this.isBowInMainhand();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        super.startExecuting();
        this.entity.setAggroed(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        super.resetTask();
        this.entity.setAggroed(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.resetActiveHand();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        if (this.entity.hurtResistantTime == 19) {
            this.entity.resetActiveHand();
        }
        LivingEntity target = this.entity.getAttackTarget();
        if (target != null) {
            boolean isSee = this.entity.getEntitySenses().canSee(target);
            boolean isSeeTimeFound = this.seeTime > 0;
            if (isSee != isSeeTimeFound) {
                this.seeTime = 0;
            }

            if (isSee) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            this.entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);

            if (this.entity.isHandActive()) {
                if (!isSee && this.seeTime < -10) {
                    this.entity.resetActiveHand();
                } else if (isSee) {
                    int i = this.entity.getItemInUseMaxCount();
                    if (i >= 20) {
                        this.entity.resetActiveHand();
                        this.entity.attackEntityWithRangedAttack(target, BowItem.getArrowVelocity(i));
                        this.attackTime = this.attackCooldown;
                    }
                }
            } else if (--this.attackTime <= 0 && this.seeTime >= -10) {
                this.entity.setActiveHand(ProjectileHelper.getHandWith(this.entity, Items.BOW));
            }

        }
    }
}
