package com.sistr.scarlethill.world.biome;

import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.world.Feature.ScarletDefaultFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import static net.minecraft.world.gen.surfacebuilders.SurfaceBuilder.GRAVEL;

public class ScarletHillBiome extends AbstractScarletBiome {

    public ScarletHillBiome() {
        super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, new SurfaceBuilderConfig(Registration.SCARLET_STONE_BLOCK.get().getDefaultState(), Registration.SCARLET_STONE_BLOCK.get().getDefaultState(), GRAVEL))
                        .precipitation(Biome.RainType.RAIN).category(Biome.Category.EXTREME_HILLS)
                        .depth(1.5F).scale(1.5F).temperature(1.0F).downfall(0.0F).waterColor(0xe62d10).waterFogColor(0x4a0b01).parent(null),
                new ExtensionBuilder().skyColor(0xf06d59).grassColor(0xb82800).foliageColor(0xba001f));
        this.addStructure(Registration.SCARLET_BEAR_NEST_STRUCTURE_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        ScarletDefaultFeatures.addStructures(this);
    }

}
