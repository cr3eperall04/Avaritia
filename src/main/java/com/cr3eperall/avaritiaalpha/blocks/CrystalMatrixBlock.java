package com.cr3eperall.avaritiaalpha.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class CrystalMatrixBlock extends Block {
    public CrystalMatrixBlock() {
        super(Properties.create(Material.GLASS)
                .sound(SoundType.GLASS)
                .hardnessAndResistance(2.0f)
                .lightValue(5)
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("crystalmatrixblock");
    }
}
