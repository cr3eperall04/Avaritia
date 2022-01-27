package com.cr3eperall.avaritiaalpha.items;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RecordFragment extends Item {
    public RecordFragment() {
        super(new Item.Properties().group(AvaritiaAlpha.setup.itemGroup));
        setRegistryName("record_fragment");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(I18n.format("tooltip.item.record_fragment.desc")).applyTextStyles(TextFormatting.DARK_GRAY,TextFormatting.ITALIC));
    }
}
