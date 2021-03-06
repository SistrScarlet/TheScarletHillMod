package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.StairsBlock;

public class ScarletStoneBrickStairsBlock extends StairsBlock {

    public ScarletStoneBrickStairsBlock() {
        super(() -> Registration.SCARLET_STONE_BRICKS_BLOCK.get().getDefaultState(), Properties.from(Registration.SCARLET_STONE_BRICKS_BLOCK.get()));
    }

}
