package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.StairsBlock;

public class ScarletPlanksStairsBlock extends StairsBlock {

    public ScarletPlanksStairsBlock() {
        super(() -> Registration.SCARLET_PLANKS_BLOCK.get().getDefaultState(), Properties.from(Registration.SCARLET_PLANKS_BLOCK.get()));
    }

}
