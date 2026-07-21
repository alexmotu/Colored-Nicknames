package com.example.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class NickgroupsModClient implements ClientModInitializer {
    private static KeyMapping openGuiKeyBinding;

    @Override
    public void onInitializeClient() {
        openGuiKeyBinding = KeyBindingHelperCompat.registerOpenGuiKey(GLFW.GLFW_KEY_N);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKeyBinding.consumeClick()) {
                //? if >=26.2 {
                /*client.setScreenAndShow(new NicknameScreen());
                *///?} else {
                client.setScreen(new NicknameScreen());
                //?}
            }
        });
    }

    public static KeyMapping getOpenGuiKeyBinding() {
        return openGuiKeyBinding;
    }
}
