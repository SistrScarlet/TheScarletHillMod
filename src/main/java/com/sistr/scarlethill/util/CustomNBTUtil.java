package com.sistr.scarlethill.util;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Optional;

public class CustomNBTUtil {

    public static Optional<AxisAlignedBB> getArea(String tag, CompoundNBT nbt) {
        if (nbt.contains(tag, 9)) {
            ListNBT list = nbt.getList(tag, 11);
            int[] array = list.getIntArray(0);
            return Optional.of(new AxisAlignedBB(array[0], array[1], array[2], array[3], array[4], array[5]));
        }
        return Optional.empty();
    }

    public static void writeArea(String tag, CompoundNBT nbt, AxisAlignedBB bb) {
        if (bb == null) return;
        ListNBT list = new ListNBT();
        int[] array = {MathHelper.floor(bb.maxX), MathHelper.floor(bb.maxY), MathHelper.floor(bb.maxZ),
                MathHelper.floor(bb.minX), MathHelper.floor(bb.minY), MathHelper.floor(bb.minZ)};
        list.add(0, new IntArrayNBT(array));
        nbt.put(tag, list);
    }

    public static List<BlockPos> getBlocks(String tag, CompoundNBT nbt) {
        if (nbt.contains(tag, 9)) {
            ListNBT list = nbt.getList(tag, 11);
            return getBlockPosList(list);
        }
        return Lists.newArrayList();
    }

    public static List<BlockPos> getBlockPosList(ListNBT list) {
        List<BlockPos> posList = Lists.newArrayListWithExpectedSize(list.size());
        for (int i = 0; i < list.size(); i++) {
            posList.add(convertArrayToPos(list.getIntArray(i)));
        }
        return posList;
    }

    public static void writeBlockPosList(String tag, CompoundNBT nbt, List<BlockPos> list) {
        ListNBT listNBT = new ListNBT();
        list.forEach(pos -> addBlockPos(pos, listNBT));
        nbt.put(tag, listNBT);
    }

    public static void addBlockPos(BlockPos pos, ListNBT list) {
        list.add(new IntArrayNBT(convertPosToArray(pos)));
    }

    public static int[] convertPosToArray(BlockPos pos) {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public static BlockPos convertArrayToPos(int[] array) {
        return new BlockPos(array[0], array[1], array[2]);
    }

}
