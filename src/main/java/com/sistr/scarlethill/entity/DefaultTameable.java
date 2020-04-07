package com.sistr.scarlethill.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class DefaultTameable implements ITameable {
    private static final DataParameter<Boolean> WAIT = EntityDataManager.createKey(CrimsonianEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(CrimsonianEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private final Entity entity;

    public DefaultTameable(Entity entity) {
        this.entity = entity;
        entity.getDataManager().register(WAIT, false);
        entity.getDataManager().register(OWNER_UNIQUE_ID, Optional.empty());
    }

    @Override
    public Optional<UUID> getOwnerId() {
        return this.entity.getDataManager().get(OWNER_UNIQUE_ID);
    }

    @Override
    public void setOwnerId(@Nullable UUID id) {
        this.entity.getDataManager().set(OWNER_UNIQUE_ID, Optional.ofNullable(id));
    }

    @Override
    public boolean isWait() {
        return this.entity.getDataManager().get(WAIT);
    }

    @Override
    public void setWait(boolean state) {
        this.entity.getDataManager().set(WAIT, state);
    }

    @Override
    public void writeTameableNBT(CompoundNBT nbt) {
        Optional<UUID> ownerId = this.getOwnerId();
        if (ownerId.isPresent()) {
            nbt.putUniqueId("OwnerUUID", ownerId.get());
        }
        nbt.putBoolean("IsWait", this.isWait());
    }

    @Override
    public void readTameableNBT(CompoundNBT nbt) {
        if (nbt.hasUniqueId("OwnerUUID")) {
            this.setOwnerId(nbt.getUniqueId("OwnerUUID"));
        }
        this.setWait(nbt.getBoolean("IsWait"));
    }
}
