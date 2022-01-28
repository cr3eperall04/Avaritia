package com.cr3eperall.avaritiaalpha.render.entity;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import com.cr3eperall.avaritiaalpha.entity.GapingVoidEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;


public class GapingVoidRenderer extends EntityRenderer<GapingVoidEntity> {

    private ResourceLocation fill = new ResourceLocation("avaritiaalpha", "textures/entity/void.png");
    private ResourceLocation halo = new ResourceLocation("avaritiaalpha", "textures/entity/voidhalo.png");

    private CCModel model;
/*
    public Field renderPosX;
    public Field renderPosY;
    public Field renderPosZ;
 */


    public GapingVoidRenderer(EntityRendererManager manager) {
        super(manager);
        /*
        renderPosX=ObfuscationReflectionHelper.findField(EntityRendererManager.class,"field_78725_b");
        renderPosY=ObfuscationReflectionHelper.findField(EntityRendererManager.class,"field_78726_c");
        renderPosZ=ObfuscationReflectionHelper.findField(EntityRendererManager.class,"field_78723_d");
         */
        model = OBJParser.parseModels(new ResourceLocation("avaritiaalpha", "models/hemisphere.obj")).get("model");
    }

    @Override
    public void render(GapingVoidEntity entity, float entityYaw, float partialTicks, MatrixStack matrixIn, IRenderTypeBuffer bufferIn, int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        CCRenderState ccrs = CCRenderState.instance();
        TextureUtils.changeTexture(halo);

        double age = entity.getAge() + partialTicks;

        setColour(age, 1.0);

        double scale = GapingVoidEntity.getVoidScale(age);

        double fullfadedist = 0.6 * scale;
        double fadedist = fullfadedist + 1.5;

        double halocoord = 0.58 * scale;
        double haloscaledist = 2.2 * scale;

        double dx = entity.getPosX();
        double dy = entity.getPosY();
        double dz = entity.getPosZ();
        /*try {
            dx -= renderPosX.getDouble(renderManager);
            dy -= renderPosY.getDouble(renderManager);
            dz -= renderPosZ.getDouble(renderManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }*/

        double xzlen = Math.sqrt(dx * dx + dz * dz);
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (len <= haloscaledist) {
            double close = (haloscaledist - len) / haloscaledist;
            halocoord *= 1.0 + close * close * close * close * 1.5;
        }

        double yang = Math.atan2(xzlen, dy) * MathHelper.todeg;
        double xang = Math.atan2(dx, dz) * MathHelper.todeg;

        //Lumberjack.info("dx: "+dx+", dy: "+dy+", dz: "+dz+", xang: "+xang);
        //Lumberjack.info("x: "+x+", y: "+y+", z: "+z);

        GlStateManager.disableLighting();
        //mc.gameRenderer.disableLightmap();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(dx-Minecraft.getInstance().player.getPosX(), dy-Minecraft.getInstance().player.getPosY(), dz-Minecraft.getInstance().player.getPosZ());

            GlStateManager.rotatef((float) xang, 0, 1, 0);
            GlStateManager.rotatef((float) (yang + 90), 1, 0, 0);

            GlStateManager.pushMatrix();
            {
                GlStateManager.rotatef(90, 1, 0, 0);

                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                GlStateManager.depthMask(false);

                buffer.begin(0x07, DefaultVertexFormats.POSITION_TEX);
                buffer.pos(-halocoord, 0.0, -halocoord).tex(0.0F, 0.0F).endVertex();
                buffer.pos(-halocoord, 0.0, halocoord).tex(0.0F, 1.0F).endVertex();
                buffer.pos(halocoord, 0.0, halocoord).tex(1.0F, 1.0F).endVertex();
                buffer.pos(halocoord, 0.0, -halocoord).tex(1.0F, 0.0F).endVertex();
                tess.draw();

                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.enableAlphaTest();
            }
            GlStateManager.popMatrix();

            TextureUtils.changeTexture(fill);

            GlStateManager.scaled(scale, scale, scale);

            GlStateManager.disableCull();
            ccrs.startDrawing(0x07, DefaultVertexFormats.POSITION_TEX);
            model.render(ccrs);
            ccrs.draw();
            GlStateManager.enableCull();

        }
        GlStateManager.popMatrix();

        if (len <= fadedist) {
            double alpha = 1.0;
            if (len >= fullfadedist) {
                alpha = 1.0 - ((len - fullfadedist) / (fadedist - fullfadedist));
                alpha = alpha * alpha * (3 - 2 * alpha);
            }
            setColour(age, alpha);
            GlStateManager.pushMatrix();
            {
                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();

                //GlStateManager.rotatef((float) (180.0F - dy), 0.0F, 1.0F, 0.0F);
                //GlStateManager.rotatef((float) -dx, 1.0F, 0.0F, 0.0F);

                double d = 0;

                buffer.begin(0x07, DefaultVertexFormats.POSITION_TEX);
                buffer.pos(-100, 100, d).tex(0.0F, 0.0F).endVertex();
                buffer.pos(-100, -100, d).tex(0.0F, 1.0F).endVertex();
                buffer.pos(100, -100, d).tex(1.0F, 1.0F).endVertex();
                buffer.pos(100, 100, d).tex(1.0F, 0.0F).endVertex();
                tess.draw();

                GlStateManager.disableBlend();
                GlStateManager.enableAlphaTest();
            }
            GlStateManager.popMatrix();
        }

        //mc.gameRenderer.enableLightmap();
        GlStateManager.enableLighting();

        GlStateManager.color4f(1, 1, 1, 1);
    }

    @Override
    public ResourceLocation getEntityTexture(GapingVoidEntity ent) {
        return fill;
    }

    private void setColour(double age, double alpha) {
        double life = (age / GapingVoidEntity.maxLifetime);
        double f = Math.max(0, (life - GapingVoidEntity.collapse) / (1 - GapingVoidEntity.collapse));
        f = Math.max(f, 1 - (life * 30));
        GlStateManager.color4f((float) f, (float) f, (float) f, (float) alpha);
    }
}
