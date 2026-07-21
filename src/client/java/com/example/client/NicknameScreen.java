package com.example.client;

import com.example.client.ModConfig.GroupStyle;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.List;

public class NicknameScreen extends Screen {
    private static NicknameScreen openScreen;

    private int selectedGroupIndex = 0;
    private int groupScrollIndex = 0;
    private int playerScrollIndex = 0;
    private int pickingColorIndex = 1;

    private EditBox newGroupNameField;
    private EditBox groupRenameField;
    private EditBox addPlayerNameField;
    private EditBox hexColorField;
    private SleekButton addGroupBtn;
    private SleekButton addPlayerBtn;
    private SleekButton deleteBtn;
    private SleekButton closeBtn;
    private SleekButton enabledBtn;
    private SleekButton boldBtn;
    private SleekButton italicBtn;
    private SleekButton underlineBtn;
    private SleekButton modeBtn;
    private SleekButton hideNonGroupNicksToggleBtn;
    private SleekButton prioritizeServerPlayersInTabBtn;
    private SleekButton onlyGroupsInLocatorBtn;
    private SleekButton keepTeamPrefixesBtn;

    private static final int[] PRESET_COLORS = {
        0xFF5555, 0xFFAA00, 0xFFFF55, 0x55FF55, 0x55FFFF, 0x5555FF, 0xFF55FF
    };

    public NicknameScreen() {
        super(Component.translatable("gui.nickgroups.title"));
        openScreen = this;
    }

    public static void rebuildOpenScreen() {
        if (openScreen != null) {
            openScreen.rebuildWidgets();
        }
    }

    @Override
    public void removed() {
        if (openScreen == this) {
            openScreen = null;
        }
        super.removed();
    }

    @Override
    protected void init() {
        ensureSelectedGroup();

        int w = 380;
        int h = 245;
        int left = (this.width - w) / 2;
        int top = (this.height - h) / 2;

        newGroupNameField = new EditBox(this.font, left + 14, top + 195, 66, 16, Component.empty());
        newGroupNameField.setBordered(false);
        newGroupNameField.setHint(Component.translatable("gui.nickgroups.group_placeholder"));

        addGroupBtn = new SleekButton(left + 84, top + 195, 16, 16, Component.literal("+"), btn -> {
            String name = newGroupNameField.getValue().trim();
            if (!name.isEmpty()) {
                GroupStyle group = new GroupStyle();
                group.name = name;
                NicknameManager.getConfig().groups.add(group);
                selectedGroupIndex = NicknameManager.getConfig().groups.size();
                NicknameManager.save();
                this.rebuildWidgets();
            }
        });

        groupRenameField = new EditBox(this.font, left + 129, top + 10, 232, 16, Component.empty());
        groupRenameField.setBordered(false);
        groupRenameField.setResponder(text -> {
            GroupStyle group = getSelectedGroup();
            if (group != null && !text.trim().isEmpty()) {
                group.name = text.trim();
                NicknameManager.save();
            }
        });

        enabledBtn = new SleekButton(left + 125, top + 30, 50, 16, Component.translatable("gui.nickgroups.enabled_toggle"), btn -> {
            NicknameStyle style = getActiveStyle();
            if (style != null) {
                style.enabled = !style.enabled;
                enabledBtn.setToggled(style.enabled);
                NicknameManager.save();
            }
        });

        boldBtn = new SleekButton(left + 185, top + 30, 20, 16, Component.literal("B"), btn -> toggleStyleFlag("bold"));
        italicBtn = new SleekButton(left + 210, top + 30, 20, 16, Component.literal("I"), btn -> toggleStyleFlag("italic"));
        underlineBtn = new SleekButton(left + 235, top + 30, 20, 16, Component.literal("U"), btn -> toggleStyleFlag("underline"));

        modeBtn = new SleekButton(left + 265, top + 30, 100, 16, Component.empty(), btn -> {
            NicknameStyle style = getActiveStyle();
            if (style != null) {
                if ("SOLID".equalsIgnoreCase(style.mode)) {
                    style.mode = "GRADIENT";
                } else if ("GRADIENT".equalsIgnoreCase(style.mode)) {
                    style.mode = "RAINBOW";
                } else {
                    style.mode = "SOLID";
                }
                updateModeBtnLabel();
                NicknameManager.save();
            }
        });

        addPlayerNameField = new EditBox(this.font, left + 129, top + 50, 207, 16, Component.empty());
        addPlayerNameField.setBordered(false);
        addPlayerNameField.setMaxLength(16);
        addPlayerNameField.setHint(Component.translatable("gui.nickgroups.friend_placeholder"));

        addPlayerBtn = new SleekButton(left + 347, top + 50, 18, 16, Component.literal("+"), btn -> {
            GroupStyle group = getSelectedGroup();
            String name = addPlayerNameField.getValue().trim();
            if (group != null && !name.isEmpty() && !group.players.contains(name)) {
                group.players.add(name);
                addPlayerNameField.setValue("");
                playerScrollIndex = Math.max(0, group.players.size() - 4);
                NicknameManager.save();
            }
        });

        hexColorField = new EditBox(this.font, left + 314, top + 127, 48, 12, Component.empty());
        hexColorField.setBordered(false);
        hexColorField.setMaxLength(7);
        hexColorField.setResponder(this::applyHexColor);

        hideNonGroupNicksToggleBtn = new SleekButton(left + 125, top + 35, 115, 16, Component.translatable("gui.nickgroups.hide_non_group_toggle"), btn -> {
            ModConfig cfg = NicknameManager.getConfig();
            cfg.hideNonGroupNicks = !cfg.hideNonGroupNicks;
            hideNonGroupNicksToggleBtn.setToggled(cfg.hideNonGroupNicks);
            NicknameManager.save();
        });

        onlyGroupsInLocatorBtn = new SleekButton(left + 245, top + 35, 115, 16, Component.translatable("gui.nickgroups.only_groups_locator_toggle"), btn -> {
            ModConfig cfg = NicknameManager.getConfig();
            cfg.onlyGroupsInLocator = !cfg.onlyGroupsInLocator;
            onlyGroupsInLocatorBtn.setToggled(cfg.onlyGroupsInLocator);
            NicknameManager.save();
        });

        keepTeamPrefixesBtn = new SleekButton(left + 125, top + 55, 115, 16, Component.translatable("gui.nickgroups.keep_prefixes_toggle"), btn -> {
            ModConfig cfg = NicknameManager.getConfig();
            cfg.keepTeamPrefixes = !cfg.keepTeamPrefixes;
            keepTeamPrefixesBtn.setToggled(cfg.keepTeamPrefixes);
            NicknameManager.save();
        });

        prioritizeServerPlayersInTabBtn = new SleekButton(left + 245, top + 55, 115, 16, Component.translatable("gui.nickgroups.prioritize_tab_toggle"), btn -> {
            ModConfig cfg = NicknameManager.getConfig();
            cfg.prioritizeServerPlayersInTab = !cfg.prioritizeServerPlayersInTab;
            prioritizeServerPlayersInTabBtn.setToggled(cfg.prioritizeServerPlayersInTab);
            NicknameManager.save();
        });

        deleteBtn = new SleekButton(left + 125, top + 224, 115, 16, Component.translatable("gui.nickgroups.btn.delete_group"), btn -> {
            List<GroupStyle> groups = NicknameManager.getConfig().groups;
            if (selectedGroupIndex > 0 && selectedGroupIndex <= groups.size()) {
                groups.remove(selectedGroupIndex - 1);
                selectedGroupIndex = Math.max(0, Math.min(selectedGroupIndex, groups.size()));
                NicknameManager.save();
                this.rebuildWidgets();
            }
        });

        closeBtn = new SleekButton(left + 250, top + 224, 115, 16, Component.translatable("gui.nickgroups.btn.close"), btn -> {
            NicknameManager.save();
            this.onClose();
        });

        this.addRenderableWidget(newGroupNameField);
        this.addRenderableWidget(addGroupBtn);
        this.addRenderableWidget(groupRenameField);
        this.addRenderableWidget(enabledBtn);
        this.addRenderableWidget(boldBtn);
        this.addRenderableWidget(italicBtn);
        this.addRenderableWidget(underlineBtn);
        this.addRenderableWidget(modeBtn);
        this.addRenderableWidget(addPlayerNameField);
        this.addRenderableWidget(addPlayerBtn);
        this.addRenderableWidget(hexColorField);
        this.addRenderableWidget(hideNonGroupNicksToggleBtn);
        this.addRenderableWidget(onlyGroupsInLocatorBtn);
        this.addRenderableWidget(keepTeamPrefixesBtn);
        this.addRenderableWidget(prioritizeServerPlayersInTabBtn);
        this.addRenderableWidget(deleteBtn);
        this.addRenderableWidget(closeBtn);

        onGroupSelected();
    }

    private void toggleStyleFlag(String flag) {
        NicknameStyle style = getActiveStyle();
        if (style == null) {
            return;
        }
        if ("bold".equals(flag)) {
            style.bold = !style.bold;
            boldBtn.setToggled(style.bold);
        } else if ("italic".equals(flag)) {
            style.italic = !style.italic;
            italicBtn.setToggled(style.italic);
        } else if ("underline".equals(flag)) {
            style.underline = !style.underline;
            underlineBtn.setToggled(style.underline);
        }
        NicknameManager.save();
    }

    private void applyHexColor(String text) {
        NicknameStyle style = getActiveStyle();
        if (style == null) {
            return;
        }
        String clean = text.replace("#", "").trim();
        if (clean.length() != 6) {
            return;
        }
        try {
            int color = Integer.parseInt(clean, 16);
            if (pickingColorIndex == 1) {
                style.color1 = color;
            } else {
                style.color2 = color;
            }
            NicknameManager.save();
        } catch (NumberFormatException ignored) {
        }
    }

    private void ensureSelectedGroup() {
        List<GroupStyle> groups = NicknameManager.getConfig().groups;
        if (groups == null || groups.isEmpty()) {
            NicknameManager.load();
            groups = NicknameManager.getConfig().groups;
        }
        selectedGroupIndex = Math.max(0, Math.min(selectedGroupIndex, groups.size()));
    }

    private GroupStyle getSelectedGroup() {
        List<GroupStyle> groups = NicknameManager.getConfig().groups;
        if (groups == null || groups.isEmpty()) {
            return null;
        }
        ensureSelectedGroup();
        if (selectedGroupIndex == 0) {
            return null;
        }
        return groups.get(selectedGroupIndex - 1);
    }

    private NicknameStyle getActiveStyle() {
        GroupStyle group = getSelectedGroup();
        return group == null ? null : group.style;
    }

    private void onGroupSelected() {
        ensureSelectedGroup();
        if (selectedGroupIndex == 0) {
            groupRenameField.visible = false;
            enabledBtn.visible = false;
            boldBtn.visible = false;
            italicBtn.visible = false;
            underlineBtn.visible = false;
            modeBtn.visible = false;
            addPlayerNameField.visible = false;
            addPlayerBtn.visible = false;
            hexColorField.visible = false;
            deleteBtn.active = false;

            hideNonGroupNicksToggleBtn.visible = true;
            onlyGroupsInLocatorBtn.visible = true;
            keepTeamPrefixesBtn.visible = true;
            prioritizeServerPlayersInTabBtn.visible = true;

            hideNonGroupNicksToggleBtn.setToggled(NicknameManager.getConfig().hideNonGroupNicks);
            onlyGroupsInLocatorBtn.setToggled(NicknameManager.getConfig().onlyGroupsInLocator);
            keepTeamPrefixesBtn.setToggled(NicknameManager.getConfig().keepTeamPrefixes);
            prioritizeServerPlayersInTabBtn.setToggled(NicknameManager.getConfig().prioritizeServerPlayersInTab);
            return;
        }

        GroupStyle group = getSelectedGroup();
        NicknameStyle style = getActiveStyle();
        if (group == null || style == null) {
            return;
        }

        groupRenameField.visible = true;
        enabledBtn.visible = true;
        boldBtn.visible = true;
        italicBtn.visible = true;
        underlineBtn.visible = true;
        modeBtn.visible = true;
        addPlayerNameField.visible = true;
        addPlayerBtn.visible = true;
        hexColorField.visible = true;
        deleteBtn.active = NicknameManager.getConfig().groups.size() > 1;

        hideNonGroupNicksToggleBtn.visible = false;
        onlyGroupsInLocatorBtn.visible = false;
        keepTeamPrefixesBtn.visible = false;
        prioritizeServerPlayersInTabBtn.visible = false;

        groupRenameField.setValue(group.name);
        addPlayerNameField.setValue("");
        playerScrollIndex = Math.max(0, Math.min(playerScrollIndex, group.players.size()));

        enabledBtn.setToggled(style.enabled);
        boldBtn.setToggled(style.bold);
        italicBtn.setToggled(style.italic);
        underlineBtn.setToggled(style.underline);
        updateModeBtnLabel();

        int color = pickingColorIndex == 1 ? style.color1 : style.color2;
        hexColorField.setValue(String.format("#%06X", color));
    }

    private void updateModeBtnLabel() {
        NicknameStyle style = getActiveStyle();
        if (style == null || modeBtn == null) {
            return;
        }
        String mode = "solid";
        if ("GRADIENT".equalsIgnoreCase(style.mode)) {
            mode = "gradient";
        } else if ("RAINBOW".equalsIgnoreCase(style.mode)) {
            mode = "rainbow";
        }
        modeBtn.setMessage(Component.translatable("gui.nickgroups.mode_btn_format", Component.translatable("gui.nickgroups.mode." + mode)));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int left = (this.width - 380) / 2;
        int top = (this.height - 245) / 2;
        List<GroupStyle> groups = NicknameManager.getConfig().groups;

        if (mouseX >= left + 4 && mouseX <= left + 106 && mouseY >= top + 20 && mouseY <= top + 185) {
            int maxScroll = Math.max(0, groups.size() + 1 - 7);
            groupScrollIndex = verticalAmount > 0 ? Math.max(0, groupScrollIndex - 1) : Math.min(maxScroll, groupScrollIndex + 1);
            return true;
        }

        GroupStyle group = getSelectedGroup();
        if (group != null && mouseX >= left + 125 && mouseX <= left + 365 && mouseY >= top + 70 && mouseY <= top + 126) {
            int maxScroll = Math.max(0, group.players.size() - 4);
            playerScrollIndex = verticalAmount > 0 ? Math.max(0, playerScrollIndex - 1) : Math.min(maxScroll, playerScrollIndex + 1);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        int left = (this.width - 380) / 2;
        int top = (this.height - 245) / 2;
        List<GroupStyle> groups = NicknameManager.getConfig().groups;

        int visibleIdx = 0;
        for (int i = groupScrollIndex; i < Math.min(groups.size() + 1, groupScrollIndex + 7); i++) {
            int itemX = left + 10;
            int itemY = top + 25 + visibleIdx * 22;
            visibleIdx++;
            if (mouseX >= itemX && mouseX <= itemX + 90 && mouseY >= itemY && mouseY <= itemY + 18) {
                selectedGroupIndex = i;
                onGroupSelected();
                SoundPlayCompat.play(SoundEvents.UI_BUTTON_CLICK.value());
                return true;
            }
        }

        GroupStyle group = getSelectedGroup();
        if (group != null) {
            for (int i = playerScrollIndex; i < Math.min(group.players.size(), playerScrollIndex + 4); i++) {
                int idx = i - playerScrollIndex;
                int itemY = top + 73 + idx * 13;
                if (mouseX >= left + 334 && mouseX <= left + 344 && mouseY >= itemY - 2 && mouseY <= itemY + 10) {
                    group.players.remove(i);
                    playerScrollIndex = Math.max(0, Math.min(group.players.size() - 4, playerScrollIndex));
                    NicknameManager.save();
                    SoundPlayCompat.play(SoundEvents.UI_BUTTON_CLICK.value());
                    return true;
                }
            }
        }

        return handleColorClick(mouseX, mouseY);
    }

    private boolean handleColorClick(double mouseX, double mouseY) {
        int left = (this.width - 380) / 2;
        int top = (this.height - 245) / 2;
        NicknameStyle style = getActiveStyle();
        if (style == null) {
            return false;
        }

        if (mouseX >= left + 200 && mouseX <= left + 212 && mouseY >= top + 128 && mouseY <= top + 140) {
            pickingColorIndex = 1;
            hexColorField.setValue(String.format("#%06X", style.color1));
            return true;
        }
        if ("GRADIENT".equalsIgnoreCase(style.mode) && mouseX >= left + 216 && mouseX <= left + 228 && mouseY >= top + 128 && mouseY <= top + 140) {
            pickingColorIndex = 2;
            hexColorField.setValue(String.format("#%06X", style.color2));
            return true;
        }
        if (mouseX >= left + 125 && mouseX <= left + 365 && mouseY >= top + 142 && mouseY <= top + 152) {
            applyPickedColor(hsbToRgb((float) (mouseX - (left + 125)) / 240.0f, 1.0f, 1.0f));
            return true;
        }
        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int x = left + 125 + i * 34;
            int y = top + 158;
            if (mouseX >= x && mouseX <= x + 24 && mouseY >= y && mouseY <= y + 14) {
                applyPickedColor(PRESET_COLORS[i]);
                return true;
            }
        }
        return false;
    }

    private boolean handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int left = (this.width - 380) / 2;
        int top = (this.height - 245) / 2;
        if (mouseX >= left + 125 && mouseX <= left + 365 && mouseY >= top + 142 && mouseY <= top + 152) {
            float hue = (float) (mouseX - (left + 125)) / 240.0f;
            applyPickedColor(hsbToRgb(Math.max(0.0f, Math.min(1.0f, hue)), 1.0f, 1.0f));
            return true;
        }
        return false;
    }

    private void applyPickedColor(int color) {
        NicknameStyle style = getActiveStyle();
        if (style == null) {
            return;
        }
        if (pickingColorIndex == 1) {
            style.color1 = color;
        } else {
            style.color2 = color;
        }
        hexColorField.setValue(String.format("#%06X", color));
        NicknameManager.save();
    }

    //? if <1.21.9 {
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (handleMouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (handleMouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    //?} else {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        if (handleMouseClicked(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double deltaX, double deltaY) {
        if (handleMouseDragged(event.x(), event.y(), event.button(), deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }
    *///?}

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        int w = 380;
        int h = 245;
        int left = (this.width - w) / 2;
        int top = (this.height - h) / 2;

        fillRoundedRect(guiGraphics, left, top, w, h, 6, 0xFA151419);
        drawRoundedOutline(guiGraphics, left, top, w, h, 6, 1, 0xFF3F3F3F);
        fillRoundedRect(guiGraphics, left + 4, top + 4, 102, h - 8, 4, 0xFF0E0D11);
        drawRoundedOutline(guiGraphics, left + 4, top + 4, 102, h - 8, 4, 1, 0xFF28272C);

        GuiGraphicsCompat.drawString(guiGraphics, this.font, Component.translatable("gui.nickgroups.groups_header"), left + 10, top + 10, 0xFF888888, false);
        renderGroupList(guiGraphics, left, top);

        if (selectedGroupIndex == 0) {
            GuiGraphicsCompat.drawString(guiGraphics, this.font, Component.translatable("gui.nickgroups.settings_title"), left + 125, top + 12, 0xFFFFFFFF, false);
        } else {
            renderPlayerList(guiGraphics, left, top, mouseX, mouseY);
            renderColorPicker(guiGraphics, left, top, mouseX, mouseY);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderGroupList(GuiGraphics guiGraphics, int left, int top) {
        List<GroupStyle> groups = NicknameManager.getConfig().groups;
        int visibleIdx = 0;
        for (int i = groupScrollIndex; i < Math.min(groups.size() + 1, groupScrollIndex + 7); i++) {
            int itemX = left + 10;
            int itemY = top + 25 + visibleIdx * 22;
            visibleIdx++;
            boolean selected = i == selectedGroupIndex;
            fillRoundedRect(guiGraphics, itemX, itemY, 90, 18, 3, selected ? 0x30E0523C : 0x1A0E0D11);
            drawRoundedOutline(guiGraphics, itemX, itemY, 90, 18, 3, 1, selected ? 0xFFE0523C : 0xFF28272C);

            if (i == 0) {
                Component name = Component.translatable("gui.nickgroups.settings_toggle");
                int textWidth = this.font.width(name);
                GuiGraphicsCompat.drawString(guiGraphics, this.font, name, itemX + (90 - textWidth) / 2, itemY + 5, 0xFFFFFFFF, false);
            } else {
                GroupStyle group = groups.get(i - 1);
                Component name = group.style != null && group.style.enabled ? group.style.format(group.name) : Component.literal(group.name);
                int textWidth = this.font.width(name);
                GuiGraphicsCompat.drawString(guiGraphics, this.font, name, itemX + (90 - textWidth) / 2, itemY + 5, 0xFFFFFFFF, false);
            }
        }
    }

    private void renderPlayerList(GuiGraphics guiGraphics, int left, int top, int mouseX, int mouseY) {
        GroupStyle group = getSelectedGroup();
        if (group == null) {
            return;
        }
        int N = group.players.size();

        fillRoundedRect(guiGraphics, left + 125, top + 70, 240, 56, 3, 0xFF0E0D11);
        drawRoundedOutline(guiGraphics, left + 125, top + 70, 240, 56, 3, 1, 0xFF28272C);

        for (int i = playerScrollIndex; i < Math.min(N, playerScrollIndex + 4); i++) {
            int idx = i - playerScrollIndex;
            int itemY = top + 73 + idx * 13;
            GuiGraphicsCompat.drawString(guiGraphics, this.font, group.players.get(i), left + 131, itemY, 0xFFECEAEF, false);

            boolean hoverX = mouseX >= left + 334 && mouseX <= left + 344 && mouseY >= itemY - 2 && mouseY <= itemY + 10;
            GuiGraphicsCompat.drawString(guiGraphics, this.font, "x", left + 336, itemY - 1, hoverX ? 0xFFFF5555 : 0xFF888888, false);
        }

        if (N > 4) {
            int trackX = left + 348;
            int trackY = top + 72;
            int trackH = 52;
            fillRoundedRect(guiGraphics, trackX, trackY, 4, trackH, 1, 0xFF151419);
            int handleH = Math.max(10, (4 * trackH) / N);
            int handleY = (playerScrollIndex * (trackH - handleH)) / (N - 4);
            fillRoundedRect(guiGraphics, trackX, trackY + handleY, 4, handleH, 1, 0xFFE0523C);
        }
    }

    private void renderColorPicker(GuiGraphics guiGraphics, int left, int top, int mouseX, int mouseY) {
        NicknameStyle style = getActiveStyle();
        GroupStyle group = getSelectedGroup();
        if (style == null || group == null) {
            return;
        }

        GuiGraphicsCompat.drawString(guiGraphics, this.font, Component.translatable("gui.nickgroups.choose_color"), left + 125, top + 130, 0xFFAAAAAA, false);

        fillRoundedRect(guiGraphics, left + 200, top + 128, 12, 12, 1, 0xFF000000 | style.color1);
        if (pickingColorIndex == 1) {
            drawRoundedOutline(guiGraphics, left + 199, top + 127, 14, 14, 1, 1, 0xFFFFFFFF);
        } else {
            drawRoundedOutline(guiGraphics, left + 200, top + 128, 12, 12, 1, 1, 0xFF3F3F3F);
        }

        if ("GRADIENT".equalsIgnoreCase(style.mode)) {
            fillRoundedRect(guiGraphics, left + 216, top + 128, 12, 12, 1, 0xFF000000 | style.color2);
            if (pickingColorIndex == 2) {
                drawRoundedOutline(guiGraphics, left + 215, top + 127, 14, 14, 1, 1, 0xFFFFFFFF);
            } else {
                drawRoundedOutline(guiGraphics, left + 216, top + 128, 12, 12, 1, 1, 0xFF3F3F3F);
            }
        }

        int hexX = hexColorField.getX();
        int hexY = hexColorField.getY();
        int hexW = hexColorField.getWidth();
        int hexH = hexColorField.getHeight();
        fillRoundedRect(guiGraphics, hexX - 4, hexY - 3, hexW + 8, hexH + 6, 2, 0x50151419);
        drawRoundedOutline(guiGraphics, hexX - 4, hexY - 3, hexW + 8, hexH + 6, 2, 1, 0xFF28272C);

        int specX = left + 125;
        int specY = top + 142;
        int specW = 240;
        int specH = 10;
        fillRoundedRect(guiGraphics, specX - 1, specY - 1, specW + 2, specH + 2, 2, 0xFF28272C);
        for (int x = 0; x < specW; x++) {
            guiGraphics.fill(specX + x, specY, specX + x + 1, specY + specH, 0xFF000000 | hsbToRgb((float) x / (float) specW, 1.0f, 1.0f));
        }

        int activeColor = pickingColorIndex == 1 ? style.color1 : style.color2;
        float[] hsb = rgbToHsb((activeColor >> 16) & 0xFF, (activeColor >> 8) & 0xFF, activeColor & 0xFF);
        int selX = specX + (int)(hsb[0] * specW);
        fillRoundedRect(guiGraphics, Math.max(specX, selX - 1), specY - 2, 3, specH + 4, 1, 0xFFFFFFFF);

        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int x = left + 125 + i * 34;
            int y = top + 158;
            fillRoundedRect(guiGraphics, x, y, 24, 14, 2, 0xFF000000 | PRESET_COLORS[i]);
            if (activeColor == PRESET_COLORS[i]) {
                drawRoundedOutline(guiGraphics, x - 1, y - 1, 26, 16, 2, 2, 0xFFFFFFFF);
            } else if (mouseX >= x && mouseX <= x + 24 && mouseY >= y && mouseY <= y + 14) {
                drawRoundedOutline(guiGraphics, x - 1, y - 1, 26, 16, 2, 1, 0xFFAAAAAA);
            } else {
                drawRoundedOutline(guiGraphics, x, y, 24, 14, 2, 1, 0xFF3F3F3F);
            }
        }

        if (style.enabled) {
            fillRoundedRect(guiGraphics, left + 125, top + 178, 240, 14, 3, 0xFF000000 | style.color1);
        } else {
            fillRoundedRect(guiGraphics, left + 125, top + 178, 240, 14, 3, 0xFF2D1E1B);
            GuiGraphicsCompat.drawCenteredString(guiGraphics, this.font, Component.translatable("gui.nickgroups.preview_disabled"), left + 245, top + 181, 0xFF888888);
        }

        fillRoundedRect(guiGraphics, left + 125, top + 198, 240, 18, 3, 0x30151419);
        drawRoundedOutline(guiGraphics, left + 125, top + 198, 240, 18, 3, 1, 0xFF3F3F3F);

        Component previewComponent = style.format(group.name);
        GuiGraphicsCompat.drawCenteredString(guiGraphics, this.font, previewComponent, left + 245, top + 203, 0xFFFFFFFF);
    }

    public static void fillRoundedRect(GuiGraphics graphics, int x, int y, int w, int h, int r, int color) {
        graphics.fill(x + r, y, x + w - r, y + h, color);
        graphics.fill(x, y + r, x + r, y + h - r, color);
        graphics.fill(x + w - r, y + r, x + w, y + h - r, color);
        drawQuarterCircle(graphics, x + r, y + r, r, color, 180);
        drawQuarterCircle(graphics, x + w - r, y + r, r, color, 270);
        drawQuarterCircle(graphics, x + r, y + h - r, r, color, 90);
        drawQuarterCircle(graphics, x + w - r, y + h - r, r, color, 0);
    }

    public static void drawRoundedOutline(GuiGraphics graphics, int x, int y, int w, int h, int r, int thickness, int color) {
        graphics.fill(x + r, y, x + w - r, y + thickness, color);
        graphics.fill(x + r, y + h - thickness, x + w - r, y + h, color);
        graphics.fill(x, y + r, x + thickness, y + h - r, color);
        graphics.fill(x + w - thickness, y + r, x + w, y + h - r, color);
        drawQuarterCircleOutline(graphics, x + r, y + r, r, color, 180);
        drawQuarterCircleOutline(graphics, x + w - r, y + r, r, color, 270);
        drawQuarterCircleOutline(graphics, x + r, y + h - r, r, color, 90);
        drawQuarterCircleOutline(graphics, x + w - r, y + h - r, r, color, 0);
    }

    private static void drawQuarterCircle(GuiGraphics graphics, int cx, int cy, int r, int color, int startAngle) {
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                if (x * x + y * y <= r * r && inQuarter(x, y, startAngle)) {
                    graphics.fill(cx + x, cy + y, cx + x + 1, cy + y + 1, color);
                }
            }
        }
    }

    private static void drawQuarterCircleOutline(GuiGraphics graphics, int cx, int cy, int r, int color, int startAngle) {
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                double dist = x * x + y * y;
                if (dist >= (r - 1.2) * (r - 1.2) && dist <= (r + 0.5) * (r + 0.5) && inQuarter(x, y, startAngle)) {
                    graphics.fill(cx + x, cy + y, cx + x + 1, cy + y + 1, color);
                }
            }
        }
    }

    private static boolean inQuarter(int x, int y, int startAngle) {
        return (startAngle == 180 && x <= 0 && y <= 0)
            || (startAngle == 270 && x >= 0 && y <= 0)
            || (startAngle == 90 && x <= 0 && y >= 0)
            || (startAngle == 0 && x >= 0 && y >= 0);
    }

    private static int hsbToRgb(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - saturation * (1.0f - f));
            switch ((int) h) {
                case 0 -> { r = (int) (brightness * 255.0f + 0.5f); g = (int) (t * 255.0f + 0.5f); b = (int) (p * 255.0f + 0.5f); }
                case 1 -> { r = (int) (q * 255.0f + 0.5f); g = (int) (brightness * 255.0f + 0.5f); b = (int) (p * 255.0f + 0.5f); }
                case 2 -> { r = (int) (p * 255.0f + 0.5f); g = (int) (brightness * 255.0f + 0.5f); b = (int) (t * 255.0f + 0.5f); }
                case 3 -> { r = (int) (p * 255.0f + 0.5f); g = (int) (q * 255.0f + 0.5f); b = (int) (brightness * 255.0f + 0.5f); }
                case 4 -> { r = (int) (t * 255.0f + 0.5f); g = (int) (p * 255.0f + 0.5f); b = (int) (brightness * 255.0f + 0.5f); }
                case 5 -> { r = (int) (brightness * 255.0f + 0.5f); g = (int) (p * 255.0f + 0.5f); b = (int) (q * 255.0f + 0.5f); }
            }
        }
        return (r << 16) | (g << 8) | b;
    }

    private static float[] rgbToHsb(int r, int g, int b) {
        float[] hsb = new float[3];
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float h = 0.0f;
        if (delta != 0.0f) {
            if (max == rf) {
                h = ((gf - bf) / delta) % 6.0f;
            } else if (max == gf) {
                h = ((bf - rf) / delta) + 2.0f;
            } else {
                h = ((rf - gf) / delta) + 4.0f;
            }
            h /= 6.0f;
            if (h < 0.0f) {
                h += 1.0f;
            }
        }

        hsb[0] = h;
        hsb[1] = max == 0.0f ? 0.0f : delta / max;
        hsb[2] = max;
        return hsb;
    }

    //? if <1.21.9 {
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (newGroupNameField.isFocused() || groupRenameField.isFocused() || addPlayerNameField.isFocused() || hexColorField.isFocused()) {
            return false;
        }
        KeyMapping openKey = NickgroupsModClient.getOpenGuiKeyBinding();
        if (openKey != null && openKey.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return false;
    }
    //?} else {
    /*@Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (super.keyPressed(event)) {
            return true;
        }
        if (newGroupNameField.isFocused() || groupRenameField.isFocused() || addPlayerNameField.isFocused() || hexColorField.isFocused()) {
            return false;
        }
        KeyMapping openKey = NickgroupsModClient.getOpenGuiKeyBinding();
        if (openKey != null && openKey.matches(event)) {
            this.onClose();
            return true;
        }
        return false;
    }
    *///?}
}
