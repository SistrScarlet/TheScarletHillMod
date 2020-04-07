package com.sistr.scarlethill.block;


import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class ScarletGlassBlock extends AbstractGlassBlock {

    public ScarletGlassBlock() {
        super(Block.Properties.create(Material.GLASS, MaterialColor.AIR)
                .hardnessAndResistance(0.3F).sound(SoundType.GLASS).notSolid());
    }

}
