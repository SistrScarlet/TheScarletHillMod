package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.IGroupController;
import com.sistr.scarlethill.entity.IGroupable;
import com.sistr.scarlethill.entity.ITameable;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

//todo 分離が必要
//リーダーかオーナーについていく
public class FollowLeaderOrOwnerGoal<E extends MobEntity & IGroupable<E> & ITameable> extends Goal {
    private final E mob;
    private final IWorldReader world;
    private final PathNavigator navigator;
    private final double followSpeed;
    private final float minDist;
    private final float maxDist;
    private final boolean isFlyable;
    private LivingEntity followTarget;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public FollowLeaderOrOwnerGoal(E mob, double followSpeed, float minDist, float maxDist, boolean isFlyable) {
        this.mob = mob;
        this.world = mob.world;
        this.navigator = mob.getNavigator();
        this.followSpeed = followSpeed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.isFlyable = isFlyable;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mob.getNavigator() instanceof GroundPathNavigator) && !(mob.getNavigator() instanceof FlyingPathNavigator)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute() {
        if (this.mob.ticksExisted < 100) return false;
        LivingEntity followTarget = null;
        Optional<IGroupController<E>> optional = this.mob.getGroupController();

        if (optional.isPresent() && optional.get().getLeader().isPresent()) {
            followTarget = optional.get().getLeader().get();
        }

        //追従対象が存在しない(グループに不所属またはリーダー不在)か、追従対象が自身(リーダー)の場合
        if (followTarget == null || followTarget.equals(this.mob)) {
            //オーナーが居るなら、追従対象にする
            Optional<UUID> optionalUUID = this.mob.getOwnerId();
            if (!optionalUUID.isPresent()) {
                return false;
            }
            PlayerEntity owner = this.mob.world.getPlayerByUuid(optionalUUID.get());
            if (owner == null) {
                return false;
            }
            followTarget = owner;
        }

        if (!followTarget.isSpectator() && this.minDist * this.minDist < this.mob.getDistanceSq(followTarget)) {
            this.followTarget = followTarget;
            return true;
        }
        return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        if (this.navigator.noPath()) {
            return false;
        } else {
            return !(this.mob.getDistanceSq(this.followTarget) <= (double) (this.maxDist * this.maxDist));
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.getPathPriority(PathNodeType.WATER);
        this.mob.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.followTarget = null;
        this.navigator.clearPath();
        this.mob.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        this.mob.getLookController().setLookPositionWithEntity(this.followTarget, 10.0F, (float) this.mob.getVerticalFaceSpeed());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.mob.getLeashed() && !this.mob.isPassenger()) {
                if (this.mob.getDistanceSq(this.followTarget) >= 144.0D) {
                    this.func_226330_g_();
                } else {
                    this.navigator.tryMoveToEntityLiving(this.followTarget, this.followSpeed);
                }

            }
        }
    }

    private void func_226330_g_() {
        BlockPos blockpos = new BlockPos(this.followTarget);

        for (int i = 0; i < 10; ++i) {
            int j = this.func_226327_a_(-3, 3);
            int k = this.func_226327_a_(-1, 1);
            int l = this.func_226327_a_(-3, 3);
            boolean flag = this.func_226328_a_(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }

    }

    private boolean func_226328_a_(int p_226328_1_, int p_226328_2_, int p_226328_3_) {
        if (Math.abs((double) p_226328_1_ - this.followTarget.getPosX()) < 2.0D && Math.abs((double) p_226328_3_ - this.followTarget.getPosZ()) < 2.0D) {
            return false;
        } else if (!this.func_226329_a_(new BlockPos(p_226328_1_, p_226328_2_, p_226328_3_))) {
            return false;
        } else {
            this.mob.setLocationAndAngles((float) p_226328_1_ + 0.5F, p_226328_2_, (float) p_226328_3_ + 0.5F, this.mob.rotationYaw, this.mob.rotationPitch);
            this.navigator.clearPath();
            return true;
        }
    }

    private boolean func_226329_a_(BlockPos p_226329_1_) {
        PathNodeType pathnodetype = WalkNodeProcessor.func_227480_b_(this.world, p_226329_1_.getX(), p_226329_1_.getY(), p_226329_1_.getZ());
        if (pathnodetype != PathNodeType.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = this.world.getBlockState(p_226329_1_.down());
            if (!this.isFlyable && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = p_226329_1_.subtract(new BlockPos(this.mob));
                return this.world.hasNoCollisions(this.mob, this.mob.getBoundingBox().offset(blockpos));
            }
        }
    }

    private int func_226327_a_(int p_226327_1_, int p_226327_2_) {
        return this.mob.getRNG().nextInt(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
    }

}
