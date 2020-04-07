package com.sistr.scarlethill.world.layer;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.EdgeBiomeLayer;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

import java.util.function.LongFunction;

public class ScarletLayerUtil {
    protected static final int SCARLET_OCEAN_BIOME = Registry.BIOME.getId(Registration.SCARLET_OCEAN_BIOME.get());

    public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> repeat(long seed, IAreaTransformer1 parent, IAreaFactory<T> iAreaFactoryIn, int count, LongFunction<C> contextFactory) {
        IAreaFactory<T> iAreaFactory = iAreaFactoryIn;

        for (int i = 0; i < count; ++i) {
            iAreaFactory = parent.apply(contextFactory.apply(seed + (long) i), iAreaFactory);
        }

        return iAreaFactory;
    }

    public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> func_227475_a_(WorldType worldType, OverworldGenSettings settings, LongFunction<C> contextFactory) {
        IAreaFactory<T> islandLayer = ScarletIslandLayer.INSTANCE.apply(contextFactory.apply(1L));
        islandLayer = ZoomLayer.FUZZY.apply(contextFactory.apply(2000L), islandLayer);
        islandLayer = ScarletAddIslandLayer.INSTANCE.apply(contextFactory.apply(1L), islandLayer);
        islandLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(2001L), islandLayer);
        islandLayer = ScarletAddIslandLayer.INSTANCE.apply(contextFactory.apply(2L), islandLayer);
        islandLayer = ScarletAddIslandLayer.INSTANCE.apply(contextFactory.apply(50L), islandLayer);
        islandLayer = ScarletAddIslandLayer.INSTANCE.apply(contextFactory.apply(70L), islandLayer);
        islandLayer = ScarletRemoveTooMuchOceanLayer.INSTANCE.apply(contextFactory.apply(2L), islandLayer);
        //IAreaFactory<T> oceanLayer = OceanLayer.INSTANCE.apply(contextFactory.apply(2L));
        //oceanLayer = repeat(2001L, ZoomLayer.NORMAL, oceanLayer, 6, contextFactory);
        islandLayer = ScarletAddSnowLayer.INSTANCE.apply(contextFactory.apply(2L), islandLayer);
        islandLayer = ScarletAddIslandLayer.INSTANCE.apply(contextFactory.apply(3L), islandLayer);
        islandLayer = ScarletEdgeLayer.CoolWarm.INSTANCE.apply(contextFactory.apply(2L), islandLayer);
        islandLayer = ScarletEdgeLayer.HeatIce.INSTANCE.apply(contextFactory.apply(2L), islandLayer);
        islandLayer = ScarletEdgeLayer.Special.INSTANCE.apply(contextFactory.apply(3L), islandLayer);
        islandLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(2002L), islandLayer);
        islandLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(2003L), islandLayer);
        islandLayer = ScarletAddIslandLayer.INSTANCE.apply(contextFactory.apply(4L), islandLayer);
        //islandLayer = AddMushroomIslandLayer.INSTANCE.apply(contextFactory.apply(5L), islandLayer);
        //islandLayer = DeepOceanLayer.INSTANCE.apply(contextFactory.apply(4L), islandLayer);
        islandLayer = repeat(1000L, ZoomLayer.NORMAL, islandLayer, 0, contextFactory);

        int biomeSize = worldType == WorldType.LARGE_BIOMES ? 6 : settings.getBiomeSize();
        biomeSize = getModdedBiomeSize(worldType, biomeSize);
        int riverSize = settings.getRiverSize();

        IAreaFactory<T> riverLayer = repeat(1000L, ZoomLayer.NORMAL, islandLayer, 0, contextFactory);
        riverLayer = ScarletStartRiverLayer.INSTANCE.apply(contextFactory.apply(100L), riverLayer);
        IAreaFactory<T> biomeLayer = getBiomeLayer(islandLayer, settings, contextFactory, worldType);
        //IAreaFactory<T> lvt_9_1_ = repeat(1000L, ZoomLayer.NORMAL, riverLayer, 2, contextFactory);
        //biomeLayer = HillsLayer.INSTANCE.apply(contextFactory.apply(1000L), biomeLayer, lvt_9_1_);
        riverLayer = repeat(1000L, ZoomLayer.NORMAL, riverLayer, 2, contextFactory);
        riverLayer = repeat(1000L, ZoomLayer.NORMAL, riverLayer, riverSize, contextFactory);
        riverLayer = ScarletRiverLayer.INSTANCE.apply(contextFactory.apply(1L), riverLayer);
        riverLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1000L), riverLayer);
        //biomeLayer = RareBiomeLayer.INSTANCE.apply(contextFactory.apply(1001L), biomeLayer);

        for (int k = 0; k < biomeSize; ++k) {
            biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1000 + k), biomeLayer);
            if (k == 0) {
                biomeLayer = ScarletAddIslandLayer.INSTANCE.apply(contextFactory.apply(3L), biomeLayer);
            }

            //if (k == 1 || biomeSize == 1) {
            //    biomeLayer = ShoreLayer.INSTANCE.apply(contextFactory.apply(1000L), biomeLayer);
            //}
        }

        biomeLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1000L), biomeLayer);
        biomeLayer = ScarletMixRiverLayer.INSTANCE.apply(contextFactory.apply(100L), biomeLayer, riverLayer);
        //biomeLayer = MixOceansLayer.INSTANCE.apply(contextFactory.apply(100L), biomeLayer, oceanLayer);
        return biomeLayer;
    }

    private static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> getBiomeLayer(IAreaFactory<T> parentLayer, OverworldGenSettings chunkSettings, LongFunction<C> contextFactory, WorldType worldType) {
        parentLayer = (new ScarletBiomeLayer(worldType, chunkSettings.getBiomeId())).apply(contextFactory.apply(200L), parentLayer);
        //parentLayer = AddBambooForestLayer.INSTANCE.apply(contextFactory.apply(1001L), parentLayer);
        parentLayer = ScarletLayerUtil.repeat(1000L, ZoomLayer.NORMAL, parentLayer, 2, contextFactory);
        parentLayer = EdgeBiomeLayer.INSTANCE.apply(contextFactory.apply(1000L), parentLayer);
        return parentLayer;
    }

    public static Layer func_227474_a_(long seedIn, WorldType type, OverworldGenSettings settings) {
        IAreaFactory<LazyArea> biomeLayer = func_227475_a_(type, settings, (p_227473_2_) ->
                new LazyAreaLayerContext(25, seedIn, p_227473_2_));
        return new Layer(biomeLayer);
    }

    /* ======================================== FORGE START =====================================*/
    public static int getModdedBiomeSize(WorldType worldType, int original) {
        net.minecraftforge.event.terraingen.WorldTypeEvent.BiomeSize event = new net.minecraftforge.event.terraingen.WorldTypeEvent.BiomeSize(worldType, original);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        return event.getNewSize();
    }
    /* ========================================= FORGE END ======================================*/

    protected static boolean isOcean(int biomeIn) {
        return biomeIn == SCARLET_OCEAN_BIOME;
    }
}
