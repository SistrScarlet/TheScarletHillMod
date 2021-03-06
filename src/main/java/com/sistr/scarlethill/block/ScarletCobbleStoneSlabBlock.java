package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.SlabBlock;

public class ScarletCobbleStoneSlabBlock extends SlabBlock {

    public ScarletCobbleStoneSlabBlock() {
        super(Properties.from(Registration.SCARLET_COBBLE_STONE_BLOCK.get()));
    }
}
