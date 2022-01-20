package com.cr3eperall.avaritiaalpha.setup;

import com.cr3eperall.avaritiaalpha.blocks.ModBlocks;
import com.cr3eperall.avaritiaalpha.handler.AvaritiaAlphaEventHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ModSetup {

    public ItemGroup itemGroup =new ItemGroup("avaritiaalpha") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.CRYSTALMATRIXBLOCK);
        }
    };

    public void init(){

    }
}
