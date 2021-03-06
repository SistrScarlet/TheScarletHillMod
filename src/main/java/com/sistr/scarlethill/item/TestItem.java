package com.sistr.scarlethill.item;

import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TestItem extends Item {

    public TestItem() {
        super(new Item.Properties().group(ModSetup.ITEM_GROUP).maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        byte state = compoundNBT.getByte("MagicState");
        Vec3d hitVec = context.getHitVec();
        //最初の一回
        if (state == 0) {
            compoundNBT.putByte("MagicState", (byte) 1);
            compoundNBT.putDouble("MCPX", hitVec.getX());
            compoundNBT.putDouble("MCPY", hitVec.getY());
            compoundNBT.putDouble("MCPZ", hitVec.getZ());
            return ActionResultType.SUCCESS;
        }
        if (6 < state) {
            return ActionResultType.SUCCESS;
        }
        compoundNBT.putByte("MagicState", (byte) (state + 1));
        compoundNBT.putDouble("MCPX" + state, hitVec.getX());
        compoundNBT.putDouble("MCPY" + state, hitVec.getY());
        compoundNBT.putDouble("MCPZ" + state, hitVec.getZ());
        return ActionResultType.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        byte state = compoundNBT.getByte("MagicState");
        if (state == 0) {
            return;
        }
        Vec3d center = new Vec3d(
                compoundNBT.getDouble("MCPX"),
                compoundNBT.getDouble("MCPY"),
                compoundNBT.getDouble("MCPZ"));
        //円形描画
        for (int angle = 0; angle < 360; angle += 5) {
            float rad = (float) Math.PI / 180F;
            float x = MathHelper.sin(angle * rad) * 3F;
            float z = MathHelper.cos(angle * rad) * 3F;
            worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, center.getX() + x, center.getY(), center.getZ() + z, 0, 0, 0);
        }
        if (state < 2) {
            return;
        }
        Vec3d[] points = new Vec3d[state - 1];
        for (int i = 0; i < state - 1; i++) {
            points[i] = new Vec3d(
                    compoundNBT.getDouble("MCPX" + (i + 1)),
                    compoundNBT.getDouble("MCPY" + (i + 1)),
                    compoundNBT.getDouble("MCPZ" + (i + 1)));
        }
        //星描画
        for (int i = 0; i < points.length; i++) {
            Vec3d point = points[i];
            worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, point.getX(), point.getY(), point.getZ(), 0, 0, 0);
            for (int k = i + 1; k < points.length; k++) {
                Vec3d toPointPos = points[k];
                Vec3d toPointVec = toPointPos.subtract(point).normalize().scale(0.3);
                while (0.1 < toPointPos.subtract(point).lengthSquared()) {
                    toPointPos = toPointPos.subtract(toPointVec);
                    worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, toPointPos.getX(), toPointPos.getY(), toPointPos.getZ(), 0, 0, 0);
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }
}
