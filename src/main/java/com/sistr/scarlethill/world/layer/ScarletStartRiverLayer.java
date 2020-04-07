package com.sistr.scarlethill.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public enum ScarletStartRiverLayer implements IC0Transformer {
    INSTANCE;

    public int apply(INoiseRandom context, int value) {
        return ScarletLayerUtil.isOcean(value) ? value : context.random(299999) + 2;
    }
}