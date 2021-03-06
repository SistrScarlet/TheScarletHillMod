package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.WallBlock;

public class ScarletStoneBrickWallBlock extends WallBlock {

    public ScarletStoneBrickWallBlock() {
        super(Properties.from(Registration.SCARLET_STONE_BRICKS_BLOCK.get()));
    }
}
