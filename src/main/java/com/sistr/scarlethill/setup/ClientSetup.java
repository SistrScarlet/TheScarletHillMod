package com.sistr.scarlethill.setup;

import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.block.tile.AreaSpawnerScreen;
import com.sistr.scarlethill.block.tile.FootPrintModelLoader;
import com.sistr.scarlethill.block.tile.SpawnMarkerScreen;
import com.sistr.scarlethill.client.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ScarletHillMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(Registration.SPAWNER_MARKER_CONTAINER.get(), SpawnMarkerScreen::new);
        ScreenManager.registerFactory(Registration.AREA_SPAWNER_CONTAINER.get(), AreaSpawnerScreen::new);

        RenderingRegistry.registerEntityRenderingHandler(Registration.SCARLET_BEAR_BOSS.get(), ScarletBearRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.SOTS_BODY_BOSS.get(), SOTSRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.SOTS_FIST_BOSS.get(), SOTSFistRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.ROBE_BOSS.get(), RobeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.FLAME_ZOMBIE_MOB.get(), FlameZombieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.CHARRED_SKELETON_MOB.get(), CharredSkeletonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.MOLTEN_SLIME_MOB.get(), MoltenSlimeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.CRIMSONIAN_MOB.get(), CrimsonianRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.ROCK_PROJECTILE.get(), RockProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.MAGMA_PROJECTILE.get(), manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(Registration.SCARLET_PROJECTILE.get(), manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(Registration.BLOCK_ENTITY.get(), BlockEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.DUMMY_ENTITY.get(), DummyRenderer::new);

        ModelLoaderRegistry.registerLoader(new ResourceLocation(ScarletHillMod.MODID, "footprintloader"), new FootPrintModelLoader());

        RenderTypeLookup.setRenderLayer(Registration.SCARLET_SAPLING_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(Registration.SCARLET_GLASS_BLOCK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(Registration.SCARLET_ICE_BLOCK.get(), RenderType.getTranslucent());

        MinecraftForge.EVENT_BUS.addListener(MagicSquareRenderer::onWorldRender);

        //todo 緋色の炎のパーティクル
        //パーティクルのファクトリーのレジストリ
        Minecraft.getInstance().particles.registerFactory(Registration.SCARLET_PORTAL_PARTICLE.get(), ScarletPortalParticle.Factory::new);

    }

}
