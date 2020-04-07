package com.sistr.scarlethill.block;

import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;

public class ScarletSandBlock extends FallingBlock {

    public ScarletSandBlock() {
        super(Properties.create(Material.SAND, DyeColor.RED)
                .hardnessAndResistance(0.5F).sound(SoundType.SAND)
        );
    }
}
