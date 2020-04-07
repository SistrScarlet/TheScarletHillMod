package com.sistr.scarlethill.item.egg;

import com.sistr.scarlethill.block.tile.SpawnMarkerTile;
import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;

import java.util.Objects;

public abstract class ScarletSpawnEggItem extends Item {

    public ScarletSpawnEggItem() {
        super(new Item.Properties().group(ModSetup.ITEM_GROUP));
    }

    abstract public EntityType<?> getSummonEntity();

    /**
     * Called when this item is used when targetting a Block
     */
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {
            ItemStack itemstack = context.getItem();
            BlockPos blockpos = context.getPos();
            Direction direction = context.getFace();
            BlockState blockstate = world.getBlockState(blockpos);
            TileEntity tileentity = world.getTileEntity(blockpos);
            if (tileentity instanceof MobSpawnerTileEntity) {
                AbstractSpawner abstractspawner = ((MobSpawnerTileEntity) tileentity).getSpawnerBaseLogic();
                abstractspawner.setEntityType(getSummonEntity());
                tileentity.markDirty();
                world.notifyBlockUpdate(blockpos, blockstate, blockstate, 3);
                itemstack.shrink(1);
                return ActionResultType.SUCCESS;
            } else if (tileentity instanceof SpawnMarkerTile) {
                ((SpawnMarkerTile) tileentity).setEntityType(getSummonEntity());
                tileentity.markDirty();
                world.notifyBlockUpdate(blockpos, blockstate, blockstate, 3);
                itemstack.shrink(1);
                return ActionResultType.SUCCESS;
            }

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.offset(direction);
            }

            if (getSummonEntity().spawn(world, itemstack, context.getPlayer(), blockpos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP) != null) {
                itemstack.shrink(1);
            }

        }
        return ActionResultType.SUCCESS;
    }
}
