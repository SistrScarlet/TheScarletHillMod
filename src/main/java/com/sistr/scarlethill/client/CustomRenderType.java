package com.sistr.scarlethill.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public class CustomRenderType extends RenderType {

    public CustomRenderType(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {
        super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
    }

    public static RenderType getSwirl(ResourceLocation p_228636_0_, float p_228636_1_, float p_228636_2_) {
        return makeType("swirl", DefaultVertexFormats.ENTITY, 7, 256, false, true,
                RenderType.State.getBuilder().texture(new TextureState(p_228636_0_, false, false))
                        .texturing(new OffsetTexturingState(p_228636_1_, p_228636_2_))
                        .fog(BLACK_FOG).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
                        .alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(false));
    }

    private static final LineState THICK_LINES = new LineState(OptionalDouble.of(3));

    public static final RenderType OVERLAY_LINE_LOOP = makeType("overlay_line_loop",
            DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINE_LOOP, 256,
            RenderType.State.getBuilder().line(THICK_LINES)
                    .layer(PROJECTION_LAYERING)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .texture(NO_TEXTURE)
                    .depthTest(DEPTH_ALWAYS)
                    .cull(CULL_DISABLED)
                    .lightmap(LIGHTMAP_DISABLED)
                    .writeMask(COLOR_WRITE)
                    .build(false));

    public static final RenderType OVERLAY_LINES = makeType("overlay_lines",
            DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
            RenderType.State.getBuilder().line(THICK_LINES)
                    .layer(PROJECTION_LAYERING)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .texture(NO_TEXTURE)
                    .depthTest(DEPTH_ALWAYS)
                    .cull(CULL_DISABLED)
                    .lightmap(LIGHTMAP_DISABLED)
                    .writeMask(COLOR_WRITE)
                    .build(false));
}
