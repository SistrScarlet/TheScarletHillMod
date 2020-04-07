package com.sistr.scarlethill.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.entity.ScarletBearEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.PolarBearModel;
import net.minecraft.util.ResourceLocation;

public class ScarletBearRenderer extends MobRenderer<ScarletBearEntity, PolarBearModel<ScarletBearEntity>> {
    private static final ResourceLocation POLAR_BEAR_TEXTURE = new ResourceLocation(ScarletHillMod.MODID, "textures/entity/scarlet_bear.png");

    public ScarletBearRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PolarBearModel<>(), 0.9F);
    }

    @Override
    public void render(ScarletBearEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack matrix, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        matrix.push();
        matrix.scale(1.3F, 1.3F, 1.3F);
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, matrix, p_225623_5_, p_225623_6_);
        matrix.pop();
    }

    public ResourceLocation getEntityTexture(ScarletBearEntity entity) {
        return POLAR_BEAR_TEXTURE;
    }

}