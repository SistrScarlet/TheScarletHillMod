package com.sistr.scarlethill.world.layer;

import com.google.common.collect.ImmutableList;
import com.sistr.scarlethill.setup.Registration;
import net.minecraftforge.common.BiomeManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ScarletBiomeManager {

    private static ArrayList<BiomeManager.BiomeEntry>[] biomes = setupBiomes();

    private static ArrayList<BiomeManager.BiomeEntry>[] setupBiomes() {
        @SuppressWarnings("unchecked")
        ArrayList<BiomeManager.BiomeEntry>[] currentBiomes = new ArrayList[BiomeManager.BiomeType.values().length];
        List<BiomeManager.BiomeEntry> list = new ArrayList<>();

        //温暖な気候帯のバイオーム

        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_FOREST_BIOME.get(), 10));
        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_MOUNTAIN_BIOME.get(), 10));
        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_PLAIN_BIOME.get(), 10));
        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_HILL_BIOME.get(), 10));

        currentBiomes[BiomeManager.BiomeType.WARM.ordinal()] = new ArrayList<>(list);
        list.clear();

        //普通の

        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_FOREST_BIOME.get(), 10));
        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_MOUNTAIN_BIOME.get(), 10));
        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_PLAIN_BIOME.get(), 10));
        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_HILL_BIOME.get(), 10));

        currentBiomes[BiomeManager.BiomeType.COOL.ordinal()] = new ArrayList<>(list);
        list.clear();

        //冷涼な

        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_SNOWY_TUNDRA_BIOME.get(), 30));
        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_HILL_BIOME.get(), 10));

        currentBiomes[BiomeManager.BiomeType.ICY.ordinal()] = new ArrayList<>(list);
        list.clear();

        //砂漠
        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_DESERT_BIOME.get(), 30));
        list.add(new BiomeManager.BiomeEntry(Registration.SCARLET_HILL_BIOME.get(), 10));

        currentBiomes[BiomeManager.BiomeType.DESERT.ordinal()] = new ArrayList<>(list);

        return currentBiomes;
    }

    @Nullable
    public static ImmutableList<BiomeManager.BiomeEntry> getBiomes(BiomeManager.BiomeType type) {
        int idx = type.ordinal();
        List<BiomeManager.BiomeEntry> list = idx >= biomes.length ? null : biomes[idx];

        return list != null ? ImmutableList.copyOf(list) : null;
    }

}
