package com.sistr.scarlethill.world.Feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.function.Function;

public class ScarletPortalStructure extends ScatteredStructure<NoFeatureConfig> {
    public ScarletPortalStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51491_1_) {
        super(p_i51491_1_);
    }

    public String getStructureName() {
        return "Scarlet_Portal";
    }

    public int getSize() {
        return 3;
    }

    public IStartFactory getStartFactory() {
        return ScarletPortalStructure.Start::new;
    }

    protected int getSeedModifier() {
        return 0xCAEA11;
    }

    @Override
    protected int getBiomeFeatureDistance(ChunkGenerator<?> p_204030_1_) {
        return 24;
    }

    @Override
    protected int getBiomeFeatureSeparation(ChunkGenerator<?> p_211745_1_) {
        return 8;
    }

    public static class Start extends StructureStart {
        public Start(Structure<?> structure, int chunkX, int chunkY, MutableBoundingBox box, int rand, long seed) {
            super(structure, chunkX, chunkY, box, rand, seed);
        }

        public void init(ChunkGenerator<?> p_214625_1_, TemplateManager manager, int chunkX, int chunkY, Biome biome) {
            int posX = chunkX * 16;
            int posZ = chunkY * 16;
            BlockPos blockPos = new BlockPos(posX, 0, posZ);
            ScarletPortalPiece.place(manager, blockPos, this.components);
            this.recalculateStructureSize();
        }
    }
}
