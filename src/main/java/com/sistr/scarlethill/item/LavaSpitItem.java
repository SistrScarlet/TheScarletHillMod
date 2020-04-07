package com.sistr.scarlethill.item;

import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class LavaSpitItem extends Item {

    public LavaSpitItem() {
        super(new Properties()
                .group(ModSetup.ITEM_GROUP)
                .maxStackSize(16));
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!playerIn.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }

        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isRemote) {
            SnowballEntity snowballentity = new SnowballEntity(worldIn, playerIn);
            snowballentity.setItem(itemstack);
            snowballentity.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.addEntity(snowballentity);
        }

        playerIn.addStat(Stats.ITEM_USED.get(this));
        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }
}
