package com.sistr.scarlethill.item;

import net.minecraft.nbt.CompoundNBT;

public interface IMagicSquareEffect {

    void tick();

    void activate();

    void deactivate();

    boolean isActivate();

    void write(CompoundNBT nbt);

    void read(CompoundNBT nbt);
}
