package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.SlabBlock;

public class ScarletStoneBrickSlabBlock extends SlabBlock {

    public ScarletStoneBrickSlabBlock() {
        super(Properties.from(Registration.SCARLET_STONE_BRICKS_BLOCK.get()));
    }
}
