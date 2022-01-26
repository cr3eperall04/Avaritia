package com.cr3eperall.avaritiaalpha.setup;

import codechicken.lib.texture.TextureUtils;
import com.cr3eperall.avaritiaalpha.blocks.ModBlocks;
import com.cr3eperall.avaritiaalpha.blocks.gui.NeutronCollectorContainer;
import com.cr3eperall.avaritiaalpha.blocks.gui.NeutronCollectorScreen;
import com.cr3eperall.avaritiaalpha.entity.*;
import com.cr3eperall.avaritiaalpha.items.ModItems;
import com.cr3eperall.avaritiaalpha.render.entity.GapingVoidRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy implements IProxy{
    @Override
    public void init() {
        ScreenManager.registerFactory(ModBlocks.NEUTRONCOLLECTOR_CONTAINER, NeutronCollectorScreen::new);
        RenderingRegistry.registerEntityRenderingHandler(HeavenArrowEntity.class, HeavenArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(HeavenSubArrowEntity.class, HeavenArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GapingVoidEntity.class, GapingVoidRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EndestPearlEntity.class,manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }

    public void preinit(){
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

}
