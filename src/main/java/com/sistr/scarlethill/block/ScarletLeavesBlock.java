package com.sistr.scarlethill.block;

import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class ScarletLeavesBlock extends LeavesBlock {

    public ScarletLeavesBlock() {
        super(Block.Properties.create(Material.LEAVES, MaterialColor.ADOBE).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).lightValue(8).notSolid());
    }
}
