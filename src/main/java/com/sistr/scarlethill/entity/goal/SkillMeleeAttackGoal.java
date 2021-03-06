package com.sistr.scarlethill.entity.goal;

import com.google.common.collect.Lists;
import com.sistr.scarlethill.util.EffectUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

//ぶん殴るスキル
//振り上げて、敵が居た地点に攻撃を行う
//rangeはぶん殴りを開始する閾値。実際はもう少し長くまで攻撃が届く
//攻撃範囲が三角の場合、rangeが大きいと小さなモブがすり抜けてしまうが、どう仕様もない
//rangeを5以上にするとプレイヤーですら判定がすり抜ける
public class SkillMeleeAttackGoal extends SkillAttackGoal {
    private final AttackRangeShape shape;
    private double attackPosX;
    private double attackPosY;
    private double attackPosZ;

    public SkillMeleeAttackGoal(MobEntity attacker, int startupLength, int actionLength, int freezeLength,
                                int chance, float damage, float range, AttackRangeShape shape) {
        super(attacker, startupLength, actionLength, freezeLength, chance, damage, range, 0, range);
        this.shape = shape;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    protected void readyTick() {
        this.goalOwner.getLookController().setLookPosition(this.attackPosX, this.attackPosY, this.attackPosY, 30, 30);
    }

    @Override
    protected void actionTick() {
        this.goalOwner.getLookController().setLookPosition(this.attackPosX, this.attackPosY, this.attackPosY, 30, 30);
    }

    @Override
    public boolean shouldStart() {

        //ターゲット無しなら発動しない
        LivingEntity target = this.goalOwner.getAttackTarget();
        if (target == null) {
            return false;
        }

        //範囲内に入ったら発動
        if (this.goalOwner.getRNG().nextInt(this.chance) == 0 && checkTargetDistance()) {
            this.attackPosX = target.getPosX();
            this.attackPosY = target.getPosY() + target.getEyeHeight();
            this.attackPosZ = target.getPosZ();
            return true;
        }

        return false;
    }

    //ぶん殴りの実際の処理。三角形以外は処理自体存在しない。
    @Override
    protected void actionStart() {
        switch (this.shape) {
            case TRIANGLE:
                attackByTriangle(this.goalOwner.world, this.goalOwner,
                        new Vec3d(this.attackPosX, this.attackPosY, this.attackPosZ),
                        this.damage, this.range * 1.5, this.range / 2);
            case ROUND:
            case SQUARE:
        }
    }

    //敵が範囲内なら攻撃する
    public static void attackByTriangle(World world, LivingEntity attacker, Vec3d attackPos, float damage, double length, double width) {
        Vec3d attackerPos = new Vec3d(attacker.getPosX(), attacker.getPosY() + attacker.getEyeHeight(), attacker.getPosZ());

        //攻撃者から敵対象が居る地点へのベクトル。当たり判定には直接使わない
        Vec3d toTargetVec = attackPos.subtract(attackerPos).normalize();

        //終点。大体の場合、敵対象の背後の地点になる
        //壁貫通はしない
        Vec3d toTargetPos = attackerPos.add(toTargetVec.scale(length));
        BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(toTargetVec, toTargetPos,
                RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, attacker));
        if (result.getType() != RayTraceResult.Type.MISS) {
            toTargetPos = result.getHitVec();
        }

        //周囲の敵のリスト
        List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, attacker.getBoundingBox().grow(length), (entity ->
                entity.isAlive() && entity.canBeCollidedWith() && !entity.isSpectator() && entity != attacker));

        //攻撃範囲内の敵のリスト
        List<Entity> targetList = checkAttackRange(attackerPos, toTargetPos, entityList, width, length);

        if (!targetList.isEmpty()) {
            world.playSound(null, attacker.getPosition(),
                    SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, attacker.getSoundCategory(), 1.0F, 1.2F);
            //重複は省いて、範囲内のエンティティにダメージを与える
            targetList.stream().distinct().forEach((entity -> {
                DamageSource source = DamageSource.causeMobDamage(attacker);
                if (entity.attackEntityFrom(source, damage) && entity instanceof LivingEntity) {
                    float pi = (float) Math.PI;
                    ((LivingEntity) entity).knockBack(attacker, damage / 10,
                            MathHelper.sin(attacker.rotationYaw * (pi / 180F)),
                            -MathHelper.cos(attacker.rotationYaw * (pi / 180F)));
                }
            }));
        }

        //パーティクル演出
        //横向き二次関数グラフを横に三つ並べて爪っぽく
        Vec3d rotVec = toTargetVec.rotateYaw(90).scale(width / 2);
        EffectUtil.spawnParticleQuadraticLineHorizon((ServerWorld) world, ParticleTypes.CRIT,
                attackerPos.add(0, 1, 0).add(rotVec), toTargetPos.add(rotVec), 32, 0D);
        EffectUtil.spawnParticleQuadraticLineHorizon((ServerWorld) world, ParticleTypes.CRIT,
                attackerPos.add(0, 1, 0), toTargetPos, 32, 0D);
        EffectUtil.spawnParticleQuadraticLineHorizon((ServerWorld) world, ParticleTypes.CRIT,
                attackerPos.add(0, 1, 0).subtract(rotVec), toTargetPos.subtract(rotVec), 32, 0D);
    }

    //リストのエンティティが攻撃範囲に含まれるかチェックして返す
    //点(entity)と線(attacker-target)の距離を調べ、width以下ならヒット
    //entityがattackerから離れるほどwidthは減る
    //attackerと同じ位置の場合のwidthは100%、maxLengthまで離れるとwidthが0%になる
    //減り方は線形
    public static List<Entity> checkAttackRange(Vec3d attackerPos, Vec3d toTargetPos, List<Entity> entityList, double width, double maxLength) {
        List<Entity> hitEntity = Lists.newArrayList();
        Iterator<Entity> iterator = entityList.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            double x;
            //xをattacker-target間に収める
            if (attackerPos.getX() < entity.getPosX() && entity.getPosX() < toTargetPos.getX()
                    || toTargetPos.getX() < entity.getPosX() && entity.getPosX() < attackerPos.getX()) {
                x = entity.getPosX();
            } else {
                if (Math.abs(attackerPos.getX() - entity.getPosX()) < Math.abs(toTargetPos.getX() - entity.getPosX())) {
                    x = attackerPos.getX();
                } else {
                    x = toTargetPos.getX();
                }
            }
            //attackerPosとtoTargetPosの二点を通る直線の方程式から、zを求める
            double z = ((toTargetPos.getZ() - attackerPos.getZ()) / (toTargetPos.getX() - attackerPos.getX())) * (x - attackerPos.getX()) + attackerPos.getZ();
            //点(x, z)とentityの距離
            double d = MathHelper.sqrt((entity.getPosX() - x) * (entity.getPosX() - x) + (entity.getPosZ() - z) * (entity.getPosZ() - z));
            //点とentityとの距離がwidth以下ならtrue
            //値の調整として、距離はentityの幅分減らす。また、widthはentity-attackerPos間の距離が遠いほど狭まる
            if (d - entity.getWidth() < width - (attackerPos.distanceTo(entity.getPositionVec()) / maxLength) * width) {
                hitEntity.add(entity);
                iterator.remove();
            }
        }
        iterator = entityList.iterator();
        //方角によって判定がおかしくなるのでxz入れ替えてもう一度
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            double z;
            //zをattacker-target間に収める
            if (attackerPos.getZ() < entity.getPosZ() && entity.getPosZ() < toTargetPos.getZ()
                    || toTargetPos.getZ() < entity.getPosZ() && entity.getPosZ() < attackerPos.getZ()) {
                z = entity.getPosZ();
            } else {
                if (Math.abs(attackerPos.getZ() - entity.getPosZ()) < Math.abs(toTargetPos.getZ() - entity.getPosZ())) {
                    z = attackerPos.getZ();
                } else {
                    z = toTargetPos.getZ();
                }
            }
            //attackerPosとtoTargetPosの二点を通る直線の方程式から、xを求める
            double x = ((toTargetPos.getX() - attackerPos.getX()) / (toTargetPos.getZ() - attackerPos.getZ())) * (z - attackerPos.getZ()) + attackerPos.getX();
            //点(x, z)とentityの距離
            double d = MathHelper.sqrt((entity.getPosZ() - z) * (entity.getPosZ() - z) + (entity.getPosX() - x) * (entity.getPosX() - x));
            //点とentityとの距離がwidth以下ならtrue
            //値の調整として、距離はentityの幅分減らす。また、widthはentity-attackerPos間の距離が遠いほど狭まる
            if (d - entity.getWidth() < width - (attackerPos.distanceTo(entity.getPositionVec()) / maxLength) * width) {
                hitEntity.add(entity);
                iterator.remove();
            }
        }
        return hitEntity;
    }

    //攻撃の形状。実際には立体となる
    public enum AttackRangeShape {
        TRIANGLE,
        ROUND,
        SQUARE
    }
}
