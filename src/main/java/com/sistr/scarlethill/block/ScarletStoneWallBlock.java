package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;

public class ScarletStoneWallBlock extends WallBlock {

    public ScarletStoneWallBlock() {
        super(Block.Properties.from(Registration.SCARLET_STONE_BLOCK.get()));
    }
}
