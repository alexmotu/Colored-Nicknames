package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;

public class SoundPlayCompat {
    private static java.lang.reflect.Method playMethod;

    static {
        try {
            for (java.lang.reflect.Method m : SoundManager.class.getMethods()) {
                if (m.getName().equals("play") || m.getName().equals("method_4873")) {
                    Class<?>[] params = m.getParameterTypes();
                    if (params.length == 1 && SoundInstance.class.isAssignableFrom(params[0])) {
                        playMethod = m;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void play(SoundEvent soundEvent) {
        try {
            SoundManager manager = Minecraft.getInstance().getSoundManager();
            SimpleSoundInstance sound = SimpleSoundInstance.forUI(soundEvent, 1.0F);
            if (playMethod != null) {
                playMethod.invoke(manager, sound);
            } else {
                manager.play(sound);
            }
        } catch (Exception e) {
            try {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, 1.0F));
            } catch (Throwable t) {
            }
        }
    }
}
