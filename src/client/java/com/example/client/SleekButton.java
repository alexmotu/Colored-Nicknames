package com.example.client;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.sounds.SoundEvents;

import java.util.function.Consumer;

public class SleekButton extends AbstractWidget {
    private final Consumer<SleekButton> onPress;
    private boolean toggled = false;

    public SleekButton(int x, int y, int width, int height, Component message, Consumer<SleekButton> onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    
    public void pressButton() {
        if (this.onPress != null) {
            this.onPress.accept(this);
        }
    }

    //? if <1.21.9 {
    @Override
    public void onClick(double mouseX, double mouseY) {
        pressButton();
    }
    //?} else {
    /*@Override
    public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        pressButton();
    }
    *///?}

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;

        int bgColor;
        int borderColor;
        int textColor;

        boolean hovered = this.isHoveredOrFocused();

        if (this.active) {
            if (this.toggled) {
                bgColor = hovered ? 0xFFFF6A4E : 0xFFE0523C;
                borderColor = hovered ? 0xFFFFFFFF : 0xFFE0523C;
                textColor = 0xFFFFFFFF;
            } else {
                bgColor = hovered ? 0x40ECEAEF : 0x2D1E1B24;
                borderColor = hovered ? 0xFFECEAEF : 0xFF3F3F3F;
                textColor = hovered ? 0xFFFFFFFF : 0xFFECEAEF;
            }
        } else {
            bgColor = 0x10ECEAEF;
            borderColor = 0x30ECEAEF;
            textColor = 0xFF888888;
        }

        NicknameScreen.fillRoundedRect(guiGraphics, this.getX(), this.getY(), this.width, this.height, 4, bgColor);
        NicknameScreen.drawRoundedOutline(guiGraphics, this.getX(), this.getY(), this.width, this.height, 4, 1, borderColor);

        GuiGraphicsCompat.drawCenteredString(
            guiGraphics,
            net.minecraft.client.Minecraft.getInstance().font,
            this.getMessage(),
            this.getX() + this.width / 2,
            this.getY() + (this.height - 8) / 2,
            textColor
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
