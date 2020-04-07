package com.sistr.scarlethill.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new Recipes(generator));
        generator.addProvider(new BlockLootTables(generator));
        generator.addProvider(new EntityLootTables(generator));
        generator.addProvider(new ScarletHillAdvancements(generator));
        generator.addProvider(new ScarletBlockTagsProvider(generator));
        generator.addProvider(new ScarletItemTagsProvider(generator));
    }
}
