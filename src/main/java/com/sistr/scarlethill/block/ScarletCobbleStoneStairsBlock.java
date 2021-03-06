package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.StairsBlock;

public class ScarletCobbleStoneStairsBlock extends StairsBlock {

    public ScarletCobbleStoneStairsBlock() {
        super(() -> Registration.SCARLET_COBBLE_STONE_BLOCK.get().getDefaultState(), Properties.from(Registration.SCARLET_COBBLE_STONE_BLOCK.get()));
    }

}
