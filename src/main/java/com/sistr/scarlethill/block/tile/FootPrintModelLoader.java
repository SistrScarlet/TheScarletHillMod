package com.sistr.scarlethill.block.tile;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;

public class FootPrintModelLoader implements IModelLoader<FootPrintModelGeometry> {

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public FootPrintModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return new FootPrintModelGeometry();
    }
}
