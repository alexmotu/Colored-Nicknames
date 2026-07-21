package com.example.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Method;

public class GuiGraphicsCompat {
    private static Method drawStringMethod;
    private static Method drawCenteredStringMethod;

    static {
        try {
            for (Method m : GuiGraphics.class.getMethods()) {
                if (m.getName().equals("drawString") || m.getName().equals("method_51439")) {
                    Class<?>[] params = m.getParameterTypes();
                    if (params.length == 6 &&
                        params[0] == Font.class &&
                        params[1] == Component.class &&
                        params[2] == int.class &&
                        params[3] == int.class &&
                        params[4] == int.class &&
                        params[5] == boolean.class) {
                        drawStringMethod = m;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (Method m : GuiGraphics.class.getMethods()) {
                if (m.getName().equals("drawCenteredString") || m.getName().equals("method_27534") || m.getName().equals("method_51440")) {
                    Class<?>[] params = m.getParameterTypes();
                    if (params.length == 5 &&
                        params[0] == Font.class &&
                        params[1] == Component.class &&
                        params[2] == int.class &&
                        params[3] == int.class &&
                        params[4] == int.class) {
                        drawCenteredStringMethod = m;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawString(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean shadow) {
        if (drawStringMethod != null) {
            try {
                drawStringMethod.invoke(graphics, font, text, x, y, color, shadow);
                return;
            } catch (Exception e) {
            }
        }
        //? if >=26.1 {
        /*graphics.text(font, text, x, y, color, shadow);
        *///?} else {
        graphics.drawString(font, text, x, y, color, shadow);
        //?}
    }

    public static void drawString(GuiGraphics graphics, Font font, String text, int x, int y, int color, boolean shadow) {
        drawString(graphics, font, Component.literal(text), x, y, color, shadow);
    }

    public static void drawCenteredString(GuiGraphics graphics, Font font, Component text, int x, int y, int color) {
        if (drawCenteredStringMethod != null) {
            try {
                drawCenteredStringMethod.invoke(graphics, font, text, x, y, color);
                return;
            } catch (Exception e) {
            }
        }
        //? if >=26.1 {
        /*graphics.centeredText(font, text, x, y, color);
        *///?} else {
        graphics.drawCenteredString(font, text, x, y, color);
        //?}
    }

    public static void drawCenteredString(GuiGraphics graphics, Font font, String text, int x, int y, int color) {
        drawCenteredString(graphics, font, Component.literal(text), x, y, color);
    }
}
