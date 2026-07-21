package com.example.client;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ColorSlider extends AbstractSliderButton {
    private final String labelPrefix;
    private final Consumer<Double> valueConsumer;
    private final boolean isRainbowSpeed;

    public ColorSlider(int x, int y, int width, int height, String labelPrefix, double initialValue, boolean isRainbowSpeed, Consumer<Double> valueConsumer) {
        super(x, y, width, height, Component.literal(labelPrefix + ": " + (int) (initialValue * 255.0)), initialValue);
        this.labelPrefix = labelPrefix;
        this.valueConsumer = valueConsumer;
        this.isRainbowSpeed = isRainbowSpeed;
        if (isRainbowSpeed) {
            updateMessage();
        }
    }

    @Override
    protected void updateMessage() {
        if (isRainbowSpeed) {
            double displaySpeed = 0.1 + this.value * 2.9;
            this.setMessage(Component.literal(String.format("%s: %.1fx", labelPrefix, displaySpeed)));
        } else {
            this.setMessage(Component.literal(labelPrefix + ": " + (int) (this.value * 255.0)));
        }
    }

    @Override
    protected void applyValue() {
        if (valueConsumer != null) {
            valueConsumer.accept(this.value);
        }
    }

    public void setValue(double value) {
        this.value = Math.max(0.0, Math.min(1.0, value));
        this.updateMessage();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;

        int trackY = this.getY() + this.height / 2 - 2;
        int trackHeight = 4;
        int trackColor = 0x80151419;
        NicknameScreen.fillRoundedRect(guiGraphics, this.getX(), trackY, this.width, trackHeight, 2, trackColor);

        int thumbWidth = 8;
        int thumbHeight = 10;
        int thumbX = this.getX() + (int) (this.value * (this.width - thumbWidth));
        int thumbY = this.getY() + (this.height - thumbHeight) / 2;
        int thumbColor = 0xFFE0523C;
        
        NicknameScreen.fillRoundedRect(guiGraphics, thumbX, thumbY, thumbWidth, thumbHeight, 2, thumbColor);
        if (this.isHoveredOrFocused()) {
            NicknameScreen.drawRoundedOutline(guiGraphics, thumbX - 1, thumbY - 1, thumbWidth + 2, thumbHeight + 2, 2, 1, 0xFFFFFFFF);
        }

        int textColor = this.isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFECEAEF;
        GuiGraphicsCompat.drawString(
            guiGraphics,
            net.minecraft.client.Minecraft.getInstance().font,
            this.getMessage(),
            this.getX(),
            this.getY() - 8,
            textColor,
            false
        );
    }
}
