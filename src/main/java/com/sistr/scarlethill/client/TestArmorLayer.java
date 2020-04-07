package com.sistr.scarlethill.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.setup.Registration;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TestArmorLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    private static final ResourceLocation TEXTURE_TEST = new ResourceLocation(ScarletHillMod.MODID, "textures/entity/test.png");
    private final TestModel<T> modelTest = new TestModel<>();

    public TestArmorLayer(IEntityRenderer<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.HEAD);
        if (itemstack.getItem() == Registration.TEST_ARMOR_ITEM.get()) {
            ResourceLocation resourcelocation;

            resourcelocation = TEXTURE_TEST;

            matrixStackIn.push();
            this.getEntityModel().copyModelAttributesTo(this.modelTest);
            this.modelTest.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, this.modelTest.getRenderType(resourcelocation), false, itemstack.hasEffect());
            this.modelTest.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
        }
    }
}
