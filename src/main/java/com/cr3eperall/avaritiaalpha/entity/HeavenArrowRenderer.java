package com.cr3eperall.avaritiaalpha.entity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class HeavenArrowRenderer extends ArrowRenderer<AbstractArrowEntity> {

    private static final ResourceLocation tex = new ResourceLocation("avaritiaalpha", "textures/entity/heavenarrow.png");

    public HeavenArrowRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(AbstractArrowEntity entity) {
        return tex;
    }
}
