package com.cr3eperall.avaritiaalpha.items;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import net.minecraft.item.Item;

public class NeutroniumIngot extends Item {
    public NeutroniumIngot() {
        super(new Item.Properties()
                .group(AvaritiaAlpha.setup.itemGroup)
        );
        setRegistryName("neutronium_ingot");
    }
}
