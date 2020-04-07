package com.sistr.scarlethill.entity;

import com.sistr.scarlethill.entity.goal.NearestHurtByTargetGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class FlameZombieEntity extends ZombieEntity {

    public FlameZombieEntity(EntityType<? extends ZombieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new NearestHurtByTargetGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(6.0D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
    }

    @Override
    public boolean canRenderOnFire() {
        return !this.isSpectator();
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 0;
    }
}
