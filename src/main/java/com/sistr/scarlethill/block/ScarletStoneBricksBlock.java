package com.sistr.scarlethill.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class ScarletStoneBricksBlock extends Block {

    public ScarletStoneBricksBlock() {
        super(Properties.create(Material.ROCK, MaterialColor.RED)
                .hardnessAndResistance(1.5F, 6.0F)
        );
    }
}
