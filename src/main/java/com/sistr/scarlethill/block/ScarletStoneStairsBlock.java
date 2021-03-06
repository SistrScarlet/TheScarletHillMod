package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.StairsBlock;

public class ScarletStoneStairsBlock extends StairsBlock {

    public ScarletStoneStairsBlock() {
        super(() -> Registration.SCARLET_STONE_BLOCK.get().getDefaultState(), Properties.from(Registration.SCARLET_STONE_BLOCK.get()));
    }

}
