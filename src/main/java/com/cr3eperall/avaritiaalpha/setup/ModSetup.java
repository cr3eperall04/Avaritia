package com.cr3eperall.avaritiaalpha.setup;

import com.cr3eperall.avaritiaalpha.blocks.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

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
