package com.sistr.scarlethill.item;

import com.sistr.scarlethill.entity.projectile.ScarletProjectileEntity;
import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ScarletWandItem extends Item {

    public ScarletWandItem() {
        super(new Item.Properties()
                .maxDamage(0)
                .group(ModSetup.ITEM_GROUP)
        );
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(itemstack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity && ((LivingEntity) entityIn).getActiveItemStack() == stack) {
            triggerTick(stack, worldIn, (LivingEntity) entityIn, entityIn.rotationPitch, entityIn.rotationYaw);
        }
    }

    //todo 鯖制御にするのアリアリ
    public static void triggerTick(ItemStack stack, World worldIn, LivingEntity entityIn, float pitch, float yaw) {
        if (entityIn instanceof PlayerEntity && ((PlayerEntity) entityIn).getCooldownTracker().hasCooldown(stack.getItem())) {
            return;
        }
        CompoundNBT compound = stack.getOrCreateTag();
        if (!compound.contains("WandWait")) {
            compound.putByte("WandWait", (byte) 0);
            compound.putByte("WandAmmo", (byte) 3);
        }
        int wait = compound.getByte("WandWait");
        int ammo = compound.getByte("WandAmmo");
        if (ammo <= 0) {
            entityIn.resetActiveHand();
            if (entityIn instanceof PlayerEntity) {
                ((PlayerEntity) entityIn).getCooldownTracker().setCooldown(stack.getItem(), 40);
                compound.putByte("WandAmmo", (byte) 3);
                return;
            }
        }
        if (wait != 0) {
            compound.putByte("WandWait", (byte) Math.max(wait - 1, 0));
            return;
        }
        compound.putByte("WandWait", (byte) 3);
        compound.putByte("WandAmmo", (byte) (ammo - 1));
        worldIn.playSound(null, entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(),
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.HOSTILE, 1, 2);
        if (!worldIn.isRemote) {
            ScarletProjectileEntity projectile = new ScarletProjectileEntity(entityIn, worldIn);
            Vec3d shootVec = Vec3d.fromPitchYaw(pitch, yaw);
            projectile.shoot(shootVec.getX(), shootVec.getY(), shootVec.getZ(), 0, 0);
            worldIn.addEntity(projectile);
        }
    }


}
