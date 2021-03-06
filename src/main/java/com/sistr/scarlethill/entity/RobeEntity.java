package com.sistr.scarlethill.entity;

import com.google.common.collect.Lists;
import com.sistr.scarlethill.entity.goal.NearestHurtByTargetGoal;
import com.sistr.scarlethill.item.ScarletWandItem;
import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.util.EffectUtil;
import com.sistr.scarlethill.util.GoalHelper;
import com.sistr.scarlethill.util.ScarletProjectileHelper;
import com.sistr.scarlethill.util.VecMathUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

//方針…的確、威力低め、怯みやすい
//魔法と白兵戦を行う
//雑魚召喚したりする
public class RobeEntity extends MonsterEntity implements IMob, IRangedAttackMob {
    private final ActionController actionController;
    private final LinkedList<Vec3d> targetMoves = Lists.newLinkedList();

    public RobeEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
        actionController = new ActionController(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 20, true, true, null));

        this.goalSelector.addGoal(1, new RobeMeleeAttackGoal(0.6, false, 0.1F, 0.5F));
        this.goalSelector.addGoal(1, new RobeBowAttack(0.6F, 10, 16F));
        this.goalSelector.addGoal(1, new RobeMagicGoal(0.6F, 10, 16F));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20);
        this.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(8);
    }

    @Override
    protected void registerData() {
        super.registerData();
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        this.actionController.tick();
        LivingEntity target = this.getAttackTarget();
        if (target != null) {//ターゲット切り替え時、最大1秒の遅延
            this.targetMoves.addLast(target.getPositionVec().subtract(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ));
            if (20 < this.targetMoves.size()) {
                this.targetMoves.removeFirst();
            }
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isNonBoss() {
        return false;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return super.createSpawnPacket();
    }

    public Vec3d getTargetMovePerSec() {
        Vec3d motion = Vec3d.ZERO;
        for (Vec3d move : targetMoves) {
            motion = motion.add(move);
        }
        return motion.scale(1D / targetMoves.size());
    }

    public ActionController getActionController() {
        return this.actionController;
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack stack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
        AbstractArrowEntity projectile = this.fireArrow(stack, distanceFactor);
        if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
            projectile = ((net.minecraft.item.BowItem) this.getHeldItemMainhand().getItem()).customeArrow(projectile);
        Vec3d targetAt = new Vec3d(
                target.getPosX() - this.getPosX(),
                target.getPosYHeight(1D / 3D) - projectile.getPosY(),
                target.getPosZ() - this.getPosZ());
        double horizonLength = MathHelper.sqrt(targetAt.getX() * targetAt.getX() + targetAt.getZ() * targetAt.getZ());
        float velocity = distanceFactor * 3F;
        Vec3d targetMotion = getTargetMovePerSec().scale(horizonLength / velocity);
        targetAt = targetAt.add(targetMotion);
        projectile.shoot(targetAt.getX(), targetAt.getY() + horizonLength * 0.1D, targetAt.getZ(), velocity, 14 - this.world.getDifficulty().getId() * 4);
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(projectile);
    }

    /**
     * Fires an arrow
     */
    protected AbstractArrowEntity fireArrow(ItemStack arrowStack, float distanceFactor) {
        return ProjectileHelper.fireArrow(this, arrowStack, distanceFactor);
    }

    enum Action {
        NONE,
        MELEE,
        HEAL,
        BOW,
        MAGIC
    }

    static class ActionController {
        private final MobEntity mob;
        private Action action = Action.NONE;

        public ActionController(MobEntity mob) {
            this.mob = mob;
        }

        public void tick() {
            LivingEntity target = this.mob.getAttackTarget();
            if (target == null || !target.isAlive()) {
                this.action = Action.NONE;
                if (target != null) {
                    this.mob.setAttackTarget(null);
                }
                return;
            }
            if (this.mob.ticksExisted % 100 == 0) {
                double distanceSq = this.mob.getDistanceSq(target);
                if (this.action != Action.MAGIC && 12 * 12 < distanceSq) {
                    this.action = Action.MAGIC;
                    this.mob.setHeldItem(Hand.MAIN_HAND, Registration.SCARLET_WAND_ITEM.get().getDefaultInstance());
                } else if (this.action != Action.BOW && 6 * 6 < distanceSq) {
                    this.action = Action.BOW;
                    this.mob.setHeldItem(Hand.MAIN_HAND, Items.BOW.getDefaultInstance());
                } else if (this.action != Action.MELEE) {
                    this.action = Action.MELEE;
                    if (target.isActiveItemStackBlocking()) {
                        this.mob.setHeldItem(Hand.MAIN_HAND, Items.IRON_AXE.getDefaultInstance());
                    } else {
                        this.mob.setHeldItem(Hand.MAIN_HAND, Items.IRON_SWORD.getDefaultInstance());
                    }
                }
            }
        }

        @Nonnull
        public Action getAction() {
            return this.action;
        }

    }

    class RobeMeleeAttackGoal extends MeleeAttackGoal {
        private final float forwardSpeed;
        private final float strafeSpeed;
        private float strafe;
        private int swingTick = 0;
        @Nullable
        private Vec3d targetPos;

        public RobeMeleeAttackGoal(double speedIn, boolean useLongMemory, float forwardSpeed, float strafeSpeed) {
            super(RobeEntity.this, speedIn, useLongMemory);
            this.forwardSpeed = forwardSpeed;
            this.strafeSpeed = strafeSpeed;
        }

        @Override
        public boolean shouldExecute() {
            return RobeEntity.this.getActionController().getAction() == Action.MELEE;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.shouldExecute();
        }

        @Override
        public void tick() {
            LivingEntity target = this.attacker.getAttackTarget();
            if (target == null) {
                return;
            }
            if (this.attacker.getNavigator().noPath()) {
                boolean far = 3 * 3 < this.attacker.getDistanceSq(target);
                if (this.attacker.ticksExisted % 20 == 0) {
                    this.strafe = (this.attacker.getRNG().nextFloat() * 2 - 1) * this.strafeSpeed;
                }
                this.attacker.getMoveHelper().strafe(far ? this.forwardSpeed : -this.forwardSpeed, this.strafe);
            }
            if (0 < swingTick) {
                this.attacker.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
                this.attackTick--;
                this.checkAndPerformAttack(target, this.attacker.getDistanceSq(target));
            } else {
                super.tick();
            }

        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            //振り降ろされたら
            if (0 < swingTick) {
                swingTick--;
                if (swingTick == 0) {
                    this.attacker.swingArm(Hand.MAIN_HAND);
                    if (targetPos == null) {
                        return;
                    }
                    Vec3d attackerPos = this.attacker.getEyePosition(1);
                    Vec3d direction = targetPos.subtract(attackerPos).normalize();
                    Vec3d lookPos = attackerPos.add(direction.scale(4));
                    AxisAlignedBB bb = this.attacker.getBoundingBox().expand(direction.scale(4)).grow(1);
                    BlockRayTraceResult blockResult = this.attacker.world.rayTraceBlocks(
                            new RayTraceContext(attackerPos, lookPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, this.attacker));
                    if (blockResult.getType() != RayTraceResult.Type.MISS) {
                        lookPos = blockResult.getHitVec();
                    }
                    EntityRayTraceResult entityResult = ProjectileHelper.rayTraceEntities(this.attacker, attackerPos, lookPos, bb,
                            (entity -> !entity.isSpectator() && entity.canBeCollidedWith()), direction.scale(4).lengthSquared());
                    if (entityResult != null && entityResult.getType() != RayTraceResult.Type.MISS) {
                        Entity entity = entityResult.getEntity();
                        this.attacker.attackEntityAsMob(entity);
                    }
                    EffectUtil.spawnParticleLine((ServerWorld) this.attacker.world, ParticleTypes.CRIT, attackerPos, lookPos, 8, 0);
                    super.tick();
                }
            } else if (this.attackTick <= 0 && distToEnemySqr <= 3 * 3) {
                this.attacker.getNavigator().clearPath();
                this.attackTick = MathHelper.floor(1.0D / this.attacker.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getValue() * 20.0D);
                if (this.attackTick == 0) {
                    this.attackTick = 5;
                }
                swingTick = 3;
                targetPos = enemy.getEyePosition(1);
            }
        }
    }

    class RobeBowAttack extends Goal {
        private final RobeEntity entity;
        private final float moveSpeedAmp;
        private int attackCooldown;
        private final double minAttackDistanceSq;
        private final float maxAttackDistanceSq;
        private boolean strafingClockwise;
        private int timeToReCalcPath;
        private int seeTime;

        public RobeBowAttack(float moveSpeedAmpIn, float minAttackDistance, float maxAttackDistanceIn) {
            this.entity = RobeEntity.this;
            this.moveSpeedAmp = moveSpeedAmpIn;
            this.minAttackDistanceSq = minAttackDistance * minAttackDistance;
            this.maxAttackDistanceSq = maxAttackDistanceIn * maxAttackDistanceIn;
        }

        @Override
        public boolean shouldExecute() {
            return RobeEntity.this.getActionController().getAction() == Action.BOW;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.shouldExecute();
        }

        public void startExecuting() {
            super.startExecuting();
            this.entity.setAggroed(true);
        }

        //1、相手と距離を取る。２、相手を狙える位置に移動する。３、狙う。４、相手の攻撃を避ける。
        @Override
        public void tick() {
            this.timeToReCalcPath--;
            this.attackCooldown--;
            LivingEntity target = this.entity.getAttackTarget();
            if (target == null) {
                return;
            }
            //相手と距離を取る
            double distanceSq = this.entity.getDistanceSq(target);
            if (distanceSq < this.minAttackDistanceSq && this.timeToReCalcPath <= 0) {
                this.timeToReCalcPath = 20;
                this.entity.getNavigator().clearPath();
                Vec3d farthestPos = null;
                for (int i = 0; i < 10; ++i) {
                    Vec3d randomPos = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, target.getPositionVec());
                    if (randomPos == null) continue;
                    if (farthestPos == null || target.getDistanceSq(farthestPos) < target.getDistanceSq(randomPos)) {
                        farthestPos = randomPos;
                    }
                }
                if (farthestPos != null) {
                    this.entity.getNavigator().tryMoveToXYZ(farthestPos.x, farthestPos.y, farthestPos.z, moveSpeedAmp);
                }
                this.entity.resetActiveHand();
                return;
            }

            //相手を狙える位置に移動する
            boolean canSeeTarget = this.entity.getEntitySenses().canSee(target);
            if (!canSeeTarget) {
                this.seeTime--;
                if (this.seeTime < -10 && this.timeToReCalcPath <= 0) {
                    this.timeToReCalcPath = 40;
                    Vec3d nearestCanSeePos = ScarletProjectileHelper.getCanSeePos(this.entity, target.getEyePosition(1));
                    if (nearestCanSeePos != null) {
                        this.entity.getNavigator().tryMoveToXYZ(nearestCanSeePos.x, nearestCanSeePos.y, nearestCanSeePos.z, moveSpeedAmp);
                    }
                    this.entity.resetActiveHand();
                }
                return;
            }
            this.entity.getNavigator().clearPath();
            this.entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            this.seeTime = 0;

            //狙う
            if (this.attackCooldown <= 0) {
                this.entity.getMoveHelper().strafe(0, 0);
                if (!this.entity.isHandActive()) {
                    this.entity.setActiveHand(ProjectileHelper.getHandWith(this.entity, Items.BOW));
                    return;
                }
                int useCount = this.entity.getItemInUseMaxCount();
                if (useCount >= Math.min(10 + distanceSq / maxAttackDistanceSq * 10, 20)) {
                    ((IRangedAttackMob) this.entity).attackEntityWithRangedAttack(target, BowItem.getArrowVelocity(useCount));
                    this.attackCooldown = 20;
                    this.entity.resetActiveHand();
                }
                return;
            }

            //避ける
            if (this.entity.getRNG().nextFloat() < 0.3F) {
                this.strafingClockwise = !this.strafingClockwise;
            }
            float forward = moveSpeedAmp;
            if (distanceSq < this.maxAttackDistanceSq * 0.75) {
                forward = -forward;
            }
            this.entity.getMoveHelper().strafe(forward / 2, this.strafingClockwise ? moveSpeedAmp : -this.moveSpeedAmp);

        }

        public void resetTask() {
            super.resetTask();
            this.entity.setAggroed(false);
            this.entity.resetActiveHand();
        }

    }

    class RobeMagicGoal extends Goal {
        public final RobeEntity robe;
        private final float moveSpeedAmp;
        private final float minAttackDistanceSq;
        private final float maxAttackDistanceSq;
        private int timeToReCalcPath;
        private int attackCooldown;
        private int seeTime;
        private boolean strafingClockwise;

        public RobeMagicGoal(float moveSpeedAmpIn, float minAttackDistance, float maxAttackDistanceIn) {
            this.robe = RobeEntity.this;
            this.moveSpeedAmp = moveSpeedAmpIn;
            this.minAttackDistanceSq = minAttackDistance * minAttackDistance;
            this.maxAttackDistanceSq = maxAttackDistanceIn * maxAttackDistanceIn;
        }

        @Override
        public boolean shouldExecute() {
            return this.robe.getActionController().getAction() == Action.MAGIC;
        }

        //1、相手と距離を取る。２、相手を狙える位置に移動する。３、狙う。４、相手の攻撃を避ける。
        @Override
        public void tick() {
            this.timeToReCalcPath--;
            this.attackCooldown--;
            LivingEntity target = this.robe.getAttackTarget();
            if (target == null) {
                return;
            }

            //相手と距離を取る
            double distanceSq = this.robe.getDistanceSq(target);
            if (distanceSq < this.minAttackDistanceSq && this.timeToReCalcPath <= 0) {
                this.timeToReCalcPath = 20;
                this.robe.getNavigator().clearPath();
                Vec3d farthestPos = GoalHelper.getAwayPos(this.robe, target.getPositionVec());
                if (farthestPos != null) {
                    this.robe.getNavigator().tryMoveToXYZ(farthestPos.x, farthestPos.y, farthestPos.z, moveSpeedAmp);
                }
                this.robe.resetActiveHand();
                return;
            }

            //相手を狙える位置に移動する
            boolean canSeeTarget = this.robe.getEntitySenses().canSee(target);
            if (!canSeeTarget) {
                this.seeTime--;
                if (this.seeTime < -10 && this.timeToReCalcPath <= 0) {
                    this.timeToReCalcPath = 40;
                    Vec3d nearestCanSeePos = ScarletProjectileHelper.getCanSeePos(this.robe, target.getEyePosition(1));
                    if (nearestCanSeePos != null) {
                        this.robe.getNavigator().tryMoveToXYZ(nearestCanSeePos.x, nearestCanSeePos.y, nearestCanSeePos.z, moveSpeedAmp);
                    }
                    this.robe.resetActiveHand();
                }
                return;
            }
            this.robe.getNavigator().clearPath();
            this.robe.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            this.seeTime = 0;

            //狙う
            if (this.attackCooldown <= 0) {
                this.robe.getMoveHelper().strafe(0, 0);
                if (!this.robe.isHandActive()) {
                    this.robe.setActiveHand(this.robe.getHeldItemMainhand().getItem() == Registration.SCARLET_WAND_ITEM.get() ? Hand.MAIN_HAND : Hand.OFF_HAND);
                    return;
                }
                int useCount = this.robe.getItemInUseMaxCount();
                if (useCount >= 20) {
                    Vec2f pitchYaw = VecMathUtil.getYawPitch(target.getPositionVec().subtract(this.robe.getPositionVec()));
                    ScarletWandItem.triggerTick(this.robe.getActiveItemStack(), this.robe.world, this.robe, pitchYaw.y, -pitchYaw.x);
                    if (this.robe.getActiveItemStack() == ItemStack.EMPTY) {
                        this.attackCooldown = 40;
                    }
                }
                return;
            }

            //避ける
            if (this.robe.getRNG().nextFloat() < 0.3F) {
                this.strafingClockwise = !this.strafingClockwise;
            }
            float forward = moveSpeedAmp;
            if (distanceSq < this.maxAttackDistanceSq * 0.75) {
                forward = -forward;
            }
            this.robe.getMoveHelper().strafe(forward / 2, this.strafingClockwise ? moveSpeedAmp : -this.moveSpeedAmp);
        }
    }

}
