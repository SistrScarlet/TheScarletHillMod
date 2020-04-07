package com.sistr.scarlethill.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class ScarletPlanksBlock extends Block {

    public ScarletPlanksBlock() {
        super(Properties.create(Material.WOOD, MaterialColor.WHITE_TERRACOTTA)
                .hardnessAndResistance(2.0F).sound(SoundType.WOOD));
    }
}
