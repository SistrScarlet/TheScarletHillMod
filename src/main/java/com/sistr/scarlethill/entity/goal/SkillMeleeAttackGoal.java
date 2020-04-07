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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;
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

    public SkillMeleeAttackGoal(MobEntity attacker, int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, AttackRangeShape shape) {
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
                attackByTriangle();
            case ROUND:
            case SQUARE:
        }
    }

    //あんまりにもクソ長いので別メソッドとして処理
    //大雑把に言うと、開始地点の異なる4本の線でレイトレースしている
    //線と線の間？当然何も判定しとりません。
    //今数学の勉強中なので、いい感じのを思いついたら独自の当たり判定に切り替えたい
    private void attackByTriangle() {
        Vec3d targetPos = new Vec3d(this.attackPosX, this.attackPosY, this.attackPosZ);
        Vec3d attackerPos = new Vec3d(this.goalOwner.getPosX(), this.goalOwner.getPosY() + this.goalOwner.getEyeHeight(), this.goalOwner.getPosZ());

        //攻撃者から敵対象が居る地点へのベクトル。直接レイトレースには使わない
        Vec3d toTargetVec = targetPos.subtract(attackerPos).normalize();

        double radius = this.range / 4;
        float rotate = 90F * ((float) Math.PI / 180F);

        //このベクトル自体は直接使用しない。真横に線を伸ばす
        Vec3d rightVec = toTargetVec.rotateYaw(rotate).scale(radius);
        Vec3d leftVec = toTargetVec.rotateYaw(-rotate).scale(radius);

        //これも使わない。線を上下に分割
        Vec3d rightTopVec = rightVec.add(0, radius, 0);
        Vec3d rightBottomVec = rightVec.subtract(0, radius, 0);
        Vec3d leftTopVec = leftVec.add(0, radius, 0);
        Vec3d leftBottomVec = leftVec.subtract(0, radius, 0);

        //実際にレイトレースに使用する。線の先っぽの地点
        Vec3d rightTopPos = attackerPos.add(rightTopVec);
        Vec3d rightBottomPos = attackerPos.add(rightBottomVec);
        Vec3d leftTopPos = attackerPos.add(leftTopVec);
        Vec3d leftBottomPos = attackerPos.add(leftBottomVec);

        double range = this.range * 1.5D;

        //レイトレースの終点。大体の場合、敵対象の背後の地点になる
        Vec3d toTargetPos = attackerPos.add(toTargetVec.scale(range));

        World world = this.goalOwner.world;

        //周囲の敵のリスト
        List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, this.goalOwner.getBoundingBox().grow(range), (entity ->
                entity.isAlive() && entity.canBeCollidedWith() && !entity.isSpectator() && entity != this.goalOwner));

        //攻撃範囲内の敵のリスト
        List<Entity> targetList = Lists.newArrayList();

        //レイトレースx5。要素がめちゃ重複するかも
        rayTraceEntity(rightTopPos, toTargetPos, entityList, targetList);
        rayTraceEntity(rightBottomPos, toTargetPos, entityList, targetList);
        rayTraceEntity(leftTopPos, toTargetPos, entityList, targetList);
        rayTraceEntity(leftBottomPos, toTargetPos, entityList, targetList);
        rayTraceEntity(attackerPos, toTargetPos, entityList, targetList);

        DamageSource source = DamageSource.causeMobDamage(this.goalOwner);

        if (!targetList.isEmpty()) {
            world.playSound(null, this.goalOwner.getPosition(), SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, this.goalOwner.getSoundCategory(), 1.0F, 1.2F);
            //重複は省いて、範囲内のエンティティにダメージを与える
            targetList.stream().distinct().forEach((entity -> {
                if (entity.attackEntityFrom(source, this.damage) && entity instanceof LivingEntity) {
                    float pi = (float) Math.PI;
                    ((LivingEntity) entity).knockBack(this.goalOwner, this.damage / 10, MathHelper.sin(this.goalOwner.rotationYaw * (pi / 180F)), -MathHelper.cos(this.goalOwner.rotationYaw * (pi / 180F)));
                }
            }));
        }

        //パーティクル演出
        if (world instanceof ServerWorld) {
            EffectUtil.spawnParticleLine((ServerWorld) world, ParticleTypes.CRIT, rightTopPos, toTargetPos, 8, 0.2D);
            EffectUtil.spawnParticleLine((ServerWorld) world, ParticleTypes.CRIT, rightBottomPos, toTargetPos, 8, 0.2D);
            EffectUtil.spawnParticleLine((ServerWorld) world, ParticleTypes.CRIT, leftTopPos, toTargetPos, 8, 0.2D);
            EffectUtil.spawnParticleLine((ServerWorld) world, ParticleTypes.CRIT, leftBottomPos, toTargetPos, 8, 0.2D);
            EffectUtil.spawnParticleLine((ServerWorld) world, ParticleTypes.CRIT, attackerPos, toTargetPos, 8, 0.2D);
        }

    }

    //始点から終点までの座標上の全エンティティを、targetのリストに加える
    //一行でできるならわざわざメソッド化しなくてもよかったかもしれない
    private void rayTraceEntity(Vec3d start, Vec3d end, List<Entity> check, List<Entity> target) {
        check.stream().filter(entity -> entity.getBoundingBox().rayTrace(start, end).isPresent()).forEach(target::add);
    }

    //攻撃の形状。実際には立体となる
    public enum AttackRangeShape {
        TRIANGLE,
        ROUND,
        SQUARE
    }
}
