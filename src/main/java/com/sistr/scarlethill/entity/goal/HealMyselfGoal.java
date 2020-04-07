package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.util.EffectUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;

public class HealMyselfGoal extends Goal {
    private final MobEntity mob;
    private final int healReadyTicks;
    private final float maxLeaveDistance;
    private final float backSpeed;
    private int healTimer;
    private float strafe;

    public HealMyselfGoal(MobEntity mob, float backSpeed, int healReadyTicks, float maxLeaveDistance) {
        this.mob = mob;
        this.healReadyTicks = healReadyTicks;
        this.maxLeaveDistance = maxLeaveDistance * maxLeaveDistance;
        this.backSpeed = backSpeed;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        return this.mob.getHealth() < this.mob.getMaxHealth() * 0.5F;
    }

    @Override
    public void startExecuting() {
        this.mob.getNavigator().clearPath();
    }

    @Override
    public void tick() {
        if (this.healReadyTicks < this.healTimer) {
            this.mob.setHealth(this.mob.getMaxHealth());
            EffectUtil.spawnParticleBox((ServerWorld) this.mob.world, ParticleTypes.HEART,
                    this.mob.getPosX(), this.mob.getPosY() + this.mob.getEyeHeight(), this.mob.getPosZ(),
                    5, 0.5F);
        }

        if (this.mob.ticksExisted % 4 == 0) {
            EffectUtil.spawnParticleBox((ServerWorld) this.mob.world, ParticleTypes.ANGRY_VILLAGER,
                    this.mob.getPosX(), this.mob.getPosY() + this.mob.getEyeHeight(), this.mob.getPosZ(),
                    5, 0.5F);
        }

        LivingEntity target = this.mob.getAttackTarget();
        if (target == null) {
            this.healTimer++;
            return;
        }
        this.mob.faceEntity(target, 30.0F, 30.0F);

        double toTargetDis = this.mob.getDistanceSq(target.getPosX(), target.getPosY(), target.getPosZ());

        if (this.maxLeaveDistance < toTargetDis) {
            this.healTimer++;
            return;
        }
        if (this.mob.ticksExisted % 20 == 0) {
            this.strafe = (this.mob.getRNG().nextFloat() * 2 - 1) * 0.5F;
        }
        this.mob.getMoveHelper().strafe(-this.backSpeed, this.strafe);

    }

    @Override
    public void resetTask() {
        this.healTimer = 0;
    }
}
