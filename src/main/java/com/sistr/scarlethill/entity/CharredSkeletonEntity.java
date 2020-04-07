package com.sistr.scarlethill.entity;

import com.sistr.scarlethill.entity.goal.NearestHurtByTargetGoal;
import com.sistr.scarlethill.entity.goal.StopAndRangedBowAttackGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

//todo 敵対外れて棒立ち問題
public class CharredSkeletonEntity extends SkeletonEntity {

    public CharredSkeletonEntity(EntityType<? extends SkeletonEntity> type, World world) {
        super(type, world);
        this.setHeldItem(Hand.MAIN_HAND, Items.BOW.getDefaultInstance());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new NearestHurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new StopAndRangedBowAttackGoal<>(this, 20));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(6.0D);
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 0;
    }

}
