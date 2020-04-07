package com.sistr.scarlethill.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.sistr.scarlethill.entity.CrimsonianEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CrimsonianModel<T extends CrimsonianEntity> extends PlayerModel<T> {

    public CrimsonianModel(float modelSize) {
        super(modelSize, false);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.isChild = true;
        super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.isSitting = entityIn.isWait();
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.bipedBody, this.bipedRightArm, this.bipedLeftArm, this.bipedRightLeg, this.bipedLeftLeg, this.bipedLeftLegwear, this.bipedRightLegwear, this.bipedLeftArmwear, this.bipedRightArmwear, this.bipedBodyWear);
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of(this.bipedHead, this.bipedHeadwear);
    }
}
