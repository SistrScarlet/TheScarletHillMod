package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.SlabBlock;

public class ScarletStoneSlabBlock extends SlabBlock {

    public ScarletStoneSlabBlock() {
        super(Properties.from(Registration.SCARLET_STONE_BLOCK.get()));
    }
}
