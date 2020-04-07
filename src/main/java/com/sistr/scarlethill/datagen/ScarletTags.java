package com.sistr.scarlethill.datagen;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ScarletTags {

    public static class Blocks {
        public static final Tag<Block> SAMPLE = tag("sample");

        private static Tag<Block> tag(String name) {
            return new BlockTags.Wrapper(new ResourceLocation("scarlethill", name));
        }
    }

    public static class Items {
        public static final Tag<Item> RED_THINGS = tag("red_things");

        private static Tag<Item> tag(String name) {
            return new ItemTags.Wrapper(new ResourceLocation("scarlethill", name));
        }
    }
}
