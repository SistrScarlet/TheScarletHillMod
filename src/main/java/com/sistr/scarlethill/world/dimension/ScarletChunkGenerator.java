package com.sistr.scarlethill.world.dimension;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

import javax.annotation.Nullable;

public class ScarletChunkGenerator extends OverworldChunkGenerator {

    public ScarletChunkGenerator(IWorld worldIn, BiomeProvider provider) {
        super(worldIn, provider, Config.createDefault());
    }

    @Nullable
    @Override
    public BlockPos findNearestStructure(World worldIn, String name, BlockPos pos, int radius, boolean skipExistingChunks) {
        return super.findNearestStructure(worldIn, name, pos, radius, skipExistingChunks);
    }

    public static class Config extends OverworldGenSettings {

        public static Config createDefault() {
            Config config = new Config();
            config.setDefaultBlock(Registration.SCARLET_STONE_BLOCK.get().getDefaultState());
            config.setDefaultFluid(Blocks.WATER.getDefaultState());
            return config;
        }

        @Override
        public int getBiomeSize() {
            return 4;
        }
    }
}