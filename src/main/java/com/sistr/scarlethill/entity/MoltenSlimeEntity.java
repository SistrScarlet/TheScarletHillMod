package com.sistr.scarlethill.entity;

import com.sistr.scarlethill.entity.goal.NearestHurtByTargetGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.EnumSet;

//Eventから爆破時のブロック破壊を無効にしている
public class MoltenSlimeEntity extends CreeperEntity {
    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;
    private boolean wasOnGround;

    public MoltenSlimeEntity(EntityType<? extends CreeperEntity> type, World worldIn) {
        super(type, worldIn);
        this.moveController = new MoltenSlimeEntity.MoveHelperController(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new CreeperSwellGoal(this));

        this.goalSelector.addGoal(1, new MoltenSlimeEntity.FloatGoal(this));
        this.goalSelector.addGoal(2, new MoltenSlimeEntity.AttackGoal(this));
        this.goalSelector.addGoal(5, new MoltenSlimeEntity.HopGoal(this));

        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(1, new NearestHurtByTargetGoal(this));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(6.0D);
    }

    @Override
    public void tick() {
        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
        this.prevSquishFactor = this.squishFactor;
        super.tick();
        if (this.onGround && !this.wasOnGround) {
            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.squishAmount = -0.5F;
        } else if (!this.onGround && this.wasOnGround) {
            this.squishAmount = 1.0F;
        }

        this.wasOnGround = this.onGround;
        this.alterSquishAmount();
    }

    protected void alterSquishAmount() {
        this.squishAmount *= 0.6F;
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        Entity entity = cause.getTrueSource();
        if (entity != null) {
            double xRatio = entity.getPosX() - this.getPosX();
            double zRatio = entity.getPosZ() - this.getPosZ();
            for (; xRatio * xRatio + zRatio * zRatio < 1.0E-4D; zRatio = (Math.random() - Math.random()) * 0.01D) {
                xRatio = (Math.random() - Math.random()) * 0.01D;
            }
            this.knockBack(this, 1, xRatio, zRatio);
        }
    }

    @Override
    protected void onDeathUpdate() {
        super.onDeathUpdate();
        if (this.deathTime == 20 && !this.world.isRemote) {
            this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 1.5F, Explosion.Mode.NONE);

        }
    }

    /**
     * Gets the amount of time the slime needs to wait between jumps.
     */
    protected int getJumpDelay() {
        return this.rand.nextInt(20) + 10;
    }

    /**
     * Returns true if the slime makes a sound when it jumps (based upon the slime's size)
     */
    protected boolean makesSoundOnJump() {
        return true;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_SLIME_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SLIME_DEATH;
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_SLIME_JUMP;
    }

    protected SoundEvent getSquishSound() {
        return SoundEvents.ENTITY_SLIME_SQUISH;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 0.4F;
    }

    /**
     * Indicates weather the slime is able to damage the player (based upon the slime's size)
     */
    protected boolean canDamagePlayer() {
        return false;
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 0;
    }

    static class AttackGoal extends Goal {
        private final MoltenSlimeEntity slime;
        private int growTieredTimer;

        public AttackGoal(MoltenSlimeEntity slimeIn) {
            this.slime = slimeIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            LivingEntity livingentity = this.slime.getAttackTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                return (!(livingentity instanceof PlayerEntity) || !((PlayerEntity) livingentity).abilities.disableDamage) && this.slime.getMoveHelper() instanceof MoltenSlimeEntity.MoveHelperController;
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.growTieredTimer = 300;
            super.startExecuting();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            LivingEntity livingentity = this.slime.getAttackTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else if (livingentity instanceof PlayerEntity && ((PlayerEntity) livingentity).abilities.disableDamage) {
                return false;
            } else {
                return --this.growTieredTimer > 0;
            }
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
            ((MoltenSlimeEntity.MoveHelperController) this.slime.getMoveHelper()).setDirection(this.slime.rotationYaw, this.slime.canDamagePlayer());
        }
    }

    static class FloatGoal extends Goal {
        private final MoltenSlimeEntity slime;

        public FloatGoal(MoltenSlimeEntity slimeIn) {
            this.slime = slimeIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            slimeIn.getNavigator().setCanSwim(true);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveHelper() instanceof MoltenSlimeEntity.MoveHelperController;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (this.slime.getRNG().nextFloat() < 0.8F) {
                this.slime.getJumpController().setJumping();
            }

            ((MoltenSlimeEntity.MoveHelperController) this.slime.getMoveHelper()).setSpeed(1.2D);
        }
    }

    static class HopGoal extends Goal {
        private final MoltenSlimeEntity slime;

        public HopGoal(MoltenSlimeEntity slimeIn) {
            this.slime = slimeIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return !this.slime.isPassenger();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            ((MoltenSlimeEntity.MoveHelperController) this.slime.getMoveHelper()).setSpeed(1.0D);
        }
    }

    static class MoveHelperController extends MovementController {
        private float yRot;
        private int jumpDelay;
        private final MoltenSlimeEntity slime;
        private boolean isAggressive;

        public MoveHelperController(MoltenSlimeEntity slimeIn) {
            super(slimeIn);
            this.slime = slimeIn;
            this.yRot = 180.0F * slimeIn.rotationYaw / (float) Math.PI;
        }

        public void setDirection(float yRotIn, boolean aggressive) {
            this.yRot = yRotIn;
            this.isAggressive = aggressive;
        }

        public void setSpeed(double speedIn) {
            this.speed = speedIn;
            this.action = MovementController.Action.MOVE_TO;
        }

        public void tick() {
            this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, this.yRot, 90.0F);
            this.mob.rotationYawHead = this.mob.rotationYaw;
            this.mob.renderYawOffset = this.mob.rotationYaw;
            if (this.action != MovementController.Action.MOVE_TO) {
                this.mob.setMoveForward(0.0F);
            } else {
                this.action = MovementController.Action.WAIT;
                if (this.mob.onGround) {
                    this.mob.setAIMoveSpeed((float) (this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getJumpController().setJumping();
                        if (this.slime.makesSoundOnJump()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                        }
                    } else {
                        this.slime.moveStrafing = 0.0F;
                        this.slime.moveForward = 0.0F;
                        this.mob.setAIMoveSpeed(0.0F);
                    }
                } else {
                    this.mob.setAIMoveSpeed((float) (this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
                }

            }
        }
    }
}
