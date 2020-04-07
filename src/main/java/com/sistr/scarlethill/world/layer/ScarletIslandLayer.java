package com.sistr.scarlethill.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum ScarletIslandLayer implements IAreaTransformer0 {
    INSTANCE;

    public int apply(INoiseRandom p_215735_1_, int p_215735_2_, int p_215735_3_) {
        if (p_215735_2_ == 0 && p_215735_3_ == 0) {
            return 1;
        } else {
            return p_215735_1_.random(10) == 0 ? 1 : ScarletLayerUtil.SCARLET_OCEAN_BIOME;
        }
    }
}