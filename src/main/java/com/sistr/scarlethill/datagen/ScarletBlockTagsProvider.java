package com.sistr.scarlethill.datagen;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;

public class ScarletBlockTagsProvider extends ForgeBlockTagsProvider {

    protected ScarletBlockTagsProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    public void registerTags() {
        getBuilder(Tags.Blocks.STONE).add(Registration.SCARLET_STONE_BLOCK.get());
        getBuilder(BlockTags.STAIRS).add(Registration.SCARLET_STONE_STAIRS_BLOCK.get());
        getBuilder(BlockTags.SLABS).add(Registration.SCARLET_STONE_SLAB_BLOCK.get());
        getBuilder(BlockTags.WALLS).add(Registration.SCARLET_STONE_WALL_BLOCK.get());
        getBuilder(Tags.Blocks.COBBLESTONE).add(Registration.SCARLET_COBBLE_STONE_BLOCK.get());
        getBuilder(BlockTags.STAIRS).add(Registration.SCARLET_COBBLE_STONE_STAIRS_BLOCK.get());
        getBuilder(BlockTags.SLABS).add(Registration.SCARLET_COBBLE_STONE_SLAB_BLOCK.get());
        getBuilder(BlockTags.WALLS).add(Registration.SCARLET_COBBLE_STONE_WALL_BLOCK.get());
        getBuilder(BlockTags.STONE_BRICKS).add(Registration.SCARLET_STONE_BRICKS_BLOCK.get());
        getBuilder(BlockTags.STAIRS).add(Registration.SCARLET_STONE_BRICK_STAIRS_BLOCK.get());
        getBuilder(BlockTags.SLABS).add(Registration.SCARLET_STONE_BRICK_SLAB_BLOCK.get());
        getBuilder(BlockTags.WALLS).add(Registration.SCARLET_STONE_BRICK_WALL_BLOCK.get());
        getBuilder(BlockTags.LOGS).add(Registration.SCARLET_LOG_BLOCK.get());
        getBuilder(BlockTags.PLANKS).add(Registration.SCARLET_PLANKS_BLOCK.get());
        getBuilder(BlockTags.WOODEN_STAIRS).add(Registration.SCARLET_PLANKS_STAIRS_BLOCK.get());
        getBuilder(BlockTags.WOODEN_SLABS).add(Registration.SCARLET_PLANKS_SLAB_BLOCK.get());
        getBuilder(BlockTags.LEAVES).add(Registration.SCARLET_LEAVES_BLOCK.get());
        getBuilder(BlockTags.SAPLINGS).add(Registration.SCARLET_SAPLING_BLOCK.get());
        getBuilder(BlockTags.SAND).add(Registration.SCARLET_SAND_BLOCK.get());
        getBuilder(Tags.Blocks.GLASS).add(Registration.SCARLET_GLASS_BLOCK.get());
        getBuilder(Tags.Blocks.GLASS_RED).add(Registration.SCARLET_GLASS_BLOCK.get());
        getBuilder(BlockTags.ICE).add(Registration.SCARLET_ICE_BLOCK.get());
        getBuilder(BlockTags.PORTALS).add(Registration.SCARLET_PORTAL_BLOCK.get());
    }
}
