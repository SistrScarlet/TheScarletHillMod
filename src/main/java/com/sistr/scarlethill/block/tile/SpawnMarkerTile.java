package com.sistr.scarlethill.block.tile;

import com.google.common.collect.Lists;
import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SpawnMarkerTile extends TileEntity implements INamedContainerProvider, ISpawnMarker {
    @Nullable
    private EntityType<?> spawnType;

    public SpawnMarkerTile() {
        super(Registration.SPAWNER_MARKER_TILE.get());
    }

    @Override
    public List<UUID> spawn() {
        BlockPos spawnPos = this.getPos().up();
        double x = spawnPos.getX();
        double y = spawnPos.getY();
        double z = spawnPos.getZ();
        if (!this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1), EntityPredicates.NOT_SPECTATING).isEmpty()) {
            return Lists.newArrayList();
        }
        Entity spawnedEntity = getEntityType().spawn(this.world, null, null, null, this.getPos(), SpawnReason.SPAWNER, true, false);
        if (spawnedEntity instanceof MobEntity) {
            PlayerEntity closestPlayer = this.getWorld().getClosestPlayer(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D, true);
            ((MobEntity) spawnedEntity).setAttackTarget(closestPlayer);
        }
        return Lists.newArrayList(spawnedEntity.getUniqueID());
    }

    public EntityType<?> getEntityType() {
        return this.spawnType != null ? this.spawnType : EntityType.PIG;
    }

    public void setEntityType(EntityType<?> type) {
        this.spawnType = type;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.write(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.spawnType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(compound.getString("SpawnEntityType")));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);

        if (this.spawnType != null) {
            compound.putString("SpawnEntityType", this.spawnType.getRegistryName().toString());
        }

        return compound;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new SpawnMarkerContainer(windowId, world, pos, playerInventory, playerEntity);
    }
}
