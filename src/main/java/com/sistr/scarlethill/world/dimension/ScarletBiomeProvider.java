package com.sistr.scarlethill.world.dimension;

import com.google.common.collect.ImmutableSet;
import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.world.layer.ScarletLayerUtil;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.gen.layer.Layer;

import java.util.Set;

public class ScarletBiomeProvider extends BiomeProvider {
    private final Layer genBiomes;
    private static final Set<Biome> biomes;

    public ScarletBiomeProvider(OverworldBiomeProviderSettings settingsProvider) {
        super(biomes);
        this.genBiomes = ScarletLayerUtil.func_227474_a_(settingsProvider.getSeed(), settingsProvider.getWorldType(), settingsProvider.getGeneratorSettings());
    }

    public Biome getNoiseBiome(int x, int y, int z) {
        return this.genBiomes.func_215738_a(x, z);
    }

    static {
        biomes = ImmutableSet.of(
                Registration.SCARLET_HILL_BIOME.get(),
                Registration.SCARLET_OCEAN_BIOME.get(),
                Registration.SCARLET_PLAIN_BIOME.get(),
                Registration.SCARLET_DESERT_BIOME.get(),
                Registration.SCARLET_MOUNTAIN_BIOME.get(),
                Registration.SCARLET_FOREST_BIOME.get(),
                Registration.SCARLET_RIVER_BIOME.get(),
                Registration.SCARLET_FROZEN_RIVER_BIOME.get(),
                Registration.SCARLET_SNOWY_TUNDRA_BIOME.get()
        );
    }
}
