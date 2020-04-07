package com.sistr.scarlethill.entity;

import net.minecraft.util.SoundEvent;

public interface IChorusable {

    boolean setChorus(int ticksAgo, int level);

    int getChorusLevel();

    void resetLevel();

    boolean isCoolTime();

    SoundEvent getChorus();

}
