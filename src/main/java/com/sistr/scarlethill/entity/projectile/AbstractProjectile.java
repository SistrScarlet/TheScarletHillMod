package com.sistr.scarlethill.entity.projectile;

import com.sistr.scarlethill.util.VecMathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class AbstractProjectile extends Entity implements IProjectile {
    @Nullable
    private BlockState inBlockState;
    protected boolean inGround;
    protected int timeInGround;
    public UUID shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    private float baseDamage = 2f;
    private SoundEvent hitSound = this.getHitEntitySound();

    protected AbstractProjectile(EntityType<? extends AbstractProjectile> type, World world) {
        super(type, world);
    }

    protected AbstractProjectile(EntityType<? extends AbstractProjectile> type, double x, double y, double z, World world) {
        this(type, world);
        this.setPosition(x, y, z);
    }

    protected AbstractProjectile(EntityType<? extends AbstractProjectile> type, LivingEntity shooter, World world) {
        this(type, shooter.getPosX(), shooter.getPosYEye() - 0.1D, shooter.getPosZ(), world);
        this.setShooter(shooter);
    }

    protected void registerData() {

    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(double x, double y, double z, float addVelocity, float addBlurAngle) {
        float velocity = getVelocity() + addVelocity;
        float blurAngle = getBlurAngle() + addBlurAngle;
        Vec3d vec3d = new Vec3d(x, y, z);
        if (blurAngle != 0) {
            float radius = this.rand.nextFloat() * blurAngle;
            float theta = ((2F * this.rand.nextFloat()) - 1F) * (float) Math.PI;
            float randX = radius * MathHelper.cos(theta);
            float randY = radius * MathHelper.sin(theta);
            vec3d = VecMathUtil.rotatePitchYaw(vec3d, randY, randX);
        }
        vec3d = vec3d.normalize().scale(velocity);
        this.setMotion(vec3d);
        float horizontal = MathHelper.sqrt(horizontalMag(vec3d));
        this.rotationYaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (180 / Math.PI));
        this.rotationPitch = (float) (MathHelper.atan2(vec3d.y, horizontal) * (180 / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.ticksInGround = 0;
    }

    @Override
    public void tick() {
        super.tick();
        boolean noClip = this.getNoClip();
        Vec3d motion = this.getMotion();

        if (this.prevRotationPitch == 0 && this.prevRotationYaw == 0) {
            float horizontal = MathHelper.sqrt(horizontalMag(motion));
            this.rotationYaw = (float) (MathHelper.atan2(motion.x, motion.z) * (180 / Math.PI));
            this.rotationPitch = (float) (MathHelper.atan2(motion.y, horizontal) * (180 / Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        this.onBlockLogic();

        if (this.isWet()) {
            this.extinguish();
        }

        if (this.inGround && !noClip) {
            if (this.inBlockState != this.world.getBlockState(new BlockPos(this)) && this.world.hasNoCollisions(this.getBoundingBox().grow(0.06D))) {
                this.inGround = false;
                this.ticksInGround = 0;
                this.ticksInAir = 0;
                this.setMotion(motion.mul(this.rand.nextFloat() * 0.2F, this.rand.nextFloat() * 0.2F, this.rand.nextFloat() * 0.2F));
            } else if (!this.world.isRemote) {
                ++this.ticksInGround;
                this.inGroundLogic();
            }

            ++this.timeInGround;
        } else {
            this.timeInGround = 0;
            ++this.ticksInAir;
            this.inAirLogic();
            this.hitLogic();
            this.moveLogic();
        }
    }

    protected void onBlockLogic() {
        boolean noClip = this.getNoClip();
        BlockPos blockpos = new BlockPos(this);
        BlockState blockstate = this.world.getBlockState(blockpos);
        if (!blockstate.isAir(this.world, blockpos) && !noClip) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.world, blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3d posVec = this.getPositionVec();

                for (AxisAlignedBB axisalignedbb : voxelshape.toBoundingBoxList()) {
                    if (axisalignedbb.offset(blockpos).contains(posVec)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }
    }

    protected void inGroundLogic() {
        if (this.ticksInGround >= this.despawnInGroundTime()) {
            this.remove();
        }
    }

    public int despawnInGroundTime() {
        return 1200;
    }

    protected void inAirLogic() {
        if (this.ticksInAir >= this.despawnInAirTime()) {
            this.remove();
        }
    }

    public int despawnInAirTime() {
        return 100;
    }

    protected void hitLogic() {
        boolean noClip = this.getNoClip();
        Vec3d posVec = this.getPositionVec();
        Vec3d addVec = posVec.add(this.getMotion());
        RayTraceResult raytraceResult = this.world.rayTraceBlocks(new RayTraceContext(posVec, addVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        if (raytraceResult.getType() != RayTraceResult.Type.MISS) {
            addVec = raytraceResult.getHitVec();
        }

        EntityRayTraceResult entityraytraceresult = this.rayTraceEntities(posVec, addVec);
        if (entityraytraceresult != null) {
            raytraceResult = entityraytraceresult;
        }

        if (raytraceResult.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult) raytraceResult).getEntity();
            Entity shooter = this.getShooter();
            if (entity instanceof PlayerEntity && shooter instanceof PlayerEntity && !((PlayerEntity) shooter).canAttackPlayer((PlayerEntity) entity)) {
                raytraceResult = null;
            }
        }

        if (raytraceResult != null && raytraceResult.getType() != RayTraceResult.Type.MISS && !noClip && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceResult)) {
            this.onHit(raytraceResult);
            this.isAirBorne = true;
        }
    }

    protected void moveLogic() {
        boolean noClip = this.getNoClip();
        Vec3d motion = this.getMotion();
        double motionX = motion.x;
        double motionY = motion.y;
        double motionZ = motion.z;

        double posMotionX = this.getPosX() + motionX;
        double posMotionY = this.getPosY() + motionY;
        double posMotionZ = this.getPosZ() + motionZ;
        float horizontal = MathHelper.sqrt(horizontalMag(motion));
        if (noClip) {
            this.rotationYaw = (float) (MathHelper.atan2(-motionX, -motionZ) * (180F / Math.PI));
        } else {
            this.rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180F / Math.PI));
        }

        for (this.rotationPitch = (float) (MathHelper.atan2(motionY, horizontal) * (180F / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
        this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
        float drag = this.getAirDrag();
        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                this.world.addParticle(ParticleTypes.BUBBLE, posMotionX - motionX * 0.25D, posMotionY - motionY * 0.25D, posMotionZ - motionZ * 0.25D, motionX, motionY, motionZ);
            }

            drag = this.getWaterDrag();
        }

        this.setMotion(motion.scale(drag));
        if (!this.hasNoGravity() && !noClip) {
            Vec3d vec3d4 = this.getMotion();
            this.setMotion(vec3d4.x, vec3d4.y - this.getGravity(), vec3d4.z);
        }

        this.setPosition(posMotionX, posMotionY, posMotionZ);
        this.doBlockCollisions();
    }

    /**
     * Whether the arrow can noClip
     */
    public boolean getNoClip() {
        return !this.world.isRemote && this.noClip;
    }

    /**
     * Gets the EntityRayTraceResult representing the entity hit
     */
    @Nullable
    protected EntityRayTraceResult rayTraceEntities(Vec3d startVec, Vec3d endVec) {
        return ProjectileHelper.rayTraceEntities(this.world, this, startVec, endVec, this.getBoundingBox().expand(this.getMotion()).grow(1.0D), (entity) ->
                !entity.isSpectator() && entity.isAlive() && entity.canBeCollidedWith()
                        && (entity != this.getShooter() || this.ticksInAir >= 5));
    }

    /**
     * Called when the arrow hits a block or an entity
     */
    protected void onHit(RayTraceResult result) {
        RayTraceResult.Type type = result.getType();
        if (type == RayTraceResult.Type.ENTITY) {
            this.onEntityHit((EntityRayTraceResult) result);
        } else if (type == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) result;
            BlockState blockstate = this.world.getBlockState(blockraytraceresult.getPos());
            this.inBlockState = blockstate;
            Vec3d vec3d = blockraytraceresult.getHitVec().subtract(this.getPosX(), this.getPosY(), this.getPosZ());
            this.setMotion(vec3d);
            Vec3d vec3d1 = vec3d.normalize().scale(0.05F);
            this.setRawPosition(this.getPosX() - vec3d1.x, this.getPosY() - vec3d1.y, this.getPosZ() - vec3d1.z);
            this.playSound(this.getHitGroundSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.setHitSound(SoundEvents.ENTITY_ARROW_HIT);
            blockstate.onProjectileCollision(this.world, blockstate, blockraytraceresult, this);
        }

    }

    protected DamageSource getDamageType(DamageSource source) {
        return source;
    }

    /**
     * Called when the arrow hits an entity
     */
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        Entity entity = p_213868_1_.getEntity();
        double speed = this.getMotion().length();
        int damage = MathHelper.ceil(Math.max(speed * this.baseDamage, 0.0D));

        Entity shooter = this.getShooter();
        DamageSource damagesource;
        if (shooter == null) {
            damagesource = new IndirectEntityDamageSource("mob", this, this);
        } else {
            damagesource = new IndirectEntityDamageSource("mob", this, shooter);
            if (shooter instanceof LivingEntity) {
                ((LivingEntity) shooter).setLastAttackedEntity(entity);
            }
        }
        damagesource = getDamageType(damagesource);

        boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        int j = entity.getFireTimer();
        if (this.isBurning() && !isEnderman) {
            entity.setFire(5);
        }

        if (entity.attackEntityFrom(damagesource, damage)) {
            if (isEnderman) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                //if (!this.world.isRemote) {
                //    livingEntity.setArrowCountInEntity(livingEntity.getArrowCountInEntity() + 1);
                //}

                if (this.getKnockbackStrength() > 0) {
                    Vec3d vec3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale(this.getKnockbackStrength() * 0.6D);
                    if (vec3d.lengthSquared() > 0.0D) {
                        livingEntity.addVelocity(vec3d.x, 0.1D, vec3d.z);
                    }
                }

                if (!this.world.isRemote && shooter instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingEntity, shooter);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity) shooter, livingEntity);
                }

                this.addHitEffect(livingEntity);
                if (livingEntity != shooter && livingEntity instanceof PlayerEntity && shooter instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) shooter).connection.sendPacket(new SChangeGameStatePacket(6, 0.0F));
                }
            }

            this.playSound(this.hitSound, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.remove();

        } else {
            entity.setFireTimer(j);
            this.setMotion(this.getMotion().scale(-0.1D));
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            this.ticksInAir = 0;
            if (!this.world.isRemote && this.getMotion().lengthSquared() < 1.0E-7D) {
                this.remove();
            }
        }

    }

    protected void addHitEffect(LivingEntity living) {

    }

    /**
     * The sound made when an entity is hit by this projectile
     */
    public SoundEvent getHitEntitySound() {
        return SoundEvents.ENTITY_ARROW_HIT;
    }

    public final SoundEvent getHitGroundSound() {
        return this.hitSound;
    }

    public void setHitSound(SoundEvent soundIn) {
        this.hitSound = soundIn;
    }

    public float getWaterDrag() {
        return 0.6F;
    }

    public float getAirDrag() {
        return 0.99F;
    }

    public int getKnockbackStrength() {
        return 0;
    }

    public float getGravity() {
        return 0.05F;
    }

    public float getVelocity() {
        return 1.5F;
    }

    public float getBlurAngle() {
        return 1F;
    }

    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.0F;
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    /**
     * Returns true if it's possible to attack this entity with an item.
     */
    public boolean canBeAttackedWithItem() {
        return false;
    }

    public void setDamage(float damageIn) {
        this.baseDamage = damageIn;
    }

    public float getDamage() {
        return this.baseDamage;
    }

    public void setShooter(@Nullable Entity entityIn) {
        this.shootingEntity = entityIn == null ? null : entityIn.getUniqueID();
    }

    @Nullable
    public Entity getShooter() {
        return this.shootingEntity != null && this.world instanceof ServerWorld ? ((ServerWorld) this.world).getEntityByUuid(this.shootingEntity) : null;
    }

    public void writeAdditional(CompoundNBT compound) {
        compound.putShort("life", (short) this.ticksInGround);
        if (this.inBlockState != null) {
            compound.put("inBlockState", NBTUtil.writeBlockState(this.inBlockState));
        }

        compound.putBoolean("inGround", this.inGround);
        compound.putFloat("damage", this.baseDamage);
        if (this.shootingEntity != null) {
            compound.putUniqueId("OwnerUUID", this.shootingEntity);
        }

        ResourceLocation sound = ForgeRegistries.SOUND_EVENTS.getKey(this.hitSound);
        if (sound != null) {
            compound.putString("SoundEvent", sound.toString());
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound) {
        this.ticksInGround = compound.getShort("life");
        if (compound.contains("inBlockState", 10)) {
            this.inBlockState = NBTUtil.readBlockState(compound.getCompound("inBlockState"));
        }

        this.inGround = compound.getBoolean("inGround");
        if (compound.contains("damage", 99)) {
            this.baseDamage = compound.getFloat("damage");
        }

        if (compound.hasUniqueId("OwnerUUID")) {
            this.shootingEntity = compound.getUniqueId("OwnerUUID");
        }

        if (compound.contains("SoundEvent", 8)) {
            SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(compound.getString("SoundEvent")));
            if (soundEvent == null) {
                soundEvent = this.getHitEntitySound();
            }
            this.hitSound = soundEvent;
        }

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
