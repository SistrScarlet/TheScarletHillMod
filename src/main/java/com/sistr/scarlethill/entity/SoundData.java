package com.sistr.scarlethill.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SoundData {
    @Nullable
    private final Entity entity;
    private final World world;
    @Nullable
    private Vec3d pos;
    private final SoundEvent sound;
    private final SoundCategory soundCategory;
    private final float volume;
    private final float pitch;

    public SoundData(Entity entity, SoundEvent sound, float volume, float pitch) {
        this.entity = entity;
        this.world = entity.world;
        this.sound = sound;
        this.soundCategory = entity.getSoundCategory();
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundData(World world, double x, double y, double z, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        this.entity = null;
        this.world = world;
        this.pos = new Vec3d(x, y, z);
        this.sound = sound;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void playSound() {
        if (this.entity != null) {
            this.pos = this.entity.getPositionVec();
        }
        if (this.pos != null) {
            this.world.playSound(null, this.pos.x, this.pos.y, this.pos.z, this.sound, this.soundCategory, this.volume, this.pitch);
        }
    }
}
