package com.sistr.scarlethill.world.Feature;

import com.mojang.datafixers.Dynamic;
import com.sistr.scarlethill.setup.Registration;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;
import java.util.function.Function;

public class ScarletIceAndSnowFeature extends Feature<NoFeatureConfig> {

    public ScarletIceAndSnowFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51435_1_) {
        super(p_i51435_1_);
    }

    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutableDown = new BlockPos.Mutable();

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int k = pos.getX() + i;
                int l = pos.getZ() + j;
                int i1 = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, k, l);
                mutable.setPos(k, i1, l);
                mutableDown.setPos(mutable).move(Direction.DOWN, 1);
                Biome biome = worldIn.getBiome(mutable);
                if (biome.doesWaterFreeze(worldIn, mutableDown, false)) {
                    worldIn.setBlockState(mutableDown, Registration.SCARLET_ICE_BLOCK.get().getDefaultState(), 2);
                }

                if (biome.doesSnowGenerate(worldIn, mutable)) {
                    worldIn.setBlockState(mutable, Registration.SCARLET_SNOW_BLOCK.get().getDefaultState(), 2);
                }
            }
        }

        return true;
    }
}