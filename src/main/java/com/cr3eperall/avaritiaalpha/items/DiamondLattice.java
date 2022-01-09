package com.cr3eperall.avaritiaalpha.items;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import net.minecraft.item.Item;

public class DiamondLattice extends Item {
    public DiamondLattice() {
        super(new Item.Properties()
                .group(AvaritiaAlpha.setup.itemGroup)
        );
        setRegistryName("diamond_lattice");
    }
}
