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

public class MoltenMinePiece {
    private static final ResourceLocation Abyss = new ResourceLocation(ScarletHillMod.MODID, "molten_mine/abyss");
    private static final ResourceLocation Main_Frame = new ResourceLocation(ScarletHillMod.MODID, "molten_mine/main_frame");
    private static final ResourceLocation Boss = new ResourceLocation(ScarletHillMod.MODID, "molten_mine/boss");
    private static final Map<ResourceLocation, BlockPos> CenterOffset;
    private static final Map<ResourceLocation, BlockPos> PosOffset;

    public static void place(TemplateManager manager, BlockPos pos, List<StructurePiece> piece) {
        for (int i = 240; 16 < i; i -= 16) {
            piece.add(new MoltenMinePiece.Piece(manager, Abyss, pos.up(i)));
        }
        piece.add(new MoltenMinePiece.Piece(manager, Main_Frame, pos));
        piece.add(new MoltenMinePiece.Piece(manager, Boss, pos));
    }

    static {
        CenterOffset = ImmutableMap.of(Abyss, BlockPos.ZERO, Main_Frame, BlockPos.ZERO, Boss, BlockPos.ZERO);
        PosOffset = ImmutableMap.of(Abyss, BlockPos.ZERO, Main_Frame, BlockPos.ZERO, Boss, new BlockPos(-32, 0, 0));
    }

    public static class Piece extends TemplateStructurePiece {
        private final ResourceLocation location;

        public Piece(TemplateManager manager, ResourceLocation location, BlockPos placePos) {
            super(StructurePieceRegistry.MOLTEN, 0);
            this.location = location;
            BlockPos offset = MoltenMinePiece.PosOffset.get(location);
            this.templatePosition = placePos.add(offset.getX(), offset.getY(), offset.getZ());
            this.func_207614_a(manager);
        }

        public Piece(TemplateManager manager, CompoundNBT nbt) {
            super(StructurePieceRegistry.MOLTEN, nbt);
            this.location = new ResourceLocation(nbt.getString("Template"));
            this.func_207614_a(manager);
        }

        private void func_207614_a(TemplateManager manager) {
            Template template = manager.getTemplateDefaulted(this.location);
            PlacementSettings settings = (new PlacementSettings()).setMirror(Mirror.NONE)
                    .setCenterOffset(MoltenMinePiece.CenterOffset.get(this.location))
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
            if (!this.location.equals(MoltenMinePiece.Abyss)) {
                this.templatePosition = this.templatePosition.add(0, -this.templatePosition.getY() + 5, 0);
            }
            boolean flag = super.create(world, generator, rand, box, pos);

            this.templatePosition = tempPos;
            return flag;
        }
    }

}
