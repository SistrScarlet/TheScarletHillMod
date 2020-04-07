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
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

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

        for (Biome biome : Biome.BIOMES) {
            ScarletDefaultFeatures.addOverWorldStructures(biome);
            if (biome == Biomes.PLAINS || biome == Biomes.DESERT || biome == Biomes.SAVANNA || biome == Biomes.SNOWY_TUNDRA || biome == Biomes.TAIGA) {
                biome.addStructure(Registration.SCARLET_PORTAL_STRUCTURE_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));

            }
        }

    }

    @SubscribeEvent
    public static void onDimensionRegistry(RegisterDimensionsEvent event) {
        ModDimensions.SCARLETHILL_TYPE = DimensionManager.registerOrGetDimension(ModDimensions.DIMENSION_ID, Registration.SCARLETHILL_DIM.get(), null, true);
    }


}
