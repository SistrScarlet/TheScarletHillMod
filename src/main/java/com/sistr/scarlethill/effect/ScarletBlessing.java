package com.sistr.scarlethill.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

//こいつの効果はEventにて実装
//あらゆるダメージを無効化する
public class ScarletBlessing extends Effect {

    public ScarletBlessing() {
        super(EffectType.BENEFICIAL, 16720896);
        this.addAttributesModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, "1B5F7DE9-1546-EA98-34AF-1B6D7C9C67EA", 1.0D, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void performEffect(LivingEntity living, int amplifier) {
        living.world.addParticle(ParticleTypes.FLAME, living.getPosX() + (living.world.rand.nextFloat() * 2 - 1), living.getPosY() + living.getEyeHeight() + (living.world.rand.nextFloat() * 2 - 1), living.getPosZ() + (living.world.rand.nextFloat() * 2 - 1), 0, 0, 0);
    }

    @Override
    public boolean isReady(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
        return 1D;
    }
}
