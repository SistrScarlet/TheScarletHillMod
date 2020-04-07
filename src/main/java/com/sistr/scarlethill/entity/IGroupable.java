package com.sistr.scarlethill.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;

//常にグループを持っているわけではないため、IGroupControllerのみでは不十分
public interface IGroupable<E extends Entity> {

    Optional<IGroupController<E>> getGroupController();

    void setGroupController(IGroupController<E> controller);

    IGroupController<E> getDefaultGroupController();

    void writeGroupCompound(CompoundNBT compound);

    void readGroupCompound(CompoundNBT compound);

}
