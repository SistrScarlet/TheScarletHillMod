package com.sistr.scarlethill.block.tile;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class ScarletCobbleStoneBlock extends Block {

    public ScarletCobbleStoneBlock() {
        super(Properties.create(Material.ROCK, MaterialColor.RED)
                .hardnessAndResistance(1.5F, 6.0F)
        );
    }
}
