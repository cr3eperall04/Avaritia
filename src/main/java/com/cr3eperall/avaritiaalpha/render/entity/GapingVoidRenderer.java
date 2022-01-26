package com.cr3eperall.avaritiaalpha.render.entity;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import com.cr3eperall.avaritiaalpha.entity.GapingVoidEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;


public class GapingVoidRenderer extends EntityRenderer<GapingVoidEntity> {

    private ResourceLocation fill = new ResourceLocation("avaritiaalpha", "textures/entity/void.png");
    private ResourceLocation halo = new ResourceLocation("avaritiaalpha", "textures/entity/voidhalo.png");

    private CCModel model;

    public GapingVoidRenderer(EntityRendererManager manager) {
        super(manager);
        model = OBJParser.parseModels(new ResourceLocation("avaritiaalpha", "models/hemisphere.obj")).get("model");
    }

    @Override
    public void doRender(GapingVoidEntity ent, double x, double y, double z, float entityYaw, float partialTicks) {

        Minecraft mc = Minecraft.getInstance();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        CCRenderState ccrs = CCRenderState.instance();
        TextureUtils.changeTexture(halo);

        double age = ent.getAge() + partialTicks;

        setColour(age, 1.0);

        double scale = GapingVoidEntity.getVoidScale(age);

        double fullfadedist = 0.6 * scale;
        double fadedist = fullfadedist + 1.5;

        double halocoord = 0.58 * scale;
        double haloscaledist = 2.2 * scale;

        double dx = ent.posX - renderManager.renderPosX;//TODO use Reflection instead of access transformer
        double dy = ent.posY - renderManager.renderPosY;
        double dz = ent.posZ - renderManager.renderPosZ;


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
        mc.gameRenderer.disableLightmap();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(x, y, z);

            GlStateManager.rotatef((float) xang, 0, 1, 0);
            GlStateManager.rotatef((float) (yang + 90), 1, 0, 0);

            GlStateManager.pushMatrix();
            {
                GlStateManager.rotatef(90, 1, 0, 0);

                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                GlStateManager.depthMask(false);

                buffer.begin(0x07, DefaultVertexFormats.POSITION_TEX);
                buffer.pos(-halocoord, 0.0, -halocoord).tex(0.0, 0.0).endVertex();
                buffer.pos(-halocoord, 0.0, halocoord).tex(0.0, 1.0).endVertex();
                buffer.pos(halocoord, 0.0, halocoord).tex(1.0, 1.0).endVertex();
                buffer.pos(halocoord, 0.0, -halocoord).tex(1.0, 0.0).endVertex();
                tess.draw();

                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.enableAlphaTest();
            }
            GlStateManager.popMatrix();

            TextureUtils.changeTexture(fill);

            GlStateManager.scaled(scale, scale, scale);

            GlStateManager.disableCull();
            ccrs.startDrawing(0x07, DefaultVertexFormats.POSITION_TEX_NORMAL);
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

                GlStateManager.rotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

                double d = 0;

                buffer.begin(0x07, DefaultVertexFormats.POSITION_TEX);
                buffer.pos(-100, 100, d).tex(0.0, 0.0).endVertex();
                buffer.pos(-100, -100, d).tex(0.0, 1.0).endVertex();
                buffer.pos(100, -100, d).tex(1.0, 1.0).endVertex();
                buffer.pos(100, 100, d).tex(1.0, 0.0).endVertex();
                tess.draw();

                GlStateManager.disableBlend();
                GlStateManager.enableAlphaTest();
            }
            GlStateManager.popMatrix();
        }

        mc.gameRenderer.enableLightmap();
        GlStateManager.enableLighting();

        GlStateManager.color4f(1, 1, 1, 1);
    }

    @Override
    protected ResourceLocation getEntityTexture(GapingVoidEntity ent) {
        return fill;
    }

    private void setColour(double age, double alpha) {
        double life = (age / GapingVoidEntity.maxLifetime);
        double f = Math.max(0, (life - GapingVoidEntity.collapse) / (1 - GapingVoidEntity.collapse));
        f = Math.max(f, 1 - (life * 30));
        GlStateManager.color4f((float) f, (float) f, (float) f, (float) alpha);
    }
}
