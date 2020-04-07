package com.sistr.scarlethill.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Items;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

public class ScarletItemTagsProvider extends ForgeItemTagsProvider {

    public ScarletItemTagsProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    public void registerTags() {
        getBuilder(ScarletTags.Items.RED_THINGS).add(Items.RED_DYE, Items.REDSTONE);
    }
}
