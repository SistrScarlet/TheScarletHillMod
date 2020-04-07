package com.sistr.scarlethill.setup;

import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.block.*;
import com.sistr.scarlethill.block.tile.*;
import com.sistr.scarlethill.effect.ScarletBlessing;
import com.sistr.scarlethill.entity.*;
import com.sistr.scarlethill.entity.projectile.MagmaProjectileEntity;
import com.sistr.scarlethill.entity.projectile.RockProjectileEntity;
import com.sistr.scarlethill.item.*;
import com.sistr.scarlethill.item.egg.*;
import com.sistr.scarlethill.world.Feature.*;
import com.sistr.scarlethill.world.biome.*;
import com.sistr.scarlethill.world.dimension.ScarletModDimension;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.sistr.scarlethill.ScarletHillMod.MODID;

public class Registration {
    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MODID);
    private static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, MODID);
    private static final DeferredRegister<Biome> BIOMES = new DeferredRegister<>(ForgeRegistries.BIOMES, MODID);
    private static final DeferredRegister<ModDimension> DIMENSIONS = new DeferredRegister<>(ForgeRegistries.MOD_DIMENSIONS, MODID);
    private static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, MODID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = new DeferredRegister<>(ForgeRegistries.PARTICLE_TYPES, MODID);
    private static final DeferredRegister<SoundEvent> SOUNDS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, MODID);
    private static final DeferredRegister<Effect> POTIONS = new DeferredRegister<>(ForgeRegistries.POTIONS, MODID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
        DIMENSIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static final Item.Properties properties = new Item.Properties().group(ModSetup.ITEM_GROUP);

    //Blockレジストリ
    public static final RegistryObject<ScarletStoneBlock> SCARLET_STONE_BLOCK = BLOCKS.register("scarlet_stone", ScarletStoneBlock::new);
    public static final RegistryObject<ScarletLogBlock> SCARLET_LOG_BLOCK = BLOCKS.register("scarlet_log", ScarletLogBlock::new);
    public static final RegistryObject<ScarletPlanksBlock> SCARLET_PLANKS_BLOCK = BLOCKS.register("scarlet_planks", ScarletPlanksBlock::new);
    public static final RegistryObject<ScarletPlanksStairsBlock> SCARLET_PLANKS_STAIRS_BLOCK = BLOCKS.register("scarlet_planks_stairs", ScarletPlanksStairsBlock::new);
    public static final RegistryObject<ScarletPlanksSlabBlock> SCARLET_PLANKS_SLAB_BLOCK = BLOCKS.register("scarlet_planks_slab", ScarletPlanksSlabBlock::new);
    public static final RegistryObject<ScarletLeavesBlock> SCARLET_LEAVES_BLOCK = BLOCKS.register("scarlet_leaves", ScarletLeavesBlock::new);
    public static final RegistryObject<ScarletSaplingBlock> SCARLET_SAPLING_BLOCK = BLOCKS.register("scarlet_sapling", ScarletSaplingBlock::new);
    public static final RegistryObject<ScarletSandBlock> SCARLET_SAND_BLOCK = BLOCKS.register("scarlet_sand", ScarletSandBlock::new);
    public static final RegistryObject<ScarletGlassBlock> SCARLET_GLASS_BLOCK = BLOCKS.register("scarlet_glass", ScarletGlassBlock::new);
    public static final RegistryObject<ScarletSnowBlock> SCARLET_SNOW_BLOCK = BLOCKS.register("scarlet_snow", ScarletSnowBlock::new);
    public static final RegistryObject<ScarletIceBlock> SCARLET_ICE_BLOCK = BLOCKS.register("scarlet_ice", ScarletIceBlock::new);
    public static final RegistryObject<ScarletMagmaBlock> SCARLET_MAGMA_BLOCK = BLOCKS.register("scarlet_magma", ScarletMagmaBlock::new);
    public static final RegistryObject<ScarletPortalBlock> SCARLET_PORTAL_BLOCK = BLOCKS.register("scarlet_portal", ScarletPortalBlock::new);

    //BlockItemレジストリ
    public static final RegistryObject<Item> SCARLET_STONE_ITEM = ITEMS.register("scarlet_stone", () -> new BlockItem(SCARLET_STONE_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_LOG_ITEM = ITEMS.register("scarlet_log", () -> new BlockItem(SCARLET_LOG_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_PLANKS_ITEM = ITEMS.register("scarlet_planks", () -> new BlockItem(SCARLET_PLANKS_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_PLANKS_STAIRS_ITEM = ITEMS.register("scarlet_planks_stairs", () -> new BlockItem(SCARLET_PLANKS_STAIRS_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_PLANKS_SLAB_ITEM = ITEMS.register("scarlet_planks_slab", () -> new BlockItem(SCARLET_PLANKS_SLAB_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_LEAVES_ITEM = ITEMS.register("scarlet_leaves", () -> new BlockItem(SCARLET_LEAVES_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_SAPLING_ITEM = ITEMS.register("scarlet_sapling", () -> new BlockItem(SCARLET_SAPLING_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_SAND_ITEM = ITEMS.register("scarlet_sand", () -> new BlockItem(SCARLET_SAND_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_GLASS_ITEM = ITEMS.register("scarlet_glass", () -> new BlockItem(SCARLET_GLASS_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_SNOW_ITEM = ITEMS.register("scarlet_snow", () -> new BlockItem(SCARLET_SNOW_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_ICE_ITEM = ITEMS.register("scarlet_ice", () -> new BlockItem(SCARLET_ICE_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_MAGMA_ITEM = ITEMS.register("scarlet_magma", () -> new BlockItem(SCARLET_MAGMA_BLOCK.get(), properties));
    public static final RegistryObject<Item> SCARLET_PORTAL_ITEM = ITEMS.register("scarlet_portal", () -> new BlockItem(SCARLET_PORTAL_BLOCK.get(), properties));

    //todo 黄昏みたいにアイテムロストや死を堪えるやつほしい
    //アイテムレジストリ
    public static final RegistryObject<ScarletKeyItem> SCARLET_KEY_ITEM = ITEMS.register("scarlet_key", ScarletKeyItem::new);
    public static final RegistryObject<ScarletBearClawItem> SCARLET_BEAR_CLAW_ITEM = ITEMS.register("scarlet_bear_claw", ScarletBearClawItem::new);
    public static final RegistryObject<ScarletGemItem> SCARLET_GEM_ITEM = ITEMS.register("scarlet_gem", ScarletGemItem::new);
    public static final RegistryObject<ScarletSnowBallItem> SCARLET_SNOWBALL_ITEM = ITEMS.register("scarlet_snowball", ScarletSnowBallItem::new);
    public static final RegistryObject<LavaSpitItem> LAVA_SPIT_ITEM = ITEMS.register("lava_spit", LavaSpitItem::new);
    public static final RegistryObject<CreativeFillToolItem> CREATIVE_FILL_TOOL_ITEM = ITEMS.register("fill_tool", CreativeFillToolItem::new);
    public static final RegistryObject<CreativeWrenchItem> CREATIVE_WRENCH_ITEM = ITEMS.register("wrench", CreativeWrenchItem::new);
    public static final RegistryObject<SearchStructureTool> SEARCH_STRUCTURE_TOOL_ITEM = ITEMS.register("search_structure_tool", SearchStructureTool::new);
    public static final RegistryObject<TestArmorItem> TEST_ARMOR_ITEM = ITEMS.register("test_armor", TestArmorItem::new);

    //スポーンエッグレジストリ
    public static final RegistryObject<ScarletBearSpawnEggItem> SCARLET_BEAR_SPAWN_EGG_ITEM = ITEMS.register("scarlet_bear_spawn_egg", ScarletBearSpawnEggItem::new);
    public static final RegistryObject<FlameZombieSpawnEggItem> FLAME_ZOMBIE_SPAWN_EGG_ITEM = ITEMS.register("flame_zombie_spawn_egg", FlameZombieSpawnEggItem::new);
    public static final RegistryObject<MoltenSlimeSpawnEggItem> MOLTEN_SLIME_SPAWN_EGG_ITEM = ITEMS.register("molten_slime_spawn_egg", MoltenSlimeSpawnEggItem::new);
    public static final RegistryObject<CharredSkeletonSpawnEggItem> CHARRED_SKELETON_SPAWN_EGG_ITEM = ITEMS.register("charred_skeleton_spawn_egg", CharredSkeletonSpawnEggItem::new);
    public static final RegistryObject<CrimsonianSpawnEggItem> CRIMSONIAN_SPAWN_EGG_ITEM = ITEMS.register("crimsonian_spawn_egg", CrimsonianSpawnEggItem::new);
    public static final RegistryObject<SOTSSpawnEggItem> SOTS_SPAWN_EGG_ITEM = ITEMS.register("spawn_of_the_scarlet_spawn_egg", SOTSSpawnEggItem::new);

    //エンティティレジストリ
    public static final RegistryObject<EntityType<ScarletBearEntity>> SCARLET_BEAR_BOSS = ENTITIES.register("scarlet_bear", () ->
            EntityType.Builder.create((EntityType.IFactory<ScarletBearEntity>) ScarletBearEntity::new, EntityClassification.CREATURE)
                    .size(1.8F, 1.8F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("scarlet_bear"));
    public static final RegistryObject<EntityType<SOTSBodyEntity>> SOTS_BODY_BOSS = ENTITIES.register("spawn_of_the_scarlet_body", () ->
            EntityType.Builder.create((EntityType.IFactory<SOTSBodyEntity>) SOTSBodyEntity::new, EntityClassification.CREATURE)
                    .size(1.9975F, 1.9975F)
                    .setShouldReceiveVelocityUpdates(false)
                    .immuneToFire()
                    .build("spawn_of_the_scarlet_body"));
    public static final RegistryObject<EntityType<SOTSFistEntity>> SOTS_FIST_BOSS = ENTITIES.register("spawn_of_the_scarlet_fist", () ->
            EntityType.Builder.create((EntityType.IFactory<SOTSFistEntity>) SOTSFistEntity::new, EntityClassification.CREATURE)
                    .size(1.9975F, 1.9975F)
                    .setShouldReceiveVelocityUpdates(false)
                    .immuneToFire()
                    .build("spawn_of_the_scarlet_fist"));
    public static final RegistryObject<EntityType<FlameZombieEntity>> FLAME_ZOMBIE_MOB = ENTITIES.register("flame_zombie", () ->
            EntityType.Builder.create(FlameZombieEntity::new, EntityClassification.MONSTER)
                    .size(0.6F, 1.95F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("flame_zombie"));
    public static final RegistryObject<EntityType<CharredSkeletonEntity>> CHARRED_SKELETON_MOB = ENTITIES.register("charred_skeleton", () ->
            EntityType.Builder.create(CharredSkeletonEntity::new, EntityClassification.MONSTER)
                    .size(0.6F, 1.99F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("charred_skeleton"));
    public static final RegistryObject<EntityType<MoltenSlimeEntity>> MOLTEN_SLIME_MOB = ENTITIES.register("molten_slime", () ->
            EntityType.Builder.create(MoltenSlimeEntity::new, EntityClassification.MONSTER)
                    .size(0.95F, 0.95F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("molten_slime"));
    public static final RegistryObject<EntityType<CrimsonianEntity>> CRIMSONIAN_MOB = ENTITIES.register("crimsonian", () ->
            EntityType.Builder.create((EntityType.IFactory<CrimsonianEntity>) CrimsonianEntity::new, EntityClassification.CREATURE)
                    .size(0.6F, 0.95F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("crimsonian"));
    public static final RegistryObject<EntityType<BlockEntity>> BLOCK_ENTITY = ENTITIES.register("block_entity", () ->
            EntityType.Builder.create((EntityType.IFactory<BlockEntity>) BlockEntity::new, EntityClassification.MISC)
                    .size(0.98F, 0.98F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("block_entity"));
    public static final RegistryObject<EntityType<RockProjectileEntity>> ROCK_PROJECTILE = ENTITIES.register("rock_projectile", () ->
            EntityType.Builder.create((EntityType.IFactory<RockProjectileEntity>) RockProjectileEntity::new, EntityClassification.MISC)
                    .size(2.0F, 2.0F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("rock_projectile"));
    public static final RegistryObject<EntityType<MagmaProjectileEntity>> MAGMA_PROJECTILE = ENTITIES.register("magma_projectile", () ->
            EntityType.Builder.create((EntityType.IFactory<MagmaProjectileEntity>) MagmaProjectileEntity::new, EntityClassification.MISC)
                    .size(0.5F, 0.5F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("magma_projectile"));
    public static final RegistryObject<EntityType<DummyEntity>> DUMMY_ENTITY = ENTITIES.register("dummy", () ->
            EntityType.Builder.create((EntityType.IFactory<DummyEntity>) DummyEntity::new, EntityClassification.MISC)
                    .size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                    .setShouldReceiveVelocityUpdates(false)
                    .build("dummy"));

    //フィーチャーレジストリ
    //ストラクチャ/フィーチャーの登録順かなんかの問題で分割している
    //Biome以外ではRegistryObjectから取ること
    public static Structure<NoFeatureConfig> SCARLET_PORTAL_STRUCTURE_BEFORE = new ScarletPortalStructure(NoFeatureConfig::deserialize);
    public static Structure<NoFeatureConfig> SCARLET_CRIMSONIAN_VILLAGE_BEFORE = new CrimsonianVillageStructure(NoFeatureConfig::deserialize);
    public static Structure<NoFeatureConfig> SCARLET_BEAR_NEST_STRUCTURE_BEFORE = new ScarletBearNestStructure(NoFeatureConfig::deserialize);
    public static Structure<NoFeatureConfig> MOLTEN_MINE_STRUCTURE_BEFORE = new MoltenMineStructure(NoFeatureConfig::deserialize);
    public static Feature<NoFeatureConfig> SCARLET_FREEZE_TOP_LAYER_BEFORE = new ScarletIceAndSnowFeature(NoFeatureConfig::deserialize);

    public static final RegistryObject<Structure<NoFeatureConfig>> SCARLET_PORTAL_STRUCTURE = FEATURES.register("scarlet_portal", () -> SCARLET_PORTAL_STRUCTURE_BEFORE);
    public static final RegistryObject<Structure<NoFeatureConfig>> SCARLET_CRIMSONIAN_VILLAGE_STRUCTURE = FEATURES.register("crimsonian_village", () -> SCARLET_CRIMSONIAN_VILLAGE_BEFORE);
    public static final RegistryObject<Structure<NoFeatureConfig>> SCARLET_BEAR_NEST_STRUCTURE = FEATURES.register("scarlet_bear_nest", () -> SCARLET_BEAR_NEST_STRUCTURE_BEFORE);
    public static final RegistryObject<Structure<NoFeatureConfig>> MOLTEN_MINE_STRUCTURE = FEATURES.register("molten_mine", () -> MOLTEN_MINE_STRUCTURE_BEFORE);
    public static final RegistryObject<Feature<NoFeatureConfig>> SCARLET_FREEZE_TOP_LAYER = FEATURES.register("scarlet_freeze_top_layer", () -> SCARLET_FREEZE_TOP_LAYER_BEFORE);

    //バイオームレジストリ
    public static final RegistryObject<ScarletOceanBiome> SCARLET_OCEAN_BIOME = BIOMES.register("scarlet_ocean", ScarletOceanBiome::new);
    public static final RegistryObject<ScarletRiverBiome> SCARLET_RIVER_BIOME = BIOMES.register("scarlet_river", ScarletRiverBiome::new);
    public static final RegistryObject<ScarletFrozenRiverBiome> SCARLET_FROZEN_RIVER_BIOME = BIOMES.register("scarlet_frozen_river", ScarletFrozenRiverBiome::new);
    public static final RegistryObject<ScarletHillBiome> SCARLET_HILL_BIOME = BIOMES.register("scarlet_hill", ScarletHillBiome::new);
    public static final RegistryObject<ScarletPlainBiome> SCARLET_PLAIN_BIOME = BIOMES.register("scarlet_plain", ScarletPlainBiome::new);
    public static final RegistryObject<ScarletForestBiome> SCARLET_FOREST_BIOME = BIOMES.register("scarlet_forest", ScarletForestBiome::new);
    public static final RegistryObject<ScarletMountainBiome> SCARLET_MOUNTAIN_BIOME = BIOMES.register("scarlet_mountain", ScarletMountainBiome::new);
    public static final RegistryObject<ScarletDesertBiome> SCARLET_DESERT_BIOME = BIOMES.register("scarlet_desert", ScarletDesertBiome::new);
    public static final RegistryObject<ScarletSnowyTundraBiome> SCARLET_SNOWY_TUNDRA_BIOME = BIOMES.register("scarlet_snowy_tundra", ScarletSnowyTundraBiome::new);

    //ディメンジョンレジストリ
    public static final RegistryObject<ScarletModDimension> SCARLETHILL_DIM = DIMENSIONS.register("dimension", ScarletModDimension::new);

    //機能付きブロック
    public static final RegistryObject<FootPrintBlock> FOOTPRINT_BLOCK = BLOCKS.register("footprint", FootPrintBlock::new);
    public static final RegistryObject<Item> FOOTPRINT_ITEM = ITEMS.register("footprint", () ->
            new BlockItem(FOOTPRINT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<TileEntityType<FootPrintTile>> FOOTPRINT_TILE = TILES.register("footprint", () ->
            TileEntityType.Builder.create(FootPrintTile::new, FOOTPRINT_BLOCK.get()).build(null));

    public static final RegistryObject<AreaSpawnerBlock> AREA_SPAWNER_BLOCK = BLOCKS.register("area_spawner", AreaSpawnerBlock::new);
    public static final RegistryObject<Item> AREA_SPAWNER_ITEM = ITEMS.register("area_spawner", () ->
            new BlockItem(AREA_SPAWNER_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<TileEntityType<AreaSpawnerTile>> AREA_SPAWNER_TILE = TILES.register("area_spawner", () ->
            TileEntityType.Builder.create(AreaSpawnerTile::new, AREA_SPAWNER_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<AreaSpawnerContainer>> AREA_SPAWNER_CONTAINER = CONTAINERS.register("area_spawner", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new AreaSpawnerContainer(windowId, ScarletHillMod.proxy.getClientWorld(), pos, inv, ScarletHillMod.proxy.getClientPlayer());
    }));

    public static final RegistryObject<SpawnMarkerBlock> SPAWNER_MARKER_BLOCK = BLOCKS.register("spawn_marker", SpawnMarkerBlock::new);
    public static final RegistryObject<Item> SPAWNER_MARKER_ITEM = ITEMS.register("spawn_marker", () ->
            new BlockItem(SPAWNER_MARKER_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<TileEntityType<SpawnMarkerTile>> SPAWNER_MARKER_TILE = TILES.register("spawn_marker", () ->
            TileEntityType.Builder.create(SpawnMarkerTile::new, SPAWNER_MARKER_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<SpawnMarkerContainer>> SPAWNER_MARKER_CONTAINER = CONTAINERS.register("spawn_marker", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new SpawnMarkerContainer(windowId, ScarletHillMod.proxy.getClientWorld(), pos, inv, ScarletHillMod.proxy.getClientPlayer());
    }));

    //パーティクルレジストリ ClientSetupにて
    public static final RegistryObject<BasicParticleType> SCARLET_PORTAL_PARTICLE = PARTICLES.register("scarlet_portal", () -> new BasicParticleType(false));

    //サウンドレジストリ
    public static final RegistryObject<SoundEvent> WHOOSH_MEDIUM = SOUNDS.register("whoosh_medium", () -> new SoundEvent(new ResourceLocation(MODID, "whoosh_medium")));
    public static final RegistryObject<SoundEvent> SKILL_WARN = SOUNDS.register("angry", () -> new SoundEvent(new ResourceLocation(MODID, "angry")));
    public static final RegistryObject<SoundEvent> SKILL_DANG = SOUNDS.register("dramatic", () -> new SoundEvent(new ResourceLocation(MODID, "dramatic")));
    public static final RegistryObject<SoundEvent> SKILL_PLOPS = SOUNDS.register("plops", () -> new SoundEvent(new ResourceLocation(MODID, "plops")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_YEHAR = SOUNDS.register("yehar", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_yehar")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_SIG = SOUNDS.register("sig", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_sig")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_DAS = SOUNDS.register("das", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_das")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_ZAN = SOUNDS.register("zan", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_zan")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_NID = SOUNDS.register("nid", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_nid")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_GA = SOUNDS.register("ga", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_ga")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_EEL = SOUNDS.register("eel", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_eel")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_SCARLET = SOUNDS.register("scarlet", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_scarlet")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_GUTE = SOUNDS.register("gute", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_gute")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_HYF = SOUNDS.register("hyf", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_hyf")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_NIL = SOUNDS.register("nil", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_nil")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_MEL = SOUNDS.register("mel", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_mel")));
    public static final RegistryObject<SoundEvent> CRIMSONIAN_HEW = SOUNDS.register("hew", () -> new SoundEvent(new ResourceLocation(MODID, "crimsonian_hew")));
    public static final RegistryObject<SoundEvent> NONE = SOUNDS.register("none", () -> new SoundEvent(new ResourceLocation(MODID, "none")));

    //ポーションレジストリ
    public static final RegistryObject<Effect> SCARLET_BLESSING = POTIONS.register("scarlet_blessing", ScarletBlessing::new);
}
