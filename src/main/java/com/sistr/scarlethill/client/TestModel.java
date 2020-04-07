package com.sistr.scarlethill.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * ModelPlayer - Either Mojang or a mod author
 * Created using Tabula 7.0.1
 */
@OnlyIn(Dist.CLIENT)
public class TestModel<T extends LivingEntity> extends AgeableModel<T> {
    public ModelRenderer neck;
    public ModelRenderer muffler;

    public TestModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.neck = new ModelRenderer(this, 0, 0);
        this.neck.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.neck.addBox(-5.0F, -2.0F, -5.0F, 10, 3, 10, 0.0F);
        this.muffler = new ModelRenderer(this, 48, 0);
        this.muffler.setRotationPoint(0.0F, 0.0F, 5.0F);
        this.muffler.addBox(-2.5F, 0.0F, 0.0F, 5, 20, 1, 0.0F);
    }

    //前回地点と現在地点からベクトルを作る、そのベクトルと今向いている方角とのなす角を求める、そこから内積を求める
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.neck.rotateAngleY = netHeadYaw * 0.017453292F;
        this.muffler.rotateAngleY = this.neck.rotateAngleY;
        float move = 0;
        Vec3d moveVec = new Vec3d(entityIn.prevPosX - entityIn.getPosX(), entityIn.prevPosY - entityIn.getPosY(), entityIn.prevPosZ - entityIn.getPosZ());
        if (!moveVec.equals(Vec3d.ZERO)) {
            Vec3d lookVec = entityIn.getLookVec().mul(1, 0, 1).add(0, -0.5, 0);
            double moveVecLength = moveVec.length();
            double cosTheta = moveVec.dotProduct(lookVec) / moveVecLength * lookVec.length();
            move = (float) (-cosTheta * moveVecLength);
        }

        float ticks = MathHelper.lerp(ageInTicks, entityIn.ticksExisted - 1, entityIn.ticksExisted);
        float straight = (ticks % 80) / 80F;
        if (0.5F <= straight) {
            straight = 1 - straight;
        }
        straight *= 2;
        this.muffler.rotateAngleX = (MathHelper.cos(straight)) * 0.25F + Math.max(Math.min(move * 1.4F, 1.4F), 0);
    }

    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of(this.neck, this.muffler);
    }

    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of();
    }
}
