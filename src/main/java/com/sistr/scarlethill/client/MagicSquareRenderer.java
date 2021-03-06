package com.sistr.scarlethill.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import static com.sistr.scarlethill.world.MagicSquareManager.MAGIC_SQUARES;

public class MagicSquareRenderer {

    public static void onWorldRender(RenderWorldLastEvent event) {
        render(event.getMatrixStack(), event.getPartialTicks());
    }

    public static void render(MatrixStack matrixStack, float partialTicks) {
        if (!MAGIC_SQUARES.isEmpty())
        MAGIC_SQUARES.forEach((uuid, magicSquare) -> {
            if (magicSquare.world != Minecraft.getInstance().world) {
                return;
            }

            matrixStack.push();

            Vec3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
            matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

            Matrix4f posMatrix = matrixStack.getLast().getMatrix();

            IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            {
                IVertexBuilder builder = buffer.getBuffer(CustomRenderType.OVERLAY_LINE_LOOP);

                circleRender(posMatrix, builder, magicSquare.center, magicSquare.color, magicSquare.radius);
                buffer.finish(CustomRenderType.OVERLAY_LINE_LOOP);
            }
            {
                IVertexBuilder builder = buffer.getBuffer(CustomRenderType.OVERLAY_LINES);
                for (int i = 0; i < magicSquare.vertexes.size(); i++) {
                    Vec3d point = magicSquare.vertexes.get(i);
                    for (int k = i + 1; k < magicSquare.vertexes.size(); k++) {
                        Vec3d toPoint = magicSquare.vertexes.get(k);
                        vertexBuilder(posMatrix, builder, point, magicSquare.color);
                        vertexBuilder(posMatrix, builder, toPoint, magicSquare.color);
                    }
                }
                buffer.finish(CustomRenderType.OVERLAY_LINES);
            }
            matrixStack.pop();
        });
    }

    public static void circleRender(Matrix4f posMatrix, IVertexBuilder builder,
                                    Vec3d center, Vec3d color, float radius) {
        for (int angle = 0; angle < 360; angle += 5) {
            float rad = (float) Math.PI / 180F;
            float x = MathHelper.sin(angle * rad) * radius;
            float z = MathHelper.cos(angle * rad) * radius;
            vertexBuilder(posMatrix, builder, center.add(x, 0, z), color);
        }
    }

    public static void vertexBuilder(Matrix4f posMatrix, IVertexBuilder builder,
                                     Vec3d pos, Vec3d color) {
        builder.pos(posMatrix, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ())
                .color((float) color.getX(), (float) color.getY(), (float) color.getZ(), 1)
                .endVertex();
    }

}
