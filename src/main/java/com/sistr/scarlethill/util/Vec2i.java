package com.sistr.scarlethill.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class Vec2i {
    private final int x;
    private final int z;

    public Vec2i(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public Vec2i(BlockPos pos) {
        this.x = pos.getX();
        this.z = pos.getZ();
    }

    public Vec2i(double posX, double posZ) {
        this.x = MathHelper.floor(posX);
        this.z = MathHelper.floor(posZ);
    }

    public Vec2i offset(Direction direction) {
        return new Vec2i(this.x + direction.getXOffset(), this.z + direction.getZOffset());
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vec2i vec2i = (Vec2i) obj;
        return x == vec2i.x &&
                z == vec2i.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    @Override
    public String toString() {
        return "Vec2i{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}

