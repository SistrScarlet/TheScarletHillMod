package com.sistr.scarlethill.world.Feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;
import java.util.function.Function;

import static net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE_WG;

public class CrimsonianVillageStructure extends Structure<NoFeatureConfig> {
    public CrimsonianVillageStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51491_1_) {
        super(p_i51491_1_);
    }

    protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
        int i = this.getBiomeFeatureDistance(chunkGenerator);
        int j = this.getBiomeFeatureSeparation(chunkGenerator);
        int k = x + i * spacingOffsetsX;
        int l = z + i * spacingOffsetsZ;
        int i1 = k < 0 ? k - i + 1 : k;
        int j1 = l < 0 ? l - i + 1 : l;
        int k1 = i1 / i;
        int l1 = j1 / i;
        ((SharedSeedRandom) random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), k1, l1, this.getSeedModifier());
        k1 = k1 * i;
        l1 = l1 * i;
        k1 = k1 + random.nextInt(i - j);
        l1 = l1 + random.nextInt(i - j);
        return new ChunkPos(k1, l1);
    }

    protected int getBiomeFeatureDistance(ChunkGenerator<?> p_204030_1_) {
        return 16;
    }

    protected int getBiomeFeatureSeparation(ChunkGenerator<?> p_211745_1_) {
        return 8;
    }

    public boolean canBeGenerated(BiomeManager manager, ChunkGenerator<?> generator, Random rand, int chunkX, int chunkZ, Biome biome) {
        //生成高度の制限
        int height = generator.func_222529_a(chunkX * 16, chunkZ * 16, WORLD_SURFACE_WG);
        if (height < 66 || 96 < height) {
            return false;
        }
        ChunkPos chunkpos = this.getStartPositionForPosition(generator, rand, chunkX, chunkZ, 0, 0);
        return chunkX == chunkpos.x && chunkZ == chunkpos.z && generator.hasStructure(biome, this);
    }

    public String getStructureName() {
        return "Crimsonian_Village";
    }

    //固定3 / 可変8?
    public int getSize() {
        return 3;
    }

    public IStartFactory getStartFactory() {
        return CrimsonianVillageStructure.Start::new;
    }

    protected int getSeedModifier() {
        return 0xCAAECE;
    }

    public static class Start extends StructureStart {
        public Start(Structure<?> structure, int chunkX, int chunkY, MutableBoundingBox box, int rand, long seed) {
            super(structure, chunkX, chunkY, box, rand, seed);
        }

        public void init(ChunkGenerator<?> generator, TemplateManager manager, int chunkX, int chunkY, Biome biome) {
            int posX = chunkX * 16;
            int posZ = chunkY * 16;
            int height = generator.func_222529_a(posX, posZ, WORLD_SURFACE_WG);
            BlockPos blockPos = new BlockPos(posX, height, posZ);
            CrimsonianVillagePiece.place(manager, blockPos, this.components);
            this.recalculateStructureSize();
        }
    }
}
