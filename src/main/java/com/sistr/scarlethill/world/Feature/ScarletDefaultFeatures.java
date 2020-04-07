package com.sistr.scarlethill.world.Feature;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class ScarletDefaultFeatures {

    public static final TreeFeatureConfig SCARLET_TREE_CONFIG = (new TreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(Registration.SCARLET_LOG_BLOCK.get().getDefaultState()),
            new SimpleBlockStateProvider(Registration.SCARLET_LEAVES_BLOCK.get().getDefaultState()),
            new BlobFoliagePlacer(0, 0)))
            .setSapling(Registration.SCARLET_SAPLING_BLOCK.get()).build();

    //ストラクチャに関しては、addFeatureで追加した上でaddStructureもする必要があるみたい
    //ちなみにaddFeatureが無いバイオームにStructureが生成された場合、スッパリ切れてしまうので注意が必要
    public static void addStructures(Biome biome) {
        biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Registration.SCARLET_PORTAL_STRUCTURE_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
        biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Registration.SCARLET_CRIMSONIAN_VILLAGE_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
        biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Registration.SCARLET_BEAR_NEST_STRUCTURE_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
        biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Registration.MOLTEN_MINE_STRUCTURE_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
    }

    public static void addOverWorldStructures(Biome biome) {
        biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Registration.SCARLET_PORTAL_STRUCTURE_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
    }

    public static void addScarletTree(Biome biome) {
        biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FANCY_TREE.withConfiguration(SCARLET_TREE_CONFIG).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(5, 0.1F, 1))));
    }

    public static void addFreezeTopLayer(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Registration.SCARLET_FREEZE_TOP_LAYER_BEFORE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
    }

}
