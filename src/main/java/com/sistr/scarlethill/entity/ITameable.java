package com.sistr.scarlethill.entity;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

//EventからTameable -> Ownerへの攻撃を禁止している
public interface ITameable {

    Optional<UUID> getOwnerId();

    void setOwnerId(@Nullable UUID id);

    boolean isWait();

    void setWait(boolean state);

    void writeTameableNBT(CompoundNBT compound);

    void readTameableNBT(CompoundNBT compound);

}
