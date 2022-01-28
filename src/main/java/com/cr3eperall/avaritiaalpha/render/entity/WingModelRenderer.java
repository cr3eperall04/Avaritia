/*package com.cr3eperall.avaritiaalpha.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import org.lwjgl.opengl.GL11;

public class WingModelRenderer extends RendererModel {

    public WingModelRenderer(Model model, String string) {
        super(model, string);
    }

    public WingModelRenderer(Model model) {
        super(model);
    }

    public WingModelRenderer(Model model, int x, int y) {
        super(model, x, y);
    }

    @Override
    public void render(float f) {
        if (!isHidden && showModel) {
            //GL11.glCullFace(GL11.GL_BACK);
            //GlStateManager.enableCull();
        }
        super.render(f);
        if (!isHidden && showModel) {
            //GL11.glCullFace(GL11.GL_NONE);
            //GlStateManager.disableCull();
        }
    }
}*/