package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.ITameable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;
import java.util.UUID;

public class TameableOwnerHurtByTargetGoal<E extends MobEntity & ITameable> extends TargetGoal {
    private final E mob;
    private LivingEntity attacker;
    private int timestamp;

    public TameableOwnerHurtByTargetGoal(E mob, boolean checkSight) {
        super(mob, checkSight);
        this.mob = mob;
    }

    @Override
    public boolean shouldExecute() {
        Optional<UUID> optionalOwnerId = this.mob.getOwnerId();
        if (!optionalOwnerId.isPresent() || this.mob.isWait()) {
            return false;
        }
        LivingEntity owner = this.mob.world.getPlayerByUuid(optionalOwnerId.get());
        if (owner == null) {
            return false;
        }
        this.attacker = owner.getRevengeTarget();
        int timer = owner.getRevengeTimer();
        return timer != this.timestamp && this.isSuitableTarget(this.attacker, EntityPredicate.DEFAULT) && this.shouldAttackEntity(this.attacker, owner);
    }

    private boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
        if (target instanceof CreeperEntity || target instanceof GhastEntity) {
            return false;
        }
        if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).canAttackPlayer((PlayerEntity) target)) {
            return false;
        }
        if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity) target).isTame()) {
            return false;
        }
        if (target instanceof TameableEntity && ((TameableEntity) target).getOwner() == owner) {
            return false;
        }
        if (target instanceof ITameable) {
            ITameable tameableMob = (ITameable) target;
            Optional<UUID> optionalOwnerId = tameableMob.getOwnerId();
            return optionalOwnerId.isPresent() && optionalOwnerId.get() == owner.getUniqueID();
        }
        return false;
    }
}
