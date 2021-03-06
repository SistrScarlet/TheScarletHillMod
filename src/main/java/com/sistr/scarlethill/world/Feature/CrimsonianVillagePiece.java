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
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class CrimsonianVillagePiece {
    private static final ResourceLocation Entrance = new ResourceLocation(ScarletHillMod.MODID, "crimsonian_village/entrance");
    private static final ResourceLocation Core = new ResourceLocation(ScarletHillMod.MODID, "crimsonian_village/core");
    private static final Map<ResourceLocation, BlockPos> CenterOffset;
    private static final Map<ResourceLocation, BlockPos> PosOffset;

    public static void place(TemplateManager manager, BlockPos pos, List<StructurePiece> piece) {
        piece.add(new CrimsonianVillagePiece.Piece(manager, Entrance, pos));
        piece.add(new CrimsonianVillagePiece.Piece(manager, Core, pos));
    }

    static {
        CenterOffset = ImmutableMap.of(Entrance, BlockPos.ZERO, Core, BlockPos.ZERO);
        PosOffset = ImmutableMap.of(Entrance, new BlockPos(2, 22, 2), Core, new BlockPos(14, 32, 29));
    }

    public static class Piece extends TemplateStructurePiece {
        private final ResourceLocation location;

        public Piece(TemplateManager manager, ResourceLocation location, BlockPos placePos) {
            super(StructurePieceRegistry.CRIVIL, 0);
            this.location = location;
            BlockPos offset = CrimsonianVillagePiece.PosOffset.get(location);
            this.templatePosition = placePos.add(-offset.getX(), -offset.getY(), -offset.getZ());
            this.func_207614_a(manager);
        }

        public Piece(TemplateManager manager, CompoundNBT nbt) {
            super(StructurePieceRegistry.CRIVIL, nbt);
            this.location = new ResourceLocation(nbt.getString("Template"));
            this.func_207614_a(manager);
        }

        private void func_207614_a(TemplateManager manager) {
            Template template = manager.getTemplateDefaulted(this.location);
            PlacementSettings settings = (new PlacementSettings()).setMirror(Mirror.NONE)
                    .setCenterOffset(CrimsonianVillagePiece.CenterOffset.get(this.location))
                    .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
            this.setup(template, this.templatePosition, settings);
        }

        protected void readAdditional(CompoundNBT nbt) {
            super.readAdditional(nbt);
            nbt.putString("Template", this.location.toString());
        }

        protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
        }

        public boolean create(IWorld world, ChunkGenerator<?> generator, Random rand, MutableBoundingBox box, ChunkPos pos) {
            BlockPos tempPos = this.templatePosition;
            boolean flag = super.create(world, generator, rand, box, pos);
            this.templatePosition = tempPos;
            return flag;
        }
    }

}
