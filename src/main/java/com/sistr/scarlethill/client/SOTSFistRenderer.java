package com.sistr.scarlethill.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.entity.SOTSFistEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class SOTSFistRenderer extends MobRenderer<SOTSFistEntity, SOTSModel<SOTSFistEntity>> {
    private static final ResourceLocation SOTS_FIST_TEXTURES = new ResourceLocation(ScarletHillMod.MODID, "textures/block/scarlet_magma.png");

    public SOTSFistRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new SOTSModel<>(), 2.0F);
    }

    @Override
    public void render(SOTSFistEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, 240);
    }

    @Override
    public ResourceLocation getEntityTexture(SOTSFistEntity entity) {
        return SOTS_FIST_TEXTURES;
    }
}
