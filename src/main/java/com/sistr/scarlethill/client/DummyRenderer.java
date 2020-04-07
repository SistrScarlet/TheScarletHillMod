package com.sistr.scarlethill.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sistr.scarlethill.entity.DummyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class DummyRenderer extends EntityRenderer<DummyEntity> {

    protected DummyRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(DummyEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        entityIn.getDummyTarget().ifPresent(target -> Minecraft.getInstance().getRenderManager().getRenderer(target)
                .render(target, MathHelper.lerp(partialTicks, target.prevRotationYaw, target.rotationYaw), partialTicks, matrixStackIn, bufferIn, packedLightIn));
    }

    @Override
    public ResourceLocation getEntityTexture(DummyEntity entity) {
        return null;
    }
}
