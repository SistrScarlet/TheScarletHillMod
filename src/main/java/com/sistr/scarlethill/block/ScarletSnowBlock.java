package com.sistr.scarlethill.block;

import net.minecraft.block.SnowBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class ScarletSnowBlock extends SnowBlock {

    //todo 雪が降るようにする
    public ScarletSnowBlock() {
        super(Properties.create(Material.SNOW, MaterialColor.TNT).tickRandomly()
                .hardnessAndResistance(0.1F).sound(SoundType.SNOW).notSolid()
        );
    }


}
