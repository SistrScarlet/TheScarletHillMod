package com.sistr.scarlethill.client;

import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.entity.FlameZombieEntity;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlameZombieRenderer extends AbstractZombieRenderer<FlameZombieEntity, ZombieModel<FlameZombieEntity>> {
    private static final ResourceLocation FLAME_ZOMBIE_TEXTURE = new ResourceLocation(ScarletHillMod.MODID, "textures/entity/flame_zombie.png");

    public FlameZombieRenderer(EntityRendererManager manager) {
        super(manager, new ZombieModel<>(0.0F, false), new ZombieModel<>(0.5F, true), new ZombieModel<>(1.0F, true));
    }

    @Override
    public ResourceLocation getEntityTexture(ZombieEntity entity) {
        return FLAME_ZOMBIE_TEXTURE;
    }
}
