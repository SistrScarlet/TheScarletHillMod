package com.sistr.scarlethill.block;

import com.sistr.scarlethill.world.Feature.ScarletTreeFeature;
import net.minecraft.block.Block;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class ScarletSaplingBlock extends SaplingBlock {

    public ScarletSaplingBlock() {
        super(new ScarletTreeFeature(), Block.Properties.create(Material.PLANTS)
                .doesNotBlockMovement().tickRandomly()
                .hardnessAndResistance(0.0F).sound(SoundType.PLANT));
    }
}
