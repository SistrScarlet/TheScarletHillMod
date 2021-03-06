package com.sistr.scarlethill.world.Feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;
import java.util.function.Function;

import static net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE_WG;

public class ScarletBearNestStructure extends ScatteredStructure<NoFeatureConfig> {
    public ScarletBearNestStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51491_1_) {
        super(p_i51491_1_);
    }

    @Override
    public boolean canBeGenerated(BiomeManager manager, ChunkGenerator<?> generator, Random rand, int chunkX, int chunkZ, Biome biome) {
        //128以下だったら生成しない
        if (generator.func_222529_a(chunkX * 16, chunkZ * 16, WORLD_SURFACE_WG) < 128) return false;
        return super.canBeGenerated(manager, generator, rand, chunkX, chunkZ, biome);
    }

    public String getStructureName() {
        return "Scarlet_Bear_Nest";
    }

    public int getSize() {
        return 3;
    }

    public IStartFactory getStartFactory() {
        return ScarletBearNestStructure.Start::new;
    }

    protected int getSeedModifier() {
        return 0xCAEBEA;
    }

    @Override
    protected int getBiomeFeatureDistance(ChunkGenerator<?> p_204030_1_) {
        return 6;
    }

    @Override
    protected int getBiomeFeatureSeparation(ChunkGenerator<?> p_211745_1_) {
        return 4;
    }

    public static class Start extends StructureStart {
        public Start(Structure<?> structure, int chunkX, int chunkY, MutableBoundingBox box, int rand, long seed) {
            super(structure, chunkX, chunkY, box, rand, seed);
        }

        public void init(ChunkGenerator<?> generator, TemplateManager manager, int chunkX, int chunkY, Biome biome) {
            int posX = chunkX * 16;
            int posZ = chunkY * 16;
            BlockPos blockPos = new BlockPos(posX, 0, posZ);
            ScarletBearNestPiece.place(manager, blockPos, this.components);
            this.recalculateStructureSize();
        }
    }
}
