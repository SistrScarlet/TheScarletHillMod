package com.sistr.scarlethill.util;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class GoalHelper {

    @Nullable
    public static Vec3d getAwayPos(CreatureEntity goalOwner, Vec3d awayPos) {
        Vec3d farthestPos = null;
        for (int i = 0; i < 10; ++i) {
            Vec3d randomPos = RandomPositionGenerator.findRandomTargetBlockAwayFrom(goalOwner, 16, 7, awayPos);
            if (randomPos == null) continue;
            if (farthestPos == null || awayPos.squareDistanceTo(farthestPos) < awayPos.squareDistanceTo(randomPos)) {
                farthestPos = randomPos;
            }
        }
        return farthestPos;
    }

}
