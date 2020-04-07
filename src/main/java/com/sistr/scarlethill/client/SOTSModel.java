package com.sistr.scarlethill.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SOTSModel<T extends Entity> extends SegmentedModel<T> {
    private final ModelRenderer head;

    public SOTSModel() {
        this(0.0F);
    }

    public SOTSModel(float scale) {
        this.head = new ModelRenderer(this, 0, 0).setTextureSize(32, 32);
        this.head.addBox(-16.0F, -8, -16.0F, 32.0F, 32.0F, 32.0F, scale);
        this.head.setRotationPoint(0.0F, 0, 0.0F);
    }

    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.head);
    }

    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;
    }


}