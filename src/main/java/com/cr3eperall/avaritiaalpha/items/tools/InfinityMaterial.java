package com.cr3eperall.avaritiaalpha.items.tools;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.TextFormatting;

public class InfinityMaterial implements IItemTier {

    public static final Rarity COSMIC_RARITY=Rarity.create("COSMIC", TextFormatting.RED);

    private static final InfinityMaterial material =new InfinityMaterial();

    @Override
    public int getMaxUses() {
        return 999999;
    }

    @Override
    public float getEfficiency() {
        return 9999F;
    }

    @Override
    public float getAttackDamage() {
        return 20F;
    }

    @Override
    public int getHarvestLevel() {
        return 32;
    }

    @Override
    public int getEnchantability() {
        return 200;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return null;
    }

    public static InfinityMaterial get(){
        return material;
    }

}
