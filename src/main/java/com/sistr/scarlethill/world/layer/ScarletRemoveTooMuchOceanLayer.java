package com.sistr.scarlethill.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum ScarletRemoveTooMuchOceanLayer implements ICastleTransformer {
    INSTANCE;

    public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
        return ScarletLayerUtil.isOcean(p_202748_6_) && ScarletLayerUtil.isOcean(p_202748_2_) && ScarletLayerUtil.isOcean(p_202748_3_) && ScarletLayerUtil.isOcean(p_202748_5_) && ScarletLayerUtil.isOcean(p_202748_4_) && p_202748_1_.random(2) == 0 ? 1 : p_202748_6_;
    }
}
