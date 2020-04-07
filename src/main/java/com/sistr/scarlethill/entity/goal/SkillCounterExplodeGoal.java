package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.util.EffectUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;

//攻撃を食らった時に、稀に吹き飛ばす
public class SkillCounterExplodeGoal extends SkillGoal {
    private final int chance;
    protected final float strength;
    protected final float radius;

    public SkillCounterExplodeGoal(MobEntity attacker, int startupLength, int actionLength, int freezeLength, int chance, float strength, float radius) {
        super(attacker, startupLength, actionLength, freezeLength);
        this.chance = chance;
        this.strength = strength;
        this.radius = radius;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldStart() {
        return this.goalOwner.getRNG().nextInt(this.chance) == 0 && this.goalOwner.hurtResistantTime == 19;
    }

    //単にぶっ飛ばすだけ
    @Override
    protected void actionStart() {

        World world = this.goalOwner.world;

        world.getEntitiesInAABBexcluding(this.goalOwner, this.goalOwner.getBoundingBox().grow(this.radius, this.radius, this.radius), entity ->
                entity.isAlive() && entity.canBeCollidedWith() && !entity.isSpectator()).forEach(this::knockback);

        world.playSound(null, this.goalOwner.getPosX(), this.goalOwner.getPosY(), this.goalOwner.getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, this.goalOwner.getSoundCategory(),
                1.0F, 0.8F + 0.2F * world.rand.nextFloat());

        EffectUtil.spawnParticleBox((ServerWorld) world, ParticleTypes.POOF, this.goalOwner.getPosX(), this.goalOwner.getPosY(), this.goalOwner.getPosZ(), 50, this.radius);
    }

    protected void knockback(Entity target) {
        if (target instanceof LivingEntity) {
            //そのままだと吹っ飛ばないのでクライアントへ変更を通知
            //通常、ダメージ時にされるため必要ないが、これはダメージを与えないので通知する必要がある
            target.velocityChanged = true;
            Vec3d toTargetVec = this.goalOwner.getPositionVec().subtract(target.getPositionVec()).normalize();
            ((LivingEntity) target).knockBack(this.goalOwner, this.strength, toTargetVec.x, toTargetVec.z);
        }
    }

}
