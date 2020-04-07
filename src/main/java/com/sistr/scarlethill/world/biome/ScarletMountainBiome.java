package com.sistr.scarlethill.world.biome;

import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.world.Feature.ScarletDefaultFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class ScarletMountainBiome extends AbstractScarletBiome {

    public ScarletMountainBiome() {
        super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG)
                        .precipitation(Biome.RainType.RAIN).category(Biome.Category.EXTREME_HILLS)
                        .depth(6.0F).scale(0.5F).temperature(0.2F).downfall(0.3F).waterColor(0xe4553f).waterFogColor(0x330a04).parent(null),
                new ExtensionBuilder().skyColor(0xf06d59).grassColor(0xad3a00).foliageColor(0xa60027));
        this.addStructure(Registration.MOLTEN_MINE_STRUCTURE_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        ScarletDefaultFeatures.addStructures(this);
        ScarletDefaultFeatures.addFreezeTopLayer(this);
    }

}
