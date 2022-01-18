package com.cr3eperall.avaritiaalpha.setup;

import com.cr3eperall.avaritiaalpha.blocks.ModBlocks;
import com.cr3eperall.avaritiaalpha.blocks.gui.NeutronCollectorContainer;
import com.cr3eperall.avaritiaalpha.blocks.gui.NeutronCollectorScreen;
import com.cr3eperall.avaritiaalpha.entity.HeavenArrowEntity;
import com.cr3eperall.avaritiaalpha.entity.HeavenArrowRenderer;
import com.cr3eperall.avaritiaalpha.entity.HeavenSubArrowEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy implements IProxy{
    @Override
    public void init() {
        ScreenManager.registerFactory(ModBlocks.NEUTRONCOLLECTOR_CONTAINER, NeutronCollectorScreen::new);
        RenderingRegistry.registerEntityRenderingHandler(HeavenArrowEntity.class, HeavenArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(HeavenSubArrowEntity.class, HeavenArrowRenderer::new);
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
