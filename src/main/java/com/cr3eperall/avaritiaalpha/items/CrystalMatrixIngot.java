package com.cr3eperall.avaritiaalpha.items;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import net.minecraft.item.Item;

public class CrystalMatrixIngot extends Item {
    public CrystalMatrixIngot() {
        super(new Item.Properties()
                .group(AvaritiaAlpha.setup.itemGroup)
        );
        setRegistryName("crystal_matrix_ingot");
    }
}
