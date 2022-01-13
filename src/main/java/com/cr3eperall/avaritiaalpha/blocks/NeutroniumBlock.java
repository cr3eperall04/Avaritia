package com.cr3eperall.avaritiaalpha.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class NeutroniumBlock extends Block {
    public NeutroniumBlock() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.STONE)
                .hardnessAndResistance(2.0f)
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("neutronium_block");
    }
}
