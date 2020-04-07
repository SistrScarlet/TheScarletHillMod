package com.sistr.scarlethill.setup;

import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.block.tile.AreaSpawnerScreen;
import com.sistr.scarlethill.block.tile.FootPrintModelLoader;
import com.sistr.scarlethill.block.tile.SpawnMarkerScreen;
import com.sistr.scarlethill.client.*;
import com.sistr.scarlethill.item.TestArmorItem;
import com.sistr.scarlethill.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ScarletHillMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    private static final RSHUDRenderer RSHUD_RENDERER = new RSHUDRenderer();
    private static final CustomThirdPersonRender thirdPersonRender = new CustomThirdPersonRender();

    public static void init(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(Registration.SPAWNER_MARKER_CONTAINER.get(), SpawnMarkerScreen::new);
        ScreenManager.registerFactory(Registration.AREA_SPAWNER_CONTAINER.get(), AreaSpawnerScreen::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.SCARLET_BEAR_BOSS.get(), ScarletBearRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.SOTS_BODY_BOSS.get(), SOTSRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.SOTS_FIST_BOSS.get(), SOTSFistRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.FLAME_ZOMBIE_MOB.get(), FlameZombieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.CHARRED_SKELETON_MOB.get(), CharredSkeletonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.MOLTEN_SLIME_MOB.get(), MoltenSlimeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.CRIMSONIAN_MOB.get(), CrimsonianRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.ROCK_PROJECTILE.get(), RockProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.MAGMA_PROJECTILE.get(), manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        ModelLoaderRegistry.registerLoader(new ResourceLocation(ScarletHillMod.MODID, "footprintloader"), new FootPrintModelLoader());
        RenderTypeLookup.setRenderLayer(Registration.SCARLET_SAPLING_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(Registration.SCARLET_GLASS_BLOCK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(Registration.SCARLET_ICE_BLOCK.get(), RenderType.getTranslucent());

        //パーティクルのファクトリーのレジストリ
        event.getMinecraftSupplier().get().particles.registerFactory(Registration.SCARLET_PORTAL_PARTICLE.get(), ScarletPortalParticle.Factory::new);

        //レイヤーの追加
        event.getMinecraftSupplier().get().getRenderManager().getSkinMap().forEach(((s, renderer) -> renderer.addLayer(new TestArmorLayer<>((PlayerRenderer) renderer))));

        MinecraftForge.EVENT_BUS.register(ClientSetup.class);

    }

    //移動方向をカメラ基準に
    //ただしダッシュ中は無視
    @SubscribeEvent
    public static void onInputUpdate(InputUpdateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameSettings.thirdPersonView == 1 && !mc.gameSettings.keyBindSprint.isKeyDown()) {
            PlayerEntity player = ScarletHillMod.proxy.getClientPlayer();
            if (player.isSprinting()) {
                return;
            }
            MovementInput input = event.getMovementInput();
            float tempForward = input.moveForward;//input.forwardKeyDown == input.backKeyDown ? 0.0F : (input.forwardKeyDown ? 1.0F : -1.0F);
            float tempStrife = input.moveStrafe;//input.leftKeyDown == input.rightKeyDown ? 0.0F : (input.leftKeyDown ? 1.0F : -1.0F);
            if (tempForward == 0 && tempStrife == 0) {
                return;
            }
            float yaw = MathUtil.getYaw(tempStrife, tempForward);
            //float partialTick = Minecraft.getInstance().getRenderPartialTicks();
            //yaw += MathHelper.lerp(partialTick, player.prevRotationYaw, player.rotationYaw) - MathHelper.lerp(partialTick, thirdPersonRender.cameraPrevYaw, thirdPersonRender.cameraYaw);
            yaw += player.rotationYaw - thirdPersonRender.cameraYaw;
            Vec3d move = MathUtil.getVector(new Vec2f(yaw, 0));
            input.moveForward = (float) move.z;
            input.moveStrafe = (float) move.x;
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            thirdPersonRender.tick();
        }
    }

    @SubscribeEvent
    public static void onDisplayRender(EntityViewRenderEvent.CameraSetup event) {
        ActiveRenderInfo info = event.getInfo();
        if (info.isThirdPerson() && Minecraft.getInstance().gameSettings.thirdPersonView == 1) {
            float partialTicks = (float) event.getRenderPartialTicks();
            thirdPersonRender.render(info.getRenderViewEntity(), info, partialTicks);
            event.setPitch(MathHelper.lerp(partialTicks, thirdPersonRender.cameraPrevPitch, thirdPersonRender.cameraPitch));
            event.setYaw(MathHelper.lerp(partialTicks, thirdPersonRender.cameraPrevYaw, thirdPersonRender.cameraYaw));
        } else {
            thirdPersonRender.reset();
        }
    }

    /*
    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Entity entity = thirdPersonRender.followTarget;
        if (entity != null) {
            Minecraft mc = Minecraft.getInstance();
            GameRenderer gameRenderer = mc.gameRenderer;
            MatrixStack matrixStackIn = event.getMatrixStack();
            Vec3d projectedView = gameRenderer.getActiveRenderInfo().getProjectedView();
            EntityRendererManager manager = mc.getRenderManager();
            RenderTypeBuffers buf = mc.getRenderTypeBuffers();

            if (entity.ticksExisted == 0) {
                entity.lastTickPosX = entity.getPosX();
                entity.lastTickPosY = entity.getPosY();
                entity.lastTickPosZ = entity.getPosZ();
            }
            IRenderTypeBuffer.Impl impl = buf.getBufferSource();

            IRenderTypeBuffer typeBuffer;
            if (entity.isGlowing()) {
                OutlineLayerBuffer outlinelayerbuffer = buf.getOutlineBufferSource();
                typeBuffer = outlinelayerbuffer;
                int i2 = entity.getTeamColor();
                int k2 = i2 >> 16 & 255;
                int l2 = i2 >> 8 & 255;
                int i3 = i2 & 255;
                outlinelayerbuffer.setColor(k2, l2, i3, 255);
            } else {
                typeBuffer = impl;
            }
            double camX = projectedView.x;
            double camY = projectedView.y;
            double camZ = projectedView.z;
            float partialTicks = mc.getRenderPartialTicks();//event.getPartialTicks();
            double x = MathHelper.lerp(partialTicks, entity.lastTickPosX, entity.getPosX());
            double y = MathHelper.lerp(partialTicks, entity.lastTickPosY, entity.getPosY());
            double z = MathHelper.lerp(partialTicks, entity.lastTickPosZ, entity.getPosZ());
            float yaw = MathHelper.lerp(partialTicks, entity.prevRotationYaw, entity.rotationYaw);
            manager.renderEntityStatic(entity, x - camX, y - camY, z - camZ, yaw, partialTicks, matrixStackIn, typeBuffer, manager.getPackedLight(entity, partialTicks));

            EntityRenderer<Entity> entityrenderer = (EntityRenderer<Entity>) manager.getRenderer(entity);
            Vec3d vec3d = entityrenderer.getRenderOffset(entity, partialTicks);
            double d2 = x - camX + vec3d.getX();
            double d3 = y - camY + vec3d.getY();
            double d0 = z - camZ + vec3d.getZ();
            matrixStackIn.push();

            matrixStackIn.translate(d2, d3, d0);
            entityrenderer.render(entity, yaw, partialTicks, matrixStackIn, typeBuffer, manager.getPackedLight(entity, partialTicks));


            matrixStackIn.pop();


        }
    }
    */


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDisplayRender(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            return;
        }
        PlayerEntity player = ScarletHillMod.proxy.getClientPlayer();
        ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
        if (!(stack.getItem() instanceof TestArmorItem)) {
            return;
        }
        RSHUD_RENDERER.render(player, event.getWindow().getScaledWidth(), event.getWindow().getScaledHeight());

    }

}
