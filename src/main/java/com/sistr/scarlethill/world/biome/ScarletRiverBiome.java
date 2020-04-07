package com.sistr.scarlethill.world.biome;

import com.sistr.scarlethill.world.Feature.ScarletDefaultFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class ScarletRiverBiome extends AbstractScarletBiome {

    public ScarletRiverBiome() {
        super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG)
                        .precipitation(Biome.RainType.RAIN).category(Biome.Category.RIVER)
                        .depth(-0.5F).scale(0.0F).temperature(0.5F).downfall(0.5F).waterColor(0xe4553f).waterFogColor(0x330a04).parent(null),
                new ExtensionBuilder().skyColor(0xf06d59).grassColor(0xad3a00).foliageColor(0xa60027));
        ScarletDefaultFeatures.addStructures(this);
    }

}
