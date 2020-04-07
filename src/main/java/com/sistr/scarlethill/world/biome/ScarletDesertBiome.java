package com.sistr.scarlethill.world.biome;

import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.world.Feature.ScarletDefaultFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class ScarletDesertBiome extends AbstractScarletBiome {

    public ScarletDesertBiome() {
        super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, new SurfaceBuilderConfig(Registration.SCARLET_SAND_BLOCK.get().getDefaultState(), Registration.SCARLET_SAND_BLOCK.get().getDefaultState(), SurfaceBuilder.GRAVEL))
                        .precipitation(Biome.RainType.NONE).category(Biome.Category.DESERT)
                        .depth(0.125F).scale(0.05F).temperature(2.0F).downfall(0.0F).waterColor(0xe4553f).waterFogColor(0x330a04).parent(null),
                new ExtensionBuilder().skyColor(0xf06d59).grassColor(0xad3a00).foliageColor(0xa60027));
        this.addStructure(Registration.SCARLET_PORTAL_STRUCTURE_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        ScarletDefaultFeatures.addStructures(this);
    }
}
