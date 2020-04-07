package com.sistr.scarlethill.world.layer;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum ScarletRiverLayer implements ICastleTransformer {
    INSTANCE;

    public static final int SCARLET_RIVER = Registry.BIOME.getId(Registration.SCARLET_RIVER_BIOME.get());

    public int apply(INoiseRandom context, int north, int west, int south, int east, int center) {
        int i = riverFilter(center);
        return i == riverFilter(east) && i == riverFilter(north) && i == riverFilter(west) && i == riverFilter(south) ? -1 : SCARLET_RIVER;
    }

    private static int riverFilter(int p_151630_0_) {
        return p_151630_0_ >= 2 ? 2 + (p_151630_0_ & 1) : p_151630_0_;
    }
}