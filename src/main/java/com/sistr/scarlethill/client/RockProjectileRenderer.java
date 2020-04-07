package com.sistr.scarlethill.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sistr.scarlethill.entity.projectile.RockProjectileEntity;
import com.sistr.scarlethill.setup.Registration;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class RockProjectileRenderer extends EntityRenderer<RockProjectileEntity> {

    public RockProjectileRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    public void render(RockProjectileEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        BlockState blockstate = Registration.SCARLET_MAGMA_BLOCK.get().getDefaultState();
        if (blockstate.getRenderType() == BlockRenderType.MODEL) {
            World world = entityIn.getEntityWorld();
            if (blockstate != world.getBlockState(new BlockPos(entityIn)) && blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                matrixStackIn.push();
                BlockPos blockpos = new BlockPos(entityIn.getPosX(), entityIn.getBoundingBox().maxY, entityIn.getPosZ());
                matrixStackIn.translate(-1.0D, 0.0D, -1.0D);
                matrixStackIn.scale(2, 2, 2);
                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
                for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.getBlockRenderTypes()) {
                    if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
                        net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
                        blockrendererdispatcher.getBlockModelRenderer()
                                .renderModel(world, blockrendererdispatcher.getModelForState(blockstate), blockstate, blockpos, matrixStackIn, bufferIn.getBuffer(type), false, new Random(), blockstate.getPositionRandom(entityIn.getPosition()), OverlayTexture.NO_OVERLAY, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
                    }
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
                matrixStackIn.pop();
                super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }
        }
    }

    public ResourceLocation getEntityTexture(RockProjectileEntity entity) {
        return PlayerContainer.LOCATION_BLOCKS_TEXTURE;
    }
}