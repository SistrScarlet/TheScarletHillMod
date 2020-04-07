package com.sistr.scarlethill.world.layer;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import net.minecraftforge.common.BiomeManager;

import java.util.ArrayList;
import java.util.List;

public class ScarletBiomeLayer implements IC0Transformer {
    private final int settings;
    //この配列の長さは4 (四つの気候帯)
    @SuppressWarnings("unchecked")
    private List<BiomeManager.BiomeEntry>[] biomes = new ArrayList[BiomeManager.BiomeType.values().length];

    public ScarletBiomeLayer(WorldType worldType, int settings) {
        //4回のループでそれぞれの気候帯のバイオームをゲットする
        //ScarletBiomeManagerにてバイオームを指定しているが、こちらのクラスに持ってきてもいいかも？
        for (BiomeManager.BiomeType type : BiomeManager.BiomeType.values()) {
            ImmutableList<BiomeManager.BiomeEntry> biomesToAdd = ScarletBiomeManager.getBiomes(type);
            int idx = type.ordinal();

            if (biomes[idx] == null) biomes[idx] = new ArrayList<>();
            if (biomesToAdd != null) biomes[idx].addAll(biomesToAdd);
        }

        this.settings = settings;
    }

    public int apply(INoiseRandom context, int value) {
        if (this.settings >= 0) {
            return this.settings;
        } else {
            value = value & -3841;
            if (!ScarletLayerUtil.isOcean(value)) {
                switch (value) {
                    case 1:
                        return Registry.BIOME.getId(getWeightedBiomeEntry(BiomeManager.BiomeType.DESERT, context).biome);
                    case 2:
                        return Registry.BIOME.getId(getWeightedBiomeEntry(BiomeManager.BiomeType.WARM, context).biome);
                    case 3:
                        return Registry.BIOME.getId(getWeightedBiomeEntry(BiomeManager.BiomeType.COOL, context).biome);
                    case 4:
                        return Registry.BIOME.getId(getWeightedBiomeEntry(BiomeManager.BiomeType.ICY, context).biome);
                }
            }

            return value;

        }
    }

    protected BiomeManager.BiomeEntry getWeightedBiomeEntry(BiomeManager.BiomeType type, INoiseRandom context) {
        List<BiomeManager.BiomeEntry> biomeList = biomes[type.ordinal()];
        int totalWeight = WeightedRandom.getTotalWeight(biomeList);
        int weight = BiomeManager.isTypeListModded(type) ? context.random(totalWeight) : context.random(totalWeight / 10) * 10;
        return WeightedRandom.getRandomItem(biomeList, weight);
    }
}