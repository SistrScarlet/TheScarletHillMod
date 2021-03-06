package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.WallBlock;

public class ScarletCobbleStoneWallBlock extends WallBlock {

    public ScarletCobbleStoneWallBlock() {
        super(Properties.from(Registration.SCARLET_COBBLE_STONE_BLOCK.get()));
    }
}
