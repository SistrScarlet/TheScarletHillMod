package com.sistr.scarlethill.datagen;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;

import java.util.HashMap;
import java.util.Map;

public class EntityLootTables extends BaseLootTableProvider {
    protected final Map<EntityType<?>, LootTable.Builder> entityLootTables = new HashMap<>();

    public EntityLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    public void act(DirectoryCache cache) {
        super.act(cache);
        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        for (Map.Entry<EntityType<?>, LootTable.Builder> entry : entityLootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParameterSet(LootParameterSets.ENTITY).build());
        }
        writeTables(cache, tables);
    }

    @Override
    public String getName() {
        return "ScarletHillMod EntityLootTables";
    }

    @Override
    protected void addTables() {
        entityLootTables.put(Registration.SCARLET_BEAR_BOSS.get(), LootTable.builder());
        entityLootTables.put(Registration.SOTS_BODY_BOSS.get(), LootTable.builder());
        entityLootTables.put(Registration.SOTS_FIST_BOSS.get(), LootTable.builder());
        entityLootTables.put(Registration.FLAME_ZOMBIE_MOB.get(), LootTable.builder());
        entityLootTables.put(Registration.CHARRED_SKELETON_MOB.get(), LootTable.builder());
        entityLootTables.put(Registration.MOLTEN_SLIME_MOB.get(), LootTable.builder());
        entityLootTables.put(Registration.CRIMSONIAN_MOB.get(), LootTable.builder());
    }
}
