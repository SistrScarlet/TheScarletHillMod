package com.sistr.scarlethill.world.Feature;

import com.google.common.collect.ImmutableMap;
import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.setup.StructurePieceRegistry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ScarletBearNestPiece {
    private static final ResourceLocation Nest = new ResourceLocation(ScarletHillMod.MODID, "scarlet_bear_nest/nest");
    private static final Map<ResourceLocation, BlockPos> PosOffset;

    public static void place(TemplateManager manager, BlockPos blockPos, List<StructurePiece> piece) {
        piece.add(new ScarletBearNestPiece.Piece(manager, Nest, blockPos));
    }

    static {
        PosOffset = ImmutableMap.of(Nest, new BlockPos(-5, -2, -5));
    }

    public static class Piece extends TemplateStructurePiece {
        private final ResourceLocation location;

        public Piece(TemplateManager manager, ResourceLocation location, BlockPos blockPos) {
            super(StructurePieceRegistry.NEST, 0);
            this.location = location;
            BlockPos offset = ScarletBearNestPiece.PosOffset.get(location);
            this.templatePosition = blockPos.add(offset.getX(), offset.getY(), offset.getZ());
            this.func_207614_a(manager);
        }

        public Piece(TemplateManager manager, CompoundNBT compound) {
            super(StructurePieceRegistry.NEST, compound);
            this.location = new ResourceLocation(compound.getString("Template"));
            this.func_207614_a(manager);
        }

        private void func_207614_a(TemplateManager manager) {
            Template temp = manager.getTemplateDefaulted(this.location);
            PlacementSettings settings = (new PlacementSettings()).setMirror(Mirror.NONE)
                    .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
            this.setup(temp, this.templatePosition, settings);
        }

        protected void readAdditional(CompoundNBT compound) {
            super.readAdditional(compound);
            compound.putString("Template", this.location.toString());
        }

        protected void handleDataMarker(String key, BlockPos blockPos, IWorld iWorld, Random rand, MutableBoundingBox box) {
        }

        //実際に生成する処理
        //templatePositionを中心に生成する
        public boolean create(IWorld iWorld, ChunkGenerator<?> generator, Random rand, MutableBoundingBox box, ChunkPos chunkPos) {
            BlockPos tempTempPos = this.templatePosition;
            BlockPos offsetPos = PosOffset.get(this.location);
            int generatePosHeight = iWorld.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.templatePosition.getX() - offsetPos.getX(), this.templatePosition.getZ() - offsetPos.getZ());
            this.templatePosition = this.templatePosition.add(0, generatePosHeight, 0);
            boolean flag = super.create(iWorld, generator, rand, box, chunkPos);
            this.templatePosition = tempTempPos;

            return flag;
        }
    }
}
