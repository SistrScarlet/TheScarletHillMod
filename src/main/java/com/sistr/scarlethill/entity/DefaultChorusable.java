package com.sistr.scarlethill.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;

public class DefaultChorusable<E extends Entity> implements IChorusable {
    private final E mob;
    private final SoundEvent chorus;
    private int coolTime = -1;
    private int level;

    public DefaultChorusable(E mob, SoundEvent chorus) {
        this.mob = mob;
        this.chorus = chorus;
    }

    @Override
    public boolean setChorus(int ticksAgo, int level) {
        if (level <= this.level) {
            return false;
        }
        this.coolTime = this.mob.ticksExisted + ticksAgo;
        this.level = level;
        return false;
    }

    @Override
    public int getChorusLevel() {
        return this.level;
    }

    @Override
    public void resetLevel() {
        this.level = -1;
    }

    @Override
    public boolean isCoolTime() {
        return this.coolTime != -1 && this.mob.ticksExisted < this.coolTime;
    }

    @Override
    public SoundEvent getChorus() {
        return this.chorus;
    }

}
