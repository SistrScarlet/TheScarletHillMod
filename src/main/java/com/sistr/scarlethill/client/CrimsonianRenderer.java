package com.sistr.scarlethill.client;

import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.entity.CrimsonianEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrimsonianRenderer extends BipedRenderer<CrimsonianEntity, CrimsonianModel<CrimsonianEntity>> {
    private static final ResourceLocation CRIMSONIAN_TEXTURES = new ResourceLocation(ScarletHillMod.MODID, "textures/entity/crimsonian.png");
    private static final ResourceLocation CRIMSONIAN_LEADER_TEXTURES = new ResourceLocation(ScarletHillMod.MODID, "textures/entity/crimsonian_leader.png");

    public CrimsonianRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new CrimsonianModel<>(0), 0.25F);
    }

    @Override
    public ResourceLocation getEntityTexture(CrimsonianEntity entity) {
        return entity.isLeader() ? CRIMSONIAN_LEADER_TEXTURES : CRIMSONIAN_TEXTURES;
    }
}
