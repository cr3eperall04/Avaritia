package com.cr3eperall.avaritiaalpha.items.tools;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SkullfireSword extends SwordItem {
    public SkullfireSword() {
        super(ItemTier.DIAMOND, 6, -2, new Properties().group(AvaritiaAlpha.setup.itemGroup).rarity(Rarity.EPIC));
        setRegistryName("skullfire_sword");
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        tooltip.add(new StringTextComponent(I18n.format("tooltip.skullfire_sword.desc")).applyTextStyles(TextFormatting.DARK_GRAY,TextFormatting.ITALIC));
    }
}
