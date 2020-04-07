package com.sistr.scarlethill.block.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

//まだ作りかけ。現在はブロック擬態機能がある
//最終的には、設置したブロックに穴を空け、へこんだように描画させたい
//といっても、ただ足跡作りたいだけならこんなクソ面倒なことしなくていいんだけどね
public class FootPrintBlock extends Block {

    private static final VoxelShape SHAPE = VoxelShapes.create(.2, .2, .2, .8, .8, .8);

    public FootPrintBlock() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .hardnessAndResistance(2.0f));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FootPrintTile();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        ItemStack item = player.getHeldItem(hand);
        if (!item.isEmpty() && item.getItem() instanceof BlockItem) {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof FootPrintTile) {
                    BlockState mimicState = ((BlockItem) item.getItem()).getBlock().getDefaultState();
                    ((FootPrintTile) te).setMimic(mimicState);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, world, pos, player, hand, trace);
    }
}
