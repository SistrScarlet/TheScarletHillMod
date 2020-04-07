package com.sistr.scarlethill.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public abstract class BaseAdvancementProvider extends AdvancementProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public BaseAdvancementProvider(DataGenerator generatorIn) {
        super(generatorIn);
        this.generator = generatorIn;
    }

    protected abstract void addTables();

    @Override
    public void act(DirectoryCache cache) {
        addTables();
    }

    protected void writeTables(DirectoryCache cache, Map<ResourceLocation, Advancement> tables) {
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, advancement) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/advancements/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, advancement.copy().serialize(), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't write advancement {}", path, e);
            }
        });
    }

    @Override
    public abstract String getName();
}
