package com.sistr.scarlethill.util;

import com.sistr.scarlethill.entity.projectile.AbstractProjectile;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class ScarletProjectileHelper {

    //視線の通る地点を取得する
    @Nullable
    public static Vec3d getCanSeePos(CreatureEntity shooter, Vec3d targetPos) {
        Vec3d nearestCanSeePos = null;
        for (int i = 0; i < 10; ++i) {
            Vec3d randomPos = RandomPositionGenerator.getLandPos(shooter, 8, 5);
            if (randomPos == null) continue;
            if (nearestCanSeePos == null || targetPos.squareDistanceTo(randomPos) < targetPos.squareDistanceTo(nearestCanSeePos)) {
                Vec3d start = randomPos.add(0, shooter.getEyeHeight(), 0);
                BlockRayTraceResult result = shooter.world.rayTraceBlocks(new RayTraceContext(start, targetPos,
                        RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, shooter));
                if (result.getType() == RayTraceResult.Type.MISS) {
                    nearestCanSeePos = randomPos;
                }
            }
        }
        return nearestCanSeePos;
    }

    //shootPosから発射された飛翔体が、targetPosに着弾する角度を取得する
    public static Vec3d getShootAngle(AbstractProjectile projectile, Vec3d shootPos, Vec3d targetPos, float addVelocity) {
        float velocity = projectile.getVelocity() + addVelocity;
        float drag = projectile.getAirDrag();
        float gravity = projectile.getGravity();
        double distanceSq;
        double nearDistanceSq = -1;
        int nearAngle = -1;
        Vec3d baseAngle = targetPos.subtract(shootPos).normalize();
        //弾道の終点と対象との距離が1以下になるまで角度を変えて試行
        getAngle:
        for (int testAngle = -90; testAngle < 90; testAngle++) {
            Vec3d motion = VecMathUtil.rotatePitch(baseAngle, testAngle).normalize().scale(velocity);
            Vec3d checkPoint = shootPos;
            double inLoopNearDistanceSq = -1;
            //ある角度から発射された弾道の計算
            //ループ回数=ヒットまでの所要tick数
            for (int tick = 0; tick < 200; tick++) {
                distanceSq = checkPoint.squareDistanceTo(targetPos);
                //ある角度での対象との距離が一定以下になった場合は終了
                if (distanceSq < 0.5) {
                    nearAngle = testAngle;
                    break getAngle;
                }
                //接近している場合
                if (inLoopNearDistanceSq < 0 || distanceSq < inLoopNearDistanceSq) {
                    inLoopNearDistanceSq = distanceSq;
                    //今までの試行での至近距離が初期値であるか、distanceSqがより近い場合は、最近角度を更新
                    if (nearDistanceSq < 0 || distanceSq < nearDistanceSq) {
                        nearDistanceSq = distanceSq;
                        nearAngle = testAngle;
                    }
                } else {//targetPosから遠ざかっている場合
                    break;
                }

                //次回のチェックに備える
                checkPoint = checkPoint.add(motion);
                motion = motion.scale(drag).add(0, -gravity, 0);
            }
        }
        return VecMathUtil.rotatePitch(baseAngle, nearAngle);
    }

}
