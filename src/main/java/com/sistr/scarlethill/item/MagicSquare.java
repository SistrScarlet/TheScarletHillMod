package com.sistr.scarlethill.item;

import com.google.common.collect.Lists;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class MagicSquare {
    public final World world;
    public final UUID uniqueId;
    public final Vec3d center;
    public final Vec3d color;
    public final List<Vec3d> vertexes = Lists.newArrayList();
    public int ticksExisted;
    public int canExistTicks;
    public boolean removed;
    public final float radius;

    public MagicSquare(World world, UUID uniqueId, Vec3d center, Vec3d color, float radius, int canExistTicks) {
        this.world = world;
        this.uniqueId = uniqueId;
        this.center = center;
        this.color = color;
        this.radius = radius;
        this.canExistTicks = canExistTicks;
    }

    public void tick() {
        ticksExisted++;
        if (canExistTicks < ticksExisted) {
            removed = true;
        }
    }

    public boolean canAddVertex(Vec3d newVertex) {
        float length = (float) center.subtract(newVertex).length();
        //円の半径の10%以内に点が無い場合はfalse
        if (!(radius * 0.9F < length && length < radius * 1.1F)) {
            return false;
        }
        //頂点のリストが空っぽの場合はtrue
        if (vertexes.isEmpty()) {
            return true;
        }
        //頂点どうしの距離が近すぎる場合はfalse
        for (Vec3d vertex : vertexes) {
            if (vertex.lengthSquared() < radius * 0.9 * radius * 0.9) {
                return false;
            }
        }
        //問題無ければtrue
        return true;
    }

    public void addVertex(Vec3d newVertex) {
        this.vertexes.add(newVertex);
    }

}
