package com.sistr.scarlethill.entity;

import com.sistr.scarlethill.entity.goal.*;
import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.util.EffectUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

//todo 突進時の頭の向き
public class ScarletBearEntity extends PolarBearEntity implements IMob {
    public boolean isLowHealth = false;

    public ScarletBearEntity(EntityType<? extends PolarBearEntity> type, World world) {
        super(type, world);
        this.stepHeight = 1.0F;
        this.experienceValue = 50;
    }

    public ScarletBearEntity(World world) {
        super(Registration.SCARLET_BEAR_BOSS.get(), world);
        this.stepHeight = 1.0F;
        this.experienceValue = 75;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));

        this.targetSelector.addGoal(1, new NearestHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new PlayerTargetGoal());
        this.targetSelector.addGoal(3, new AliveMobTargetGoal());
        this.goalSelector.addGoal(4, new GetCloserGoal());

        this.goalSelector.addGoal(3, new SMeleeAttackGoal(10, 0, 5, 5, 12, 4, SkillMeleeAttackGoal.AttackRangeShape.TRIANGLE));
        this.goalSelector.addGoal(2, new NormalSChargeAttackGoal(30, 60, 20, 80, 7, 2, 1.0D));
        this.goalSelector.addGoal(2, new LowHealthSChargeAttackGoal(25, 60, 10, 40, 12, 2, 2.0D));
        this.goalSelector.addGoal(2, new NormalSJumpAttackGoal(10, 120, 10, 80, 10, 3, 1.0F));
        this.goalSelector.addGoal(2, new LowHealthSJumpAttackGoal(0, 120, 10, 60, 15, 4, 1.0F));
        this.goalSelector.addGoal(2, new SCounterExplodeGoal(0, 10, 5, 5, 2, 5));
        this.goalSelector.addGoal(2, new LowHealthAlertGoal(0, 20, 10, 3, 3, 10));

        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));

    }

    //倒した敵をムシャムシャする
    //敵の最大体力分回復する
    public void eat(float targetHealth) {
        this.heal(targetHealth);
        this.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1.0F, 0.8F);
        if (this.world instanceof ServerWorld) {
            EffectUtil.spawnParticleBox((ServerWorld) this.world, ParticleTypes.HEART, this.getPosX(), this.getPosY() + this.getHeight(), this.getPosZ(), MathHelper.floor(targetHealth), this.getWidth());

        }
    }

    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0D);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(6.0D);
    }

    @Override
    public void livingTick() {
        if ((this.ticksExisted & 3) == 0 && this.getHealth() <= 0) {
            playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0, 0);
        }
        super.livingTick();
    }

    //落下ダメージ消す
    @Override
    public boolean onLivingFall(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        //体力が2分の1を切ったらフラグを建てる
        if (!this.isLowHealth && this.getHealth() < this.getMaxHealth() / 2) {
            this.isLowHealth = true;
        }
        return super.attackEntityFrom(source, amount);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("IsLowHealth", this.isLowHealth);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.isLowHealth = compound.getBoolean("IsLowHealth");
    }

    public boolean isNonBoss() {
        return false;
    }

    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropSpecialItems(source, looting, recentlyHitIn);
        for (int i = 2; i > 0; i--) {
            ItemEntity itementity = this.entityDropItem(Registration.SCARLET_BEAR_CLAW_ITEM.get());
            if (itementity != null) {
                itementity.setNoDespawn();
            }
        }

    }

    class SMeleeAttackGoal extends SkillMeleeAttackGoal {
        public SMeleeAttackGoal(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, AttackRangeShape shape) {
            super(ScarletBearEntity.this, startupLength, actionLength, freezeLength, chance, damage, range, shape);
        }

        @Override
        protected void readyStart() {
            LivingEntity entity = ScarletBearEntity.this.getAttackTarget();
            if (entity == null) {
                setStatus(SkillStatus.FREEZE);
                return;
            }
            ScarletBearEntity.this.world.playSound(null, ScarletBearEntity.this.getPosition(), SoundEvents.ENTITY_POLAR_BEAR_WARNING, SoundCategory.NEUTRAL, 3.0F, 1.0F);

        }

        @Override
        protected void actionStart() {
            super.actionStart();
            LivingEntity entity = ScarletBearEntity.this.getAttackTarget();
            if (entity == null) {
                setStatus(SkillStatus.FREEZE);
                return;
            }
            ScarletBearEntity.this.markVelocityChanged();
            ScarletBearEntity.this.setMotion(ScarletBearEntity.this.getMotion().
                    add(entity.getPositionVec().subtract(ScarletBearEntity.this.getPositionVec()).normalize().mul(0.3, 0, 0.3).add(0, 0.1, 0)));
            ScarletBearEntity.this.world.playSound(null, ScarletBearEntity.this.getPosition(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.NEUTRAL, 1.0F, 0.5F);
        }

        @Override
        protected void readyTick() {
            super.readyTick();
            ScarletBearEntity.this.setStanding(this.timer < this.readyLength);
        }
    }

    class SCounterExplodeGoal extends SkillCounterExplodeGoal {

        public SCounterExplodeGoal(int startupLength, int actionLength, int freezeLength, int chance, float strength, float radius) {
            super(ScarletBearEntity.this, startupLength, actionLength, freezeLength, chance, strength, radius);
        }

        @Override
        protected void readyStart() {
            ScarletBearEntity.this.world.playSound(null, ScarletBearEntity.this.getPosition(), SoundEvents.ENTITY_POLAR_BEAR_WARNING, SoundCategory.NEUTRAL, 3.0F, 0.5F);
            ScarletBearEntity.this.setStanding(true);
        }

        @Override
        protected void freezeTick() {
            ScarletBearEntity.this.setStanding(false);
        }
    }

    class LowHealthAlertGoal extends SCounterExplodeGoal {
        private boolean isAlreadyExecute = false;

        public LowHealthAlertGoal(int startupLength, int actionLength, int freezeLength, int chance, float strength, float radius) {
            super(startupLength, actionLength, freezeLength, chance, strength, radius);
        }

        @Override
        protected void readyStart() {
            ScarletBearEntity.this.world.playSound(null, ScarletBearEntity.this.getPosition(), SoundEvents.ENTITY_POLAR_BEAR_WARNING, SoundCategory.NEUTRAL, 3.0F, 0.5F);
        }

        @Override
        protected void knockback(Entity target) {
            if (target.attackEntityFrom(DamageSource.causeMobDamage(ScarletBearEntity.this).setFireDamage(), 1)) {
                target.setFire(1);
            }
            super.knockback(target);
        }

        @Override
        public boolean shouldStart() {
            if (!this.isAlreadyExecute && ScarletBearEntity.this.isLowHealth) {
                this.isAlreadyExecute = true;
                return true;
            }
            return false;
        }

        @Override
        protected void actionStart() {
            EffectUtil.spawnParticleBox((ServerWorld) ScarletBearEntity.this.world, ParticleTypes.FLAME, ScarletBearEntity.this.getPosX(), ScarletBearEntity.this.getPosY(), ScarletBearEntity.this.getPosZ(), 200, this.radius);
        }

    }

    class NormalSChargeAttackGoal extends SkillChargeAttackGoal {

        public NormalSChargeAttackGoal(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, double chargeSpeed) {
            super(ScarletBearEntity.this, startupLength, actionLength, freezeLength, chance, damage, range, chargeSpeed);
        }

        @Override
        protected void readyStart() {
            ScarletBearEntity.this.world.playSound(null, ScarletBearEntity.this.getPosition(), SoundEvents.ENTITY_POLAR_BEAR_WARNING, SoundCategory.NEUTRAL, 3.0F, 0.5F);
        }

        @Override
        protected void readyTick() {
            //発生の前半だったら体を持ち上げる
            ScarletBearEntity.this.setStanding(this.timer < this.readyLength / 2);
        }

    }

    class LowHealthSChargeAttackGoal extends NormalSChargeAttackGoal {
        private boolean nextExecute = false;

        public LowHealthSChargeAttackGoal(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, double chargeSpeed) {
            super(startupLength, actionLength, freezeLength, chance, damage, range, chargeSpeed);
        }

        @Override
        public boolean shouldStart() {

            if (!ScarletBearEntity.this.isLowHealth) {
                return false;
            }

            if (this.nextExecute && ScarletBearEntity.this.getAttackTarget() != null) {
                this.nextExecute = false;
                //移動地点を再設定。できなかった場合はスルー
                BlockRayTraceResult result = getMoveDestination(ScarletBearEntity.this.getAttackTarget());
                if (result.getType() != RayTraceResult.Type.MISS) {
                    BlockPos hitPos = result.getPos();
                    this.movePosX = hitPos.getX();
                    this.movePosZ = hitPos.getZ();
                    return true;
                }
            }

            if (super.shouldStart()) {
                //3分の2で2連続行動
                if (ScarletBearEntity.this.getRNG().nextInt(3) != 0) {
                    this.nextExecute = true;
                }
                return true;
            }

            return false;
        }
    }

    class NormalSJumpAttackGoal extends SkillJumpAttackGoal {

        public NormalSJumpAttackGoal(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float jumpMotionY) {
            super(ScarletBearEntity.this, startupLength, actionLength, freezeLength, chance, damage, range, jumpMotionY);
        }

        @Override
        protected void readyStart() {
            ScarletBearEntity.this.world.playSound(null, ScarletBearEntity.this.getPosition(), SoundEvents.ENTITY_POLAR_BEAR_WARNING, SoundCategory.NEUTRAL, 3.0F, 0.5F);
        }

        @Override
        protected void readyTick() {
            ScarletBearEntity.this.setStanding(true);
        }

        @Override
        protected void freezeStart() {
            super.freezeStart();
            ScarletBearEntity.this.setStanding(false);
        }

    }

    class LowHealthSJumpAttackGoal extends NormalSJumpAttackGoal {

        public LowHealthSJumpAttackGoal(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float jumpMotionY) {
            super(startupLength, actionLength, freezeLength, chance, damage, range, jumpMotionY);
        }

        @Override
        public boolean shouldStart() {
            if (!ScarletBearEntity.this.isLowHealth) {
                return false;
            }
            return super.shouldStart();
        }
    }

    //プレイヤーはMobに含まれない
    class PlayerTargetGoal extends NearestAttackableTargetGoal<PlayerEntity> {
        public PlayerTargetGoal() {
            super(ScarletBearEntity.this, PlayerEntity.class, 20, true, true, null);
        }
    }

    //腐った死体以外の生きているモブなら敵対して食べる
    //もしもメイドさんを食われたらトラウマになりそう(と言いつつ除外はしない)
    class AliveMobTargetGoal extends NearestAttackableTargetGoal<MobEntity> {

        public AliveMobTargetGoal() {
            super(ScarletBearEntity.this, MobEntity.class, 20, true, true, (living) ->
                    !(living instanceof ScarletBearEntity) && (!(living instanceof MonsterEntity) || !living.isEntityUndead()));
        }
    }

    class GetCloserGoal extends MeleeAttackGoal {
        public GetCloserGoal() {
            //useLongMemoryをfalseにすると攻撃しなくなる
            //クリーパー見て気付いた
            super(ScarletBearEntity.this, 1.25D, false);
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double reach = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= reach && this.attackTick <= 0) {
                this.attackTick = 20;
                this.attacker.attackEntityAsMob(enemy);
                ScarletBearEntity.this.setStanding(false);
            } else if (distToEnemySqr <= reach * 2.0D) {
                if (this.attackTick <= 0) {
                    ScarletBearEntity.this.setStanding(false);
                    this.attackTick = 20;
                }

                if (this.attackTick <= 10) {
                    ScarletBearEntity.this.setStanding(true);
                    ScarletBearEntity.this.playWarningSound();
                }
            } else {
                this.attackTick = 20;
                ScarletBearEntity.this.setStanding(false);
            }

        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            ScarletBearEntity.this.setStanding(false);
            super.resetTask();
        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return 4.0F + attackTarget.getWidth();
        }
    }

}
