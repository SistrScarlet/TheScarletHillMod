package com.sistr.scarlethill.block;

import net.minecraft.block.IceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;

public class ScarletIceBlock extends IceBlock {

    public ScarletIceBlock() {
        super(Properties.create(Material.ICE, DyeColor.RED).slipperiness(0.98F).tickRandomly()
                .hardnessAndResistance(0.5F).sound(SoundType.GLASS).notSolid());
    }
}
