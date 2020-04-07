package com.sistr.scarlethill.world.Feature;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class ScarletTreeFeature extends Tree {
    @Nullable
    @Override
    protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random random, boolean b) {
        return Feature.FANCY_TREE.withConfiguration(ScarletDefaultFeatures.SCARLET_TREE_CONFIG);
    }

}
