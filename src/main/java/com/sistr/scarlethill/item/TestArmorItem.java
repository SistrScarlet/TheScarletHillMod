package com.sistr.scarlethill.item;

import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class TestArmorItem extends Item {

    public TestArmorItem() {
        super(new Item.Properties().group(ModSetup.ITEM_GROUP));
    }

    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }
}
