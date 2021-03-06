package com.sistr.scarlethill.effect;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

//およそ尋常ではない緋色の炎
//一定時間ごとにダメージ
//ただし緋色の加護保有時は緋色の加護の効果を延長する
public class ScarletBlazing extends Effect {

    public ScarletBlazing() {
        super(EffectType.NEUTRAL, 0xAA1200);
    }

    @Override
    public void performEffect(LivingEntity living, int amplifier) {
        living.world.addParticle(ParticleTypes.FLAME, living.getPosX() + (living.world.rand.nextFloat() * 2 - 1), living.getPosY() + living.getEyeHeight() + (living.world.rand.nextFloat() * 2 - 1), living.getPosZ() + (living.world.rand.nextFloat() * 2 - 1), 0, 0, 0);
        if (!living.isPotionActive(Registration.SCARLET_BLESSING_EFFECT.get())) {
            if (living.ticksExisted % 5 == 0) {
                living.attackEntityFrom(DamageSource.MAGIC.setFireDamage(), amplifier + 1);
            }
            return;
        }
        EffectInstance oldEffect = living.getActivePotionEffect(Registration.SCARLET_BLESSING_EFFECT.get());
        EffectInstance newEffect = new EffectInstance(Registration.SCARLET_BLESSING_EFFECT.get(), Math.max(20, oldEffect.getDuration()), oldEffect.getAmplifier(), oldEffect.isAmbient(), oldEffect.doesShowParticles(), oldEffect.isShowIcon());
        living.addPotionEffect(newEffect);
        living.removePotionEffect(Registration.SCARLET_BLAZING_EFFECT.get());
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
}
