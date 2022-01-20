package com.cr3eperall.avaritiaalpha.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import static net.minecraft.util.text.TextFormatting.*;

public class TextUtils {

    private static final TextFormatting[] fabulousness = new TextFormatting[] { RED, GOLD, YELLOW, GREEN, AQUA, BLUE, LIGHT_PURPLE };

    public static ITextComponent makeFabulous(String input) {
        return ludicrousFormatting(input, fabulousness, 80.0, 1, 1);
    }

    private static final TextFormatting[] sanic = new TextFormatting[] { BLUE, BLUE, BLUE, BLUE, WHITE, BLUE, WHITE, WHITE, BLUE, WHITE, WHITE, BLUE, RED, WHITE, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY };

    public static ITextComponent makeSANIC(String input) {
        return ludicrousFormatting(input, sanic, 50.0, 2, 1);
    }

    public static ITextComponent ludicrousFormatting(String input, TextFormatting[] colours, double delay, int step, int posstep) {
        ITextComponent sb = new StringTextComponent("");
        if (delay <= 0) {
            delay = 0.001;
        }

        int offset = (int) Math.floor(Util.milliTime() / delay) % colours.length;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            int col = ((i * posstep) + colours.length - offset) % colours.length;
            sb.appendSibling(new StringTextComponent(c+"").applyTextStyle(colours[col]));
        }

        return sb;
    }
}
