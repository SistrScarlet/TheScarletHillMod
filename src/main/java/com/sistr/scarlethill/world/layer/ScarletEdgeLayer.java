package com.sistr.scarlethill.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public class ScarletEdgeLayer {
    public enum Special implements IC0Transformer {
        INSTANCE;

        Special() {
        }

        public int apply(INoiseRandom p_202726_1_, int p_202726_2_) {
            if (!ScarletLayerUtil.isOcean(p_202726_2_) && p_202726_1_.random(13) == 0) {
                p_202726_2_ |= 1 + p_202726_1_.random(15) << 8 & 3840;
            }

            return p_202726_2_;
        }
    }

    public enum HeatIce implements ICastleTransformer {
        INSTANCE;

        HeatIce() {
        }

        public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
            return p_202748_6_ != 4 || p_202748_2_ != 1 && p_202748_3_ != 1 && p_202748_5_ != 1 && p_202748_4_ != 1 && p_202748_2_ != 2 && p_202748_3_ != 2 && p_202748_5_ != 2 && p_202748_4_ != 2 ? p_202748_6_ : 3;
        }
    }

    public enum CoolWarm implements ICastleTransformer {
        INSTANCE;

        CoolWarm() {
        }

        public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
            return p_202748_6_ != 1 || p_202748_2_ != 3 && p_202748_3_ != 3 && p_202748_5_ != 3 && p_202748_4_ != 3 && p_202748_2_ != 4 && p_202748_3_ != 4 && p_202748_5_ != 4 && p_202748_4_ != 4 ? p_202748_6_ : 2;
        }
    }
}