package com.sistr.scarlethill.item;

import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ScarletKeyItem extends Item {

    public ScarletKeyItem() {
        super(new Item.Properties()
                .group(ModSetup.ITEM_GROUP)
                .maxStackSize(1));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip0").applyTextStyle(TextFormatting.GRAY).applyTextStyle(TextFormatting.ITALIC));
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip1").applyTextStyle(TextFormatting.GRAY));
    }

}
