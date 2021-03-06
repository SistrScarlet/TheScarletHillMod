package com.sistr.scarlethill.item;

import com.sistr.scarlethill.setup.ModSetup;
import com.sistr.scarlethill.world.MagicSquareManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class RedMagicItem extends Item {

    public RedMagicItem() {
        super(new Item.Properties()
                .maxStackSize(1)
                .group(ModSetup.ITEM_GROUP));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        CompoundNBT tag = stack.getOrCreateTag();
        Vec3d hitVec = context.getHitVec();
        if (!tag.hasUniqueId("RedMagicSquare")
                || !MagicSquareManager.MAGIC_SQUARES.containsKey(tag.getUniqueId("RedMagicSquare"))) {
            UUID id = UUID.randomUUID();
            tag.putUniqueId("RedMagicSquare", id);
            MagicSquareManager.MAGIC_SQUARES.put(id, new MagicSquare(context.getWorld(), id, hitVec,
                    new Vec3d(1, 0, 0), 3, 200));
            return ActionResultType.SUCCESS;
        }
        MagicSquare square = MagicSquareManager.MAGIC_SQUARES.get(tag.getUniqueId("RedMagicSquare"));
        if (!square.canAddVertex(hitVec)) {
            return ActionResultType.SUCCESS;
        }
        square.addVertex(hitVec);
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (!tag.hasUniqueId("RedMagicSquare")
                || !MagicSquareManager.MAGIC_SQUARES.containsKey(tag.getUniqueId("RedMagicSquare"))) {
            return false;
        }
        MagicSquare square = MagicSquareManager.MAGIC_SQUARES.get(tag.getUniqueId("RedMagicSquare"));

        return false;
    }
}
