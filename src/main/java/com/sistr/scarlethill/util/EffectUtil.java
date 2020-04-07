package com.sistr.scarlethill.util;

import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class EffectUtil {

    //以下はServer側からパーティクルを発生させるメソッド群
    //クライアント側で動かせるなら要らない

    //二点間にパーティクルを発生させる
    public static <T extends IParticleData> void spawnParticleLine(ServerWorld world, T particle, Vec3d start, Vec3d end, int particleCount, double blur) {
        for (int i = 0; i < particleCount; i++) {
            double division = 1F / particleCount;
            double x = MathHelper.lerp(division * i, start.x, end.x);
            double y = MathHelper.lerp(division * i, start.y, end.y);
            double z = MathHelper.lerp(division * i, start.z, end.z);
            world.spawnParticle(particle, x + (world.rand.nextFloat() * blur * 2 - blur), y + (world.rand.nextFloat() * blur * 2 - blur), z + (world.rand.nextFloat() * blur * 2 - blur), 0, 0, 0, 0, 0);
        }
    }

    //立方体の形にパーティクルを発生させる
    public static <T extends IParticleData> void spawnParticleBox(ServerWorld world, T particle, double x, double y, double z, int particleCount, double radius) {
        for (int i = 0; i < particleCount; i++) {
            world.spawnParticle(particle, x + (world.rand.nextFloat() * radius * 2 - radius), y + (world.rand.nextFloat() * radius * 2 - radius), z + (world.rand.nextFloat() * radius * 2 - radius), 0, 0, 0, 0, 0);
        }
    }

    //球形にパーティクルを発生させる。
    public static <T extends IParticleData> void spawnParticleSphere(ServerWorld world, T particle, double x, double y, double z, int particleCount, double radius) {
        for (int i = 0; i < particleCount; i++) {
            Vec3d randomVec = MathUtil.getVector(new Vec2f((world.rand.nextFloat() * 2F - 1F) * 180F, (world.rand.nextFloat() * 2F - 1F) * 180F));
            randomVec = randomVec.normalize().scale(MathHelper.sqrt(world.rand.nextFloat()) * radius);
            world.spawnParticle(particle, x + randomVec.x, y + randomVec.y, z + randomVec.z, 0, 0, 0, 0, 0);
        }
    }

    //球形にパーティクルを発生させる。
    public static <T extends IParticleData> void spawnParticleSphereOutline(ServerWorld world, T particle, double x, double y, double z, int particleCount, double radius) {
        for (int i = 0; i < particleCount; i++) {
            Vec3d randomVec = MathUtil.getVector(new Vec2f((world.rand.nextFloat() * 2F - 1F) * 180F, (world.rand.nextFloat() * 2F - 1F) * 180F));
            randomVec = randomVec.normalize().scale(radius);
            world.spawnParticle(particle, x + randomVec.x, y + randomVec.y, z + randomVec.z, 0, 0, 0, 0, 0);
        }
    }

}
