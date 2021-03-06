package com.sistr.scarlethill.setup;

import com.sistr.scarlethill.ForgeEventHandlers;
import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.network.Networking;
import com.sistr.scarlethill.world.Feature.ScarletDefaultFeatures;
import com.sistr.scarlethill.world.dimension.ModDimensions;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static net.minecraftforge.common.BiomeDictionary.Type.*;

@Mod.EventBusSubscriber(modid = ScarletHillMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    public static final ItemGroup ITEM_GROUP = new ItemGroup("scarlethill") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registration.SCARLET_STONE_BLOCK.get());
        }
    };

    public static void init(final FMLCommonSetupEvent event) {
        Networking.registerMessages();
        MinecraftForge.EVENT_BUS.register(ForgeEventHandlers.class);

        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
            if (BiomeDictionary.hasType(biome, OVERWORLD)) {
                ScarletDefaultFeatures.addOverWorldStructures(biome);
                if (BiomeDictionary.hasAnyType(biome) && BiomeDictionary.getTypes(biome).stream().anyMatch(type ->
                        (type == PLAINS || type == FOREST || type == SANDY || type == WASTELAND) && !(type == WATER || type == MOUNTAIN || type == HILLS))) {
                    biome.addStructure(Registration.SCARLET_PORTAL_STRUCTURE_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
                }
            }
        }
        BiomeDictionary.addTypes(Registration.SCARLET_OCEAN_BIOME.get(), OCEAN, MAGICAL);
        BiomeDictionary.addTypes(Registration.SCARLET_RIVER_BIOME.get(), RIVER, MAGICAL);
        BiomeDictionary.addTypes(Registration.SCARLET_FROZEN_RIVER_BIOME.get(), RIVER, COLD, SNOWY, MAGICAL);
        BiomeDictionary.addTypes(Registration.SCARLET_HILL_BIOME.get(), MOUNTAIN, HILLS, HOT, MAGICAL);
        BiomeDictionary.addTypes(Registration.SCARLET_PLAIN_BIOME.get(), PLAINS, MAGICAL);
        BiomeDictionary.addTypes(Registration.SCARLET_FOREST_BIOME.get(), FOREST, MAGICAL);
        BiomeDictionary.addTypes(Registration.SCARLET_MOUNTAIN_BIOME.get(), MOUNTAIN, HILLS, MAGICAL);
        BiomeDictionary.addTypes(Registration.SCARLET_DESERT_BIOME.get(), HOT, DRY, SANDY, MAGICAL);
        BiomeDictionary.addTypes(Registration.SCARLET_SNOWY_TUNDRA_BIOME.get(), COLD, SNOWY, WASTELAND, MAGICAL);

    }

    @SubscribeEvent
    public static void onDimensionRegistry(RegisterDimensionsEvent event) {
        ModDimensions.SCARLETHILL_TYPE = DimensionManager.registerOrGetDimension(ModDimensions.DIMENSION_ID, Registration.SCARLETHILL_DIM.get(), null, true);
    }


}
