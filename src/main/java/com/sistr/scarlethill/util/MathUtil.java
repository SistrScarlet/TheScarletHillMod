package com.sistr.scarlethill.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

//渡す値は角度でラジアンではない
public class MathUtil {

    public static Vec3d rotatePitch(Vec3d vec3d, float pitch) {
        Vec2f spherical = getYawPitch(vec3d);
        float theta = spherical.x;
        float phi = spherical.y;
        phi += pitch;
        return getVector(new Vec2f(theta, phi));
    }

    public static Vec3d rotateYaw(Vec3d vec3d, float yaw) {
        Vec2f spherical = getYawPitch(vec3d);
        float theta = spherical.x;
        float phi = spherical.y;
        theta += yaw;
        return getVector(new Vec2f(theta, phi));
    }

    public static Vec3d rotatePitchYaw(Vec3d vec3d, float pitch, float yaw) {
        Vec2f spherical = getYawPitch(vec3d);
        float theta = spherical.x;
        float phi = spherical.y;
        theta += yaw;
        phi += pitch;
        return getVector(new Vec2f(theta, phi));
    }

    public static Vec2f getYawPitch(Vec3d vec3d) {
        float f = MathHelper.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
        float theta = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (180 / Math.PI));
        float phi = (float) (MathHelper.atan2(vec3d.y, f) * (180 / Math.PI));
        return new Vec2f(theta, phi);
    }

    public static Vec3d getVector(Vec2f yawPitch) {
        float pi = (float) Math.PI / 180F;
        float x = MathHelper.sin(yawPitch.x * pi) * MathHelper.cos(yawPitch.y * pi);
        float y = MathHelper.sin(yawPitch.y * pi);
        float z = MathHelper.cos(yawPitch.x * pi) * MathHelper.cos(yawPitch.y * pi);
        return new Vec3d(x, y, z);
    }

    public static float getYaw(double x, double z) {
        return (float) (MathHelper.atan2(x, z) * (180 / Math.PI));
    }

    public static int getManhattan(Vec2i pos, Vec2i pos2) {
        return Math.abs(pos.getX() - pos2.getX()) + Math.abs(pos.getZ() - pos2.getZ());
    }

    public static Vec3d lerpVec(float partialTick, Vec3d pos, Vec3d prevPos) {
        double x = MathHelper.lerp(partialTick, pos.x, prevPos.x);
        double y = MathHelper.lerp(partialTick, pos.y, prevPos.y);
        double z = MathHelper.lerp(partialTick, pos.z, prevPos.z);
        return new Vec3d(x, y, z);
    }
}
