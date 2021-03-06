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
    public static <T extends IParticleData> void spawnParticleLine(ServerWorld world, T particle,
                                                                   Vec3d start, Vec3d end, int particleCount, double blur) {
        for (int i = 0; i < particleCount; i++) {
            double division = 1F / particleCount;
            double x = MathHelper.lerp(division * i, start.x, end.x);
            double y = MathHelper.lerp(division * i, start.y, end.y);
            double z = MathHelper.lerp(division * i, start.z, end.z);
            x += (world.rand.nextFloat() * blur * 2 - blur);
            y += (world.rand.nextFloat() * blur * 2 - blur);
            z += (world.rand.nextFloat() * blur * 2 - blur);
            world.spawnParticle(particle, x, y, z,
                    0, 0, 0, 0, 0);
        }
    }

    //立方体の形にパーティクルを発生させる
    public static <T extends IParticleData> void spawnParticleBox(ServerWorld world, T particle,
                                                                  double x, double y, double z, int particleCount, double radius) {
        for (int i = 0; i < particleCount; i++) {
            world.spawnParticle(particle,
                    x + (world.rand.nextFloat() * radius * 2 - radius),
                    y + (world.rand.nextFloat() * radius * 2 - radius),
                    z + (world.rand.nextFloat() * radius * 2 - radius),
                    0, 0, 0, 0, 0);
        }
    }

    //球形にパーティクルを発生させる。
    public static <T extends IParticleData> void spawnParticleSphere(ServerWorld world, T particle,
                                                                     double x, double y, double z, int particleCount, double radius) {
        for (int i = 0; i < particleCount; i++) {
            Vec3d randomVec = VecMathUtil.getVector(
                    new Vec2f((world.rand.nextFloat() * 2F - 1F) * 180F, (world.rand.nextFloat() * 2F - 1F) * 180F));
            randomVec = randomVec.normalize().scale(MathHelper.sqrt(world.rand.nextFloat()) * radius);
            world.spawnParticle(particle, x + randomVec.x, y + randomVec.y, z + randomVec.z,
                    0, 0, 0, 0, 0);
        }
    }

    //球形にパーティクルを発生させる。
    public static <T extends IParticleData> void spawnParticleSphereOutline(ServerWorld world, T particle,
                                                                            double x, double y, double z, int particleCount, double radius) {
        for (int i = 0; i < particleCount; i++) {
            Vec3d randomVec = VecMathUtil.getVector(
                    new Vec2f((world.rand.nextFloat() * 2F - 1F) * 180F, (world.rand.nextFloat() * 2F - 1F) * 180F));
            randomVec = randomVec.normalize().scale(radius);
            world.spawnParticle(particle, x + randomVec.x, y + randomVec.y, z + randomVec.z,
                    0, 0, 0, 0, 0);
        }
    }

    //二次関数の線を発生させる。
    //頂点地点、開始地点を指定する
    //試してないから動かんかも
    public static <T extends IParticleData> void spawnParticleQuadraticLineVertical(ServerWorld world, T particle,
                                                                            Vec3d start, Vec3d vertex, int particleCount, double blur) {
        Vec3d relVertex = vertex.subtract(start);
        Vec3d increment = relVertex.scale(1F / particleCount * 2);
        double a = -relVertex.getY() / relVertex.getX() * relVertex.getX();
        for (int i = 0; i < particleCount; i++) {
            double x = increment.getX() * i;
            double y = a * (x - relVertex.getX()) * (x - relVertex.getX()) + relVertex.getY();
            double z = increment.getZ() * i;
            x += (world.rand.nextFloat() * blur * 2 - blur) + start.getX();
            y += (world.rand.nextFloat() * blur * 2 - blur) + start.getY();
            z += (world.rand.nextFloat() * blur * 2 - blur) + start.getZ();

            world.spawnParticle(particle, x, y, z,
                    0, 0, 0, 0, 0);
        }
    }

    //二次関数の線を発生させる。
    //頂点地点、開始地点を指定する
    public static <T extends IParticleData> void spawnParticleQuadraticLineHorizon(ServerWorld world, T particle,
                                                                                    Vec3d start, Vec3d vertex, int particleCount, double blur) {
        Vec3d relVertex = vertex.subtract(start);
        Vec3d increment = relVertex.scale(1F / particleCount * 2);
        double a = -relVertex.getX() / relVertex.getY() * relVertex.getY();
        double b = -relVertex.getZ() / relVertex.getY() * relVertex.getY();
        for (int i = 0; i < particleCount; i++) {
            double y = increment.getY() * i;
            double x = a * (y - relVertex.getY()) * (y - relVertex.getY()) + relVertex.getX();
            double z = b * (y - relVertex.getY()) * (y - relVertex.getY()) + relVertex.getZ();
            x += (world.rand.nextFloat() * blur * 2 - blur) + start.getX();
            y += (world.rand.nextFloat() * blur * 2 - blur) + start.getY();
            z += (world.rand.nextFloat() * blur * 2 - blur) + start.getZ();

            world.spawnParticle(particle, x, y, z,
                    0, 0, 0, 0, 0);
        }
    }

}
