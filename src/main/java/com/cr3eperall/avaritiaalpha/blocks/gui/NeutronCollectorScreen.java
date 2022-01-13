package com.cr3eperall.avaritiaalpha.blocks.gui;

import codechicken.lib.math.MathHelper;
import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.config.Config;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class NeutronCollectorScreen extends ContainerScreen<NeutronCollectorContainer> {

    private ResourceLocation GUI= new ResourceLocation(AvaritiaAlpha.MOD_ID,"textures/gui/neutron_collector_gui.png");

    public NeutronCollectorScreen(NeutronCollectorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(I18n.format(this.title.getFormattedText()), 8.0F,6.0F,0x404040);
        float progress=getContainer().getProgress()/ (float)Config.NEUTRONCOLLECTOR_RATE.get()*100;
        String str_progress="Progress: " + MathHelper.round(progress, 10) + "%";
        this.font.drawString(str_progress,xSize / 2F - font.getStringWidth(str_progress) / 2F, 60, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX=(this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
    }
}
