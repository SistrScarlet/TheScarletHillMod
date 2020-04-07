package com.sistr.scarlethill.entity;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.UUID;

public class SOTSFistEntity extends CreatureEntity implements IEntityAdditionalSpawnData, IMob {
    private FistSide hand = FistSide.RIGHT;
    public SOTSBodyEntity parent;
    public boolean isWorking = false;

    public SOTSFistEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
    }

    private SOTSFistEntity(World world) {
        super(Registration.SOTS_FIST_BOSS.get(), world);
        this.moveController = new MoveHelperController(this);
    }

    public SOTSFistEntity(World world, FistSide hand, SOTSBodyEntity parent) {
        this(world);
        this.hand = hand;
        this.parent = parent;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
    }

    @Override
    public void tick() {
        this.ignoreFrustumCheck = true;
        super.tick();
        if (!this.world.isRemote) {
            if (this.parent == null || !this.parent.isAlive()) {
                this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 0.8F + this.rand.nextFloat() * 0.2F);
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0, 0);
                this.remove();
            }
        }
        this.world.addParticle(ParticleTypes.FLAME, this.getPosX() + (this.rand.nextFloat() * 2 - 1) * this.getWidth() * 0.5F, this.getPosY() + this.rand.nextFloat() * this.getHeight(), this.getPosZ() + (this.rand.nextFloat() * 2 - 1) * this.getWidth() * 0.5F, 0, 0, 0);
        if (this.parent != null) {
            this.rotationYawHead = this.parent.rotationYawHead;
        }
    }

    @Override
    public boolean canRenderOnFire() {
        return !this.isSpectator();
    }

    //落下ダメージ消す
    @Override
    public boolean onLivingFall(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    @Override
    public boolean canDespawn(double p_213397_1_) {
        return false;
    }

    public FistSide getHand() {
        return this.hand;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.5F;
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 0;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putUniqueId("Parent", this.parent.getUniqueID());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.hasUniqueId("Parent")) {
            UUID parentID = compound.getUniqueId("Parent");
            this.parent = this.world instanceof ServerWorld ? (SOTSBodyEntity) ((ServerWorld) this.world).getEntityByUuid(parentID) : null;
        }
    }

    //todo 稀にエラー出る
    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(this.parent.getEntityId());
        buffer.writeBoolean(this.hand == FistSide.RIGHT);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.parent = (SOTSBodyEntity) this.world.getEntityByID(additionalData.readInt());
        this.hand = additionalData.readBoolean() ? FistSide.RIGHT : FistSide.LEFT;
        if (this.parent == null) {
            this.remove();
            return;
        }
        this.parent.getHands().add(this);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public enum FistSide {
        RIGHT,
        LEFT
    }

    public class MoveHelperController extends MovementController {

        public MoveHelperController(SOTSFistEntity hand) {
            super(hand);
        }

        @Override
        public void tick() {
            if (!SOTSFistEntity.this.parent.isFloating()) {
                this.action = Action.WAIT;
                return;
            }
            SOTSFistEntity.this.setMotion(SOTSFistEntity.this.getMotion().mul(0.75, 0.9, 0.75));
            double betweenY = this.posY - SOTSFistEntity.this.getPosY();
            Vec3d motion = SOTSFistEntity.this.getMotion();
            motion = motion.add(0, betweenY * 0.05 * this.speed, 0);
            SOTSFistEntity.this.setMotion(motion);

            if (this.action == Action.MOVE_TO) {
                Vec3d toVec = new Vec3d(this.posX - SOTSFistEntity.this.getPosX(), 0, this.posZ - SOTSFistEntity.this.getPosZ());
                if (toVec.lengthSquared() < 0.25) {
                    this.action = Action.WAIT;
                }
                toVec = toVec.scale(this.speed * 0.05);
                SOTSFistEntity.this.setMotion(SOTSFistEntity.this.getMotion().add(toVec));
            }
        }
    }
}
