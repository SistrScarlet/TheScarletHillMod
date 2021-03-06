package com.sistr.scarlethill.client;

import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.entity.RobeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;

public class RobeRenderer extends LivingRenderer<RobeEntity, RobeModel<RobeEntity>> {
    private static final ResourceLocation ROBE_TEXTURE = new ResourceLocation(ScarletHillMod.MODID, "textures/entity/robe.png");

    public RobeRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new RobeModel<>(), 0.5F);
        this.addLayer(new HeldItemLayer<>(this));
    }

    @Override
    public ResourceLocation getEntityTexture(RobeEntity entity) {
        return ROBE_TEXTURE;
    }
}
