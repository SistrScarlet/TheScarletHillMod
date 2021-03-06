package com.sistr.scarlethill.item;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;

public class FireMagicSquareEffect implements IMagicSquareEffect {
    private Vec3d center;
    private float radius;
    private float damage;
    boolean isActivate;

    public FireMagicSquareEffect(Vec3d center, float radius, float damage) {
        this.center = center;
        this.radius = radius;
        this.damage = damage;
    }

    @Override
    public void tick() {
        
    }

    @Override
    public void activate() {
        isActivate = true;
    }

    @Override
    public void deactivate() {
        isActivate = false;
    }

    @Override
    public boolean isActivate() {
        return isActivate;
    }

    @Override
    public void write(CompoundNBT nbt) {
        nbt.putFloat("damage", damage);
        nbt.putBoolean("isActivate", isActivate);
    }

    @Override
    public void read(CompoundNBT nbt) {
        damage = nbt.getFloat("damage");
        isActivate = nbt.getBoolean("isActivate");
    }
}
