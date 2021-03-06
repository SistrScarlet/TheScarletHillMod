package com.sistr.scarlethill.entity.projectile;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class MagmaProjectileEntity extends AbstractProjectile implements IRendersAsItem {

    public MagmaProjectileEntity(EntityType<? extends MagmaProjectileEntity> type, World world) {
        super(type, world);
    }

    public MagmaProjectileEntity(LivingEntity shooter, World world) {
        super(Registration.MAGMA_PROJECTILE.get(), shooter, world);
        this.setHitSound(SoundEvents.BLOCK_LAVA_EXTINGUISH);
    }

    @Override
    public float getVelocity() {
        return 1F;
    }

    @Override
    public float getGravity() {
        return 0.01F;
    }

    @Override
    public float getBlurAngle() {
        return 5F;
    }

    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        this.remove();
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult result) {
        Entity entity = result.getEntity();
        entity.hurtResistantTime = 0;
        super.onEntityHit(result);
    }

    @Override
    protected DamageSource getDamageType(DamageSource source) {
        return source.setFireDamage();
    }

    @Override
    protected void addHitEffect(LivingEntity living) {
        living.setFire(5);
    }

    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public ItemStack getItem() {
        return Registration.LAVA_SPIT_ITEM.get().getDefaultInstance();
    }

}
