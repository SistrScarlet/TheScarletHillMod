package com.sistr.scarlethill.setup;

import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.world.Feature.CrimsonianVillagePiece;
import com.sistr.scarlethill.world.Feature.MoltenMinePiece;
import com.sistr.scarlethill.world.Feature.ScarletBearNestPiece;
import com.sistr.scarlethill.world.Feature.ScarletPortalPiece;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

public class StructurePieceRegistry {
    public static final IStructurePieceType PORTAL = register(ScarletPortalPiece.Piece::new, "portal");
    public static final IStructurePieceType CRIVIL = register(CrimsonianVillagePiece.Piece::new, "crivil");
    public static final IStructurePieceType NEST = register(ScarletBearNestPiece.Piece::new, "nest");
    public static final IStructurePieceType MOLTEN = register(MoltenMinePiece.Piece::new, "molten");

    private static IStructurePieceType register(IStructurePieceType type, String key) {
        return Registry.register(Registry.STRUCTURE_PIECE, new ResourceLocation(ScarletHillMod.MODID, key), type);
    }
}
