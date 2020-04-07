package com.sistr.scarlethill.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILeftClickable {

    default void onLeftClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    }

    default void onBlockLeftClick(World worldIn, PlayerEntity playerIn, Hand handIn, BlockPos pos) {
    }

}
