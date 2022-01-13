package com.cr3eperall.avaritiaalpha.items;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import net.minecraft.item.Item;

public class NeutroniumNugget extends Item {
    public NeutroniumNugget() {
        super(new Item.Properties()
                .group(AvaritiaAlpha.setup.itemGroup)
        );
        setRegistryName("neutronium_nugget");
    }
}
