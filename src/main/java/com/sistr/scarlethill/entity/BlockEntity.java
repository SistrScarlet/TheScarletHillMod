package com.sistr.scarlethill.entity;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

//実験中。
public class BlockEntity extends Entity {
    private BlockState tile = Blocks.STONE.getDefaultState();
    protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.createKey(BlockEntity.class, DataSerializers.BLOCK_POS);

    public BlockEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public BlockEntity(World world, double x, double y, double z, BlockState blockState) {
        super(Registration.BLOCK_ENTITY.get(), world);
        this.tile = blockState;
        this.preventEntitySpawning = true;
        this.setPosition(x, y, z);
        this.setMotion(Vec3d.ZERO);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.setOrigin(new BlockPos(this));
    }

    protected void registerData() {
        this.dataManager.register(ORIGIN, BlockPos.ZERO);
    }

    public void setOrigin(BlockPos p_184530_1_) {
        this.dataManager.set(ORIGIN, p_184530_1_);
    }

    @OnlyIn(Dist.CLIENT)
    public BlockPos getOrigin() {
        return this.dataManager.get(ORIGIN);
    }

    @Override
    public void tick() {
        super.tick();
        /* シュルカーとの同様の処理
        double d0 = 0.5D - (double) MathHelper.sin((0.5F + this.peekAmount) * (float)Math.PI) * 0.5D;
        double d1 = 0.5D - (double)MathHelper.sin((0.5F + this.prevPeekAmount) * (float)Math.PI) * 0.5D;
        if (this.isAddedToWorld() && this.world instanceof net.minecraft.world.server.ServerWorld) ((net.minecraft.world.server.ServerWorld)this.world).chunkCheck(this); // Forge - Process chunk registration after moving.
        Direction direction3 = this.getAttachmentFacing().getOpposite();
        this.setBoundingBox((new AxisAlignedBB(this.getPosX() - 0.5D, this.getPosY(), this.getPosZ() - 0.5D, this.getPosX() + 0.5D, this.getPosY() + 1.0D, this.getPosZ() + 0.5D)).expand((double)direction3.getXOffset() * d0, (double)direction3.getYOffset() * d0, (double)direction3.getZOffset() * d0));
        double d2 = d0 - d1;
        if (d2 > 0.0D) {
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox());
            if (!list.isEmpty()) {
                for(Entity entity : list) {
                    if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
                        entity.move(MoverType.SHULKER, new Vec3d(d2 * (double)direction3.getXOffset(), d2 * (double)direction3.getYOffset(), d2 * (double)direction3.getZOffset()));
                    }
                }
            }
        }

         */
    }

    public BlockState getBlockState() {
        return this.tile;
    }

    @OnlyIn(Dist.CLIENT)
    public World getWorldObj() {
        return this.world;
    }

    protected void writeAdditional(CompoundNBT compound) {
        compound.put("BlockState", NBTUtil.writeBlockState(this.tile));

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditional(CompoundNBT compound) {
        this.tile = NBTUtil.readBlockState(compound.getCompound("BlockState"));

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
