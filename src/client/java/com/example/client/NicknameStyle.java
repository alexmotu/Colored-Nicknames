package com.example.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class NicknameStyle {
    public boolean enabled = true;
    public String mode = "SOLID";
    public int color1 = 0xFFFFFF;
    public int color2 = 0xFF0000;
    public double speed = 0.5;
    public boolean bold = false;
    public boolean italic = false;
    public boolean underline = false;
    public String customName = "";

    public Component format(String originalName) {
        String displayName = (customName != null && !customName.trim().isEmpty()) ? customName : originalName;
        if (!enabled) {
            return Component.literal(displayName);
        }

        MutableComponent formatted = Component.empty();
        int length = displayName.length();

        if ("SOLID".equalsIgnoreCase(mode)) {
            formatted.append(Component.literal(displayName)
                .withStyle(style -> style
                    .withColor(TextColor.fromRgb(color1))
                    .withBold(bold)
                    .withItalic(italic)
                    .withUnderlined(underline)));
        } else if ("GRADIENT".equalsIgnoreCase(mode)) {
            for (int i = 0; i < length; i++) {
                float ratio = length > 1 ? (float) i / (length - 1) : 0f;
                int color = interpolateColor(color1, color2, ratio);
                formatted.append(Component.literal(String.valueOf(displayName.charAt(i)))
                    .withStyle(style -> style
                        .withColor(TextColor.fromRgb(color))
                        .withBold(bold)
                        .withItalic(italic)
                        .withUnderlined(underline)));
            }
        } else if ("RAINBOW".equalsIgnoreCase(mode)) {
            long time = System.currentTimeMillis() % 1000000L;
            for (int i = 0; i < length; i++) {
                float ratio = length > 1 ? (float) i / (length - 1) : 0f;
                float hue = ((time / 10.0f) * (float) speed + ratio * 120.0f) % 360.0f;
                int color = hsbToRgb(hue / 360.0f, 1.0f, 1.0f);
                formatted.append(Component.literal(String.valueOf(displayName.charAt(i)))
                    .withStyle(style -> style
                        .withColor(TextColor.fromRgb(color))
                        .withBold(bold)
                        .withItalic(italic)
                        .withUnderlined(underline)));
            }
        } else {
            formatted.append(Component.literal(displayName));
        }

        return formatted;
    }

    private static int interpolateColor(int c1, int c2, float ratio) {
        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;

        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }

    private static int hsbToRgb(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - saturation * (1.0f - f));
            switch ((int) h) {
                case 0 -> {
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                }
                case 1 -> {
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                }
                case 2 -> {
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                }
                case 3 -> {
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                }
                case 4 -> {
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                }
                case 5 -> {
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                }
            }
        }
        return (r << 16) | (g << 8) | b;
    }
}
