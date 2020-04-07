package com.sistr.scarlethill.client;

import com.sistr.scarlethill.ScarletHillMod;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.util.ResourceLocation;

public class CharredSkeletonRenderer extends SkeletonRenderer {
    private static final ResourceLocation CHARRED_SKELETON_TEXTURES = new ResourceLocation(ScarletHillMod.MODID, "textures/entity/charred_skeleton.png");

    public CharredSkeletonRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractSkeletonEntity entity) {
        return CHARRED_SKELETON_TEXTURES;
    }
}
