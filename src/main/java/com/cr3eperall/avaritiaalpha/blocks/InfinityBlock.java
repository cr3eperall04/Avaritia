package com.cr3eperall.avaritiaalpha.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class InfinityBlock extends Block {
    public InfinityBlock() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.STONE)
                .hardnessAndResistance(-1f)
                .lightValue(5)
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("infinity_block");
    }
}