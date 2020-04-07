package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.SlabBlock;

public class ScarletPlanksSlabBlock extends SlabBlock {

    public ScarletPlanksSlabBlock() {
        super(Properties.from(Registration.SCARLET_PLANKS_BLOCK.get()));
    }
}
