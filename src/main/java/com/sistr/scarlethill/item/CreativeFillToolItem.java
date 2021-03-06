package com.sistr.scarlethill.item;

import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CreativeFillToolItem extends Item implements ILeftClickable {

    public CreativeFillToolItem() {
        super(new Properties()
                .maxStackSize(1)
                .maxDamage(0)
                .group(ModSetup.ITEM_GROUP));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            BlockPos clickPos = context.getPos();
            ItemStack stack = player.getHeldItem(context.getHand());
            CompoundNBT compound = stack.getOrCreateTag();

            List<Integer> blockPos = new ArrayList<>(3);
            blockPos.add(clickPos.getX());
            blockPos.add(clickPos.getY());
            blockPos.add(clickPos.getZ());

            compound.putIntArray("RightClickPos", blockPos);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    public boolean onBlockRightClick(World worldIn, PlayerEntity playerIn, Hand handIn, BlockPos pos) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        List<Integer> blockPos = new ArrayList<>(3);
        blockPos.add(pos.getX());
        blockPos.add(pos.getY());
        blockPos.add(pos.getZ());
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putIntArray("RightClickPos", blockPos);
        return true;
    }

    @Override
    public void onBlockLeftClick(World worldIn, PlayerEntity playerIn, Hand handIn, BlockPos pos) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        List<Integer> blockPos = new ArrayList<>(3);
        blockPos.add(pos.getX());
        blockPos.add(pos.getY());
        blockPos.add(pos.getZ());
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putIntArray("LeftClickPos", blockPos);
    }

    @Override
    public void onLeftClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        Item item = playerIn.getHeldItemOffhand().getItem();
        if (playerIn.isSneaking() && item instanceof BlockItem) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            CompoundNBT compound = stack.getOrCreateTag();
            if (!compound.contains("LeftClickPos") || !compound.contains("RightClickPos")) return;
            int[] leftClickPos = compound.getIntArray("LeftClickPos");
            Vec3i firstPos = new Vec3i(leftClickPos[0], leftClickPos[1], leftClickPos[2]);
            int[] rightClickPos = compound.getIntArray("RightClickPos");
            Vec3i secondPos = new Vec3i(rightClickPos[0], rightClickPos[1], rightClickPos[2]);
            MutableBoundingBox box = new MutableBoundingBox(firstPos, secondPos);
            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
                worldIn.setBlockState(blockpos, ((BlockItem) item).getBlock().getDefaultState());
            }
            compound.remove("LeftClickPos");
            compound.remove("RightClickPos");
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        CompoundNBT compound = stack.getOrCreateTag();
        if (!compound.contains("LeftClickPos")) return;
        int[] leftClickPos = compound.getIntArray("LeftClickPos");
        Vec3i firstPos = new Vec3i(leftClickPos[0], leftClickPos[1], leftClickPos[2]);
        worldIn.addParticle(new RedstoneParticleData(1, 0, 0, 1), firstPos.getX() + worldIn.rand.nextFloat(), firstPos.getY() + worldIn.rand.nextFloat(), firstPos.getZ() + worldIn.rand.nextFloat(), 0, 0, 0);
        if (!compound.contains("RightClickPos")) return;
        int[] rightClickPos = compound.getIntArray("RightClickPos");
        Vec3i secondPos = new Vec3i(rightClickPos[0], rightClickPos[1], rightClickPos[2]);
        worldIn.addParticle(new RedstoneParticleData(1, 0, 0, 1), secondPos.getX() + worldIn.rand.nextFloat(), secondPos.getY() + worldIn.rand.nextFloat(), secondPos.getZ() + worldIn.rand.nextFloat(), 0, 0, 0);
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }
}
