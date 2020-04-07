package com.sistr.scarlethill.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.entity.MoltenSlimeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeGelLayer;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class MoltenSlimeRenderer extends MobRenderer<MoltenSlimeEntity, SlimeModel<MoltenSlimeEntity>> {
    private static final ResourceLocation MOLTEN_SLIME_TEXTURES = new ResourceLocation(ScarletHillMod.MODID, "textures/entity/molten_slime.png");

    public MoltenSlimeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new SlimeModel<>(16), 0.25F);
        this.addLayer(new SlimeGelLayer<>(this));
    }

    public void render(MoltenSlimeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        this.shadowSize = 0.25F;
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    protected void preRenderCallback(MoltenSlimeEntity entity, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.999F, 0.999F, 0.999F);
        matrixStackIn.translate(0.0D, 0.001F, 0.0D);
        float size = 2;
        float lerp = MathHelper.lerp(partialTickTime, entity.prevSquishFactor, entity.squishFactor) / (size * 0.5F + 1.0F);
        float scale = 1.0F / (lerp + 1.0F);
        matrixStackIn.scale(scale * size, 1.0F / scale * size, scale * size);

        float flash = entity.getCreeperFlashIntensity(partialTickTime);
        float sin = 1.0F + MathHelper.sin(flash * 100.0F) * flash * 0.01F;
        flash = MathHelper.clamp(flash, 0.0F, 1.0F);
        flash = flash * flash;
        flash = flash * flash;
        float xz = (1.0F + flash * 0.4F) * sin;
        float y = (1.0F + flash * 0.1F) / sin;
        matrixStackIn.scale(xz, y, xz);
    }

    protected float getOverlayProgress(MoltenSlimeEntity livingEntityIn, float partialTicks) {
        float f = livingEntityIn.getCreeperFlashIntensity(partialTicks);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getEntityTexture(MoltenSlimeEntity entity) {
        return MOLTEN_SLIME_TEXTURES;
    }
}
