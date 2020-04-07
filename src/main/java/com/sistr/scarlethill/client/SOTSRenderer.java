package com.sistr.scarlethill.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.entity.SOTSBodyEntity;
import com.sistr.scarlethill.entity.SOTSFistEntity;
import com.sistr.scarlethill.util.MathUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SOTSRenderer extends MobRenderer<SOTSBodyEntity, SOTSModel<SOTSBodyEntity>> {
    private static final ResourceLocation SOTS_BODY_TEXTURES = new ResourceLocation(ScarletHillMod.MODID, "textures/entity/spawn_of_the_scarlet_body.png");
    private static final ResourceLocation SOTS_BODY_CRACK_TEXTURES = new ResourceLocation(ScarletHillMod.MODID, "textures/entity/spawn_of_the_scarlet_body_crack.png");
    private static final ResourceLocation MAGMA_TEXTURE = new ResourceLocation("textures/block/lava_flow.png");

    public SOTSRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new SOTSModel<>(), 2.0F);
    }

    @Override
    public void render(SOTSBodyEntity molten, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        int alpha = (int) (MathHelper.lerp(partialTicks, molten.clientBodyRenderAlpha, molten.prevClientBodyRenderAlpha) / 20F * 192);

        if (0 < alpha) {
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-MathHelper.lerp(partialTicks, molten.prevRotationYawHead, molten.rotationYawHead)));

            matrixStackIn.scale(0.0625F, 0.0625F, 0.0625F);

            IVertexBuilder builder = bufferIn.getBuffer(CustomRenderType.getSwirl(MAGMA_TEXTURE, molten.ticksExisted * -0.005F, molten.ticksExisted * -0.005F));
            MatrixStack.Entry entry = matrixStackIn.getLast();
            Matrix4f posMatrix = entry.getMatrix();
            Matrix3f normalMatrix = entry.getNormal();

            //body
            int length = molten.floatHeight * 2 * 16;
            int topWidth = 20;
            int bottomWidth = 8;
            //手前
            this.pointBuilder(posMatrix, normalMatrix, builder, -bottomWidth, -length, bottomWidth, alpha, 0.0F, 0.0F);
            this.pointBuilder(posMatrix, normalMatrix, builder, bottomWidth, -length, bottomWidth, alpha, 0.5F, 0.0F);
            this.pointBuilder(posMatrix, normalMatrix, builder, topWidth, 0, topWidth, alpha, 0.5F, 0.15625F);
            this.pointBuilder(posMatrix, normalMatrix, builder, -topWidth, 0, topWidth, alpha, 0.0F, 0.15625F);
            //奥
            this.pointBuilder(posMatrix, normalMatrix, builder, bottomWidth, -length, -bottomWidth, alpha, 0.5F, 0.0F);
            this.pointBuilder(posMatrix, normalMatrix, builder, -bottomWidth, -length, -bottomWidth, alpha, 0.0F, 0.0F);
            this.pointBuilder(posMatrix, normalMatrix, builder, -topWidth, 0, -topWidth, alpha, 0.0F, 0.15625F);
            this.pointBuilder(posMatrix, normalMatrix, builder, topWidth, 0, -topWidth, alpha, 0.5F, 0.15625F);
            //右
            this.pointBuilder(posMatrix, normalMatrix, builder, bottomWidth, -length, bottomWidth, alpha, 0.0F, 0.0F);
            this.pointBuilder(posMatrix, normalMatrix, builder, bottomWidth, -length, -bottomWidth, alpha, 0.5F, 0.0F);
            this.pointBuilder(posMatrix, normalMatrix, builder, topWidth, 0, -topWidth, alpha, 0.5F, 0.15625F);
            this.pointBuilder(posMatrix, normalMatrix, builder, topWidth, 0, topWidth, alpha, 0.0F, 0.15625F);
            //左
            this.pointBuilder(posMatrix, normalMatrix, builder, -bottomWidth, -length, -bottomWidth, alpha, 0.5F, 0.0F);
            this.pointBuilder(posMatrix, normalMatrix, builder, -bottomWidth, -length, bottomWidth, alpha, 0.0F, 0.0F);
            this.pointBuilder(posMatrix, normalMatrix, builder, -topWidth, 0, topWidth, alpha, 0.0F, 0.15625F);
            this.pointBuilder(posMatrix, normalMatrix, builder, -topWidth, 0, -topWidth, alpha, 0.5F, 0.15625F);
            //蓋
            this.pointBuilder(posMatrix, normalMatrix, builder, -topWidth, 0, topWidth, alpha, 0.0F, 0.0F);
            this.pointBuilder(posMatrix, normalMatrix, builder, topWidth, 0, topWidth, alpha, 0.5F, 0.0F);
            this.pointBuilder(posMatrix, normalMatrix, builder, topWidth, 0, -topWidth, alpha, 0.5F, 0.15625F);
            this.pointBuilder(posMatrix, normalMatrix, builder, -topWidth, 0, -topWidth, alpha, 0.0F, 0.15625F);

            matrixStackIn.pop();

            molten.getHands().forEach(hand -> armRender(molten, hand, partialTicks, matrixStackIn, bufferIn, alpha));
        }
        super.render(molten, entityYaw, partialTicks, matrixStackIn, bufferIn, 240);
    }

    private void armRender(SOTSBodyEntity molten, SOTSFistEntity hand, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int alpha) {
        matrixStackIn.push();

        //おそらく16分の1ブロック単位
        matrixStackIn.scale(0.0625F, 0.0625F, 0.0625F);

        IVertexBuilder builder = bufferIn.getBuffer(CustomRenderType.getSwirl(MAGMA_TEXTURE, hand.ticksExisted * -0.005F, hand.ticksExisted * -0.005F));
        MatrixStack.Entry entry = matrixStackIn.getLast();
        Matrix4f posMatrix = entry.getMatrix();
        Matrix3f normalMatrix = entry.getNormal();

        Vec3d handPos = MathUtil.lerpVec(partialTicks, hand.getPositionVec(), new Vec3d(hand.prevPosX, hand.prevPosY, hand.prevPosZ));
        Vec3d parentPos = molten.getPositionVec();
        Vec3d handRelPos = handPos.subtract(parentPos).scale(16);
        int handX = (int) handRelPos.x;
        int handY = (int) handRelPos.y;
        int handZ = (int) handRelPos.z;
        Vec2f yawPitch = MathUtil.getYawPitch(handRelPos);
        float jointRot = hand.getHand() == SOTSFistEntity.FistSide.RIGHT ? -5 : 5;
        Vec3d jointRelPos = MathUtil.getVector(new Vec2f(yawPitch.x + jointRot, (yawPitch.y + 90) / 2)).scale(3).scale(16);
        int jointX = (int) jointRelPos.x;
        int jointY = (int) jointRelPos.y;
        int jointZ = (int) jointRelPos.z;
        int armWidth = 24;
        Vec3d rotate = MathUtil.getVector(new Vec2f(yawPitch.x + 90, yawPitch.y)).scale(armWidth);
        int offsetX = (int) rotate.x;
        int offsetZ = (int) rotate.z;
        int topPY = 0;
        int bottomPY = topPY - armWidth;
        int topJY = -24;
        int bottomJY = topJY - armWidth;
        int topHY = 32;
        int bottomHY = topHY - armWidth;
        //UVの求め方 U:横軸 V:縦軸 当てたいテクスチャの座標 / テクスチャの全体解像度 = UまたはVの値
        //上腕
        //上
        this.pointBuilder(posMatrix, normalMatrix, builder, -offsetX, +topPY, -offsetZ, alpha, 0.0F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, +offsetX, +topPY, +offsetZ, alpha, 1F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX + offsetX, jointY + topJY, jointZ + offsetZ, alpha, 1F, 0.15625F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX - offsetX, jointY + topJY, jointZ - offsetZ, alpha, 0.0F, 0.15625F);
        //下
        this.pointBuilder(posMatrix, normalMatrix, builder, +offsetX, +bottomPY, +offsetZ, alpha, 1F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, -offsetX, +bottomPY, -offsetZ, alpha, 0.0F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX - offsetX, jointY + bottomJY, jointZ - offsetZ, alpha, 0.0F, 0.15625F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX + offsetX, jointY + bottomJY, jointZ + offsetZ, alpha, 1F, 0.15625F);
        //右
        this.pointBuilder(posMatrix, normalMatrix, builder, -offsetX, +bottomPY, -offsetZ, alpha, 1F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, -offsetX, +topPY, -offsetZ, alpha, 0.0F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX - offsetX, jointY + topJY, jointZ - offsetZ, alpha, 0.0F, 0.15625F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX - offsetX, jointY + bottomJY, jointZ - offsetZ, alpha, 1F, 0.15625F);
        //左
        this.pointBuilder(posMatrix, normalMatrix, builder, +offsetX, +topPY, +offsetZ, alpha, 0.0F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, +offsetX, +bottomPY, +offsetZ, alpha, 1F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX + offsetX, jointY + bottomJY, jointZ + offsetZ, alpha, 1F, 0.15625F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX + offsetX, jointY + topJY, jointZ + offsetZ, alpha, 0.0F, 0.15625F);
        //前腕
        //上
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX - offsetX, jointY + topJY, jointZ - offsetZ, alpha, 0.0F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX + offsetX, jointY + topJY, jointZ + offsetZ, alpha, 1F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, handX + offsetX, handY + topHY, handZ + offsetZ, alpha, 1F, 0.15625F);
        this.pointBuilder(posMatrix, normalMatrix, builder, handX - offsetX, handY + topHY, handZ - offsetZ, alpha, 0.0F, 0.15625F);
        //下
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX + offsetX, jointY + bottomJY, jointZ + offsetZ, alpha, 1F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX - offsetX, jointY + bottomJY, jointZ - offsetZ, alpha, 0.0F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, handX - offsetX, handY + bottomHY, handZ - offsetZ, alpha, 0.0F, 0.15625F);
        this.pointBuilder(posMatrix, normalMatrix, builder, handX + offsetX, handY + bottomHY, handZ + offsetZ, alpha, 1F, 0.15625F);
        //右
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX - offsetX, jointY + bottomJY, jointZ - offsetZ, alpha, 1F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX - offsetX, jointY + topJY, jointZ - offsetZ, alpha, 0.0F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, handX - offsetX, handY + topHY, handZ - offsetZ, alpha, 0.0F, 0.15625F);
        this.pointBuilder(posMatrix, normalMatrix, builder, handX - offsetX, handY + bottomHY, handZ - offsetZ, alpha, 1F, 0.15625F);
        //左
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX + offsetX, jointY + topJY, jointZ + offsetZ, alpha, 0.0F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, jointX + offsetX, jointY + bottomJY, jointZ + offsetZ, alpha, 1F, 0.0F);
        this.pointBuilder(posMatrix, normalMatrix, builder, handX + offsetX, handY + bottomHY, handZ + offsetZ, alpha, 1F, 0.15625F);
        this.pointBuilder(posMatrix, normalMatrix, builder, handX + offsetX, handY + topHY, handZ + offsetZ, alpha, 0.0F, 0.15625F);

        matrixStackIn.pop();
    }

    public void pointBuilder(Matrix4f posMatrix, Matrix3f normalMatrix, IVertexBuilder builder, int x, int y, int z, int alpha, float u, float v) {
        builder.pos(posMatrix, x, y, z).color(255, 255, 255, alpha).tex(u, v)
                .overlay(0, 32).lightmap(240).normal(normalMatrix, 0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getEntityTexture(SOTSBodyEntity entity) {
        return entity.isLowHealth ? SOTS_BODY_CRACK_TEXTURES : SOTS_BODY_TEXTURES;
    }
}
