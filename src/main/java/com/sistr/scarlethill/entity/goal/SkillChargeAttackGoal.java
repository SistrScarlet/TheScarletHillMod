package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.util.EffectUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;

public class SkillChargeAttackGoal extends SkillAttackGoal {
    private final double chargeSpeed;
    protected double movePosX;
    protected double movePosZ;

    public SkillChargeAttackGoal(MobEntity attacker, int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, double chargeSpeed) {
        super(attacker, startupLength, actionLength, freezeLength, chance, damage, range, 5, 16);
        this.chargeSpeed = chargeSpeed;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    protected boolean shouldStart() {
        //使えなそうな状況を除外
        if (this.goalOwner.isBeingRidden() && !this.goalOwner.onGround) {
            return false;
        }

        //別のAIでまずターゲットを指定する必要アリ
        LivingEntity chargeTarget = this.goalOwner.getAttackTarget();
        if (chargeTarget == null) {
            return false;
        }

        //nTickに一回だけ処理。
        if (this.goalOwner.getRNG().nextInt(this.chance) == 0) {
            if (checkTargetDistance()) {
                BlockRayTraceResult result = getMoveDestination(chargeTarget);
                if (result.getType() != RayTraceResult.Type.MISS) {
                    BlockPos hitPos = result.getPos();
                    this.movePosX = hitPos.getX();
                    this.movePosZ = hitPos.getZ();
                    return true;
                }

            }
        }

        return false;
    }

    protected BlockRayTraceResult getMoveDestination(LivingEntity chargeTarget) {
        //敵対象の座標ではなく、その背後のブロックを狙う。
        Vec3d startPos = this.goalOwner.getPositionVec().add(0, this.goalOwner.getEyeHeight(), 0);
        Vec3d endPos = chargeTarget.getPositionVec().add(0, 1, 0);
        endPos = startPos.add(endPos.subtract(startPos).scale(2D));
        return this.goalOwner.world.rayTraceBlocks(new RayTraceContext(startPos, endPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.goalOwner));
    }

    @Override
    protected void actionTick() {

        //側面にぶつかった場合停止
        //onGroundが無い場合段差でも止まる
        //どうやらonGroundは移動後にYが変動しているか否かで判定しているらしい
        if (10 < this.timer && this.goalOwner.collidedHorizontally && this.goalOwner.onGround) {
            setStatus(SkillStatus.FREEZE);
        }

        //地点に着いたら終了硬直へ行く
        if (this.goalOwner.getPositionVec().squareDistanceTo(this.movePosX, this.goalOwner.getPosY(), this.movePosZ) < 1) {
            setStatus(SkillStatus.FREEZE);
        }

        //地面に接しているか泳いでいる間、横に進む
        if (this.goalOwner.onGround || this.goalOwner.isSwimming()) {
            this.goalOwner.setMotion(new Vec3d(this.movePosX, 0, this.movePosZ)
                    .subtract(this.goalOwner.getPosX(), 0, this.goalOwner.getPosZ())
                    .add(this.goalOwner.getMotion().scale(0.5)).normalize().scale(this.chargeSpeed));
        }

        //一定範囲内に体当たり攻撃。リストの取得はLivingEntityのcollideWithNearbyEntities()から拝借
        for (Entity entity : this.goalOwner.world.getEntitiesInAABBexcluding(this.goalOwner, this.goalOwner.getBoundingBox().grow(this.range, this.range, this.range).offset(0, this.range, 0), EntityPredicates.pushableBy(this.goalOwner))) {
            //ダメージを食らったら上に吹き飛ぶ
            //毎tick発動してるので、このifが無いと超絶吹き飛ぶ
            if (entity.attackEntityFrom(DamageSource.causeMobDamage(this.goalOwner), this.damage)) {
                entity.setMotion(entity.getMotion().add(0, this.chargeSpeed / 2, 0));
            }
        }

    }

    @Override
    protected void freezeStart() {
        //停止する
        this.goalOwner.setMotion(Vec3d.ZERO);
        //周囲の敵に風圧ダメージ。威力は低いが範囲が広め
        float radius = this.range * 2;
        World world = this.goalOwner.world;
        for (Entity entity : world.getEntitiesInAABBexcluding(this.goalOwner, this.goalOwner.getBoundingBox().grow(radius, radius, radius), EntityPredicates.pushableBy(this.goalOwner))) {
            entity.attackEntityFrom(DamageSource.causeMobDamage(this.goalOwner), this.damage / 4);
            //範囲内の全エンティティを放射線状に吹き飛ばす
            if (entity instanceof LivingEntity) {
                Vec3d vec = entity.getPositionVec().subtract(this.goalOwner.getPositionVec()).normalize();
                ((LivingEntity) entity).knockBack(this.goalOwner, (float) this.chargeSpeed / 4F, vec.x, vec.z);
            }
        }
        world.playSound(null, this.goalOwner.getPosition(), SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, this.goalOwner.getSoundCategory(), 1.0F, 0.8F + this.goalOwner.getRNG().nextFloat() * 0.1F);

        EffectUtil.spawnParticleBox((ServerWorld) world, ParticleTypes.POOF, this.goalOwner.getPosX(), this.goalOwner.getPosY(), this.goalOwner.getPosZ(), 50, radius);

    }

}
