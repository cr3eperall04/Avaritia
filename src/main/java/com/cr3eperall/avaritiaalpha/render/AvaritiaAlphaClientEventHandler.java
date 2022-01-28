package com.cr3eperall.avaritiaalpha.render;

import codechicken.lib.texture.TextureUtils;
import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.render.shader.CosmicShaderHelper;
import com.cr3eperall.avaritiaalpha.setup.AvaritiaAlphaTextures;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = AvaritiaAlpha.MOD_ID)
public class AvaritiaAlphaClientEventHandler {
    public static FloatBuffer cosmicUVs = BufferUtils.createFloatBuffer(4 * 10);


    public static void textureStichPre(TextureStitchEvent.Pre event) {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)){
            return;
        }
        System.out.println("---fcv-dfsfv-adfbv-zd");
        AvaritiaAlphaTextures.registerIcons(event);
    }

    @SubscribeEvent
    public static void textureStichPost(TextureStitchEvent.Post event) {
        /*TextureUtils.bindBlockTexture();
        InfinityArmorModel.itempagewidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        InfinityArmorModel.itempageheight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        InfinityArmorModel.armorModel.rebuildOverlay();
        InfinityArmorModel.legModel.rebuildOverlay();
         */
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        /*if (event.phase == TickEvent.Phase.START) {
            cosmicUVs = BufferUtils.createFloatBuffer(4 * AvaritiaAlphaTextures.COSMIC.length);
            TextureAtlasSprite icon;
            for (TextureAtlasSprite cosmicIcon : AvaritiaAlphaTextures.COSMIC) {
                icon = cosmicIcon;

                cosmicUVs.put(icon.getMinU());
                cosmicUVs.put(icon.getMinV());
                cosmicUVs.put(icon.getMaxU());
                cosmicUVs.put(icon.getMaxV());
            }
            cosmicUVs.flip();
        }*/
    }

    @SubscribeEvent
    public static void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        CosmicShaderHelper.inventoryRender = true;
    }

    @SubscribeEvent
    public static void drawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        CosmicShaderHelper.inventoryRender = false;
    }
}
