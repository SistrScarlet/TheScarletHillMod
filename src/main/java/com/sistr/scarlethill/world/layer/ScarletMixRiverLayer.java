package com.sistr.scarlethill.world.layer;

import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.world.biome.AbstractScarletBiome;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum ScarletMixRiverLayer implements IAreaTransformer2, IDimOffset0Transformer {
    INSTANCE;

    public static final int SCARLET_RIVER = Registry.BIOME.getId(Registration.SCARLET_RIVER_BIOME.get());

    public int apply(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_) {
        int i = p_215723_2_.getValue(this.func_215721_a(p_215723_4_), this.func_215722_b(p_215723_5_));
        int j = p_215723_3_.getValue(this.func_215721_a(p_215723_4_), this.func_215722_b(p_215723_5_));
        if (ScarletLayerUtil.isOcean(i)) {
            return i;
        } else if (j == SCARLET_RIVER) {
            Biome biome = Registry.BIOME.getByValue(i);
            return biome instanceof AbstractScarletBiome ? Registry.BIOME.getId(biome.getRiver()) : i;
        } else {
            return i;
        }
    }
}