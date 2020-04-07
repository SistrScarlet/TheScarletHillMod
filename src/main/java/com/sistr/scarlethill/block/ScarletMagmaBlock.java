package com.sistr.scarlethill.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class ScarletMagmaBlock extends Block {

    public ScarletMagmaBlock() {
        super(Properties.create(Material.ROCK, MaterialColor.TNT)
                .hardnessAndResistance(1.5F, 6.0F).lightValue(12)
        );
    }
}
