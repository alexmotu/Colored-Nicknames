package com.example.client;

import com.mojang.blaze3d.platform.InputConstants;
//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
*///?} else {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?}
import net.minecraft.client.KeyMapping;

import java.lang.reflect.Constructor;

public class KeyBindingHelperCompat {
    public static KeyMapping registerOpenGuiKey(int defaultKey) {
        try {
            Class<?> categoryClass = null;
            java.lang.reflect.Method registerMethod = null;

            for (Class<?> c : KeyMapping.class.getDeclaredClasses()) {
                for (java.lang.reflect.Method m : c.getMethods()) {
                    if (java.lang.reflect.Modifier.isStatic(m.getModifiers()) &&
                        m.getParameterCount() == 1 &&
                        m.getParameterTypes()[0].isAssignableFrom(net.minecraft.resources.ResourceLocation.class)) {
                        categoryClass = c;
                        registerMethod = m;
                        break;
                    }
                }
                if (categoryClass != null) break;
            }

            if (categoryClass != null && registerMethod != null) {
                Object category = registerMethod.invoke(null, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("nickgroups", "general"));

                Constructor<KeyMapping> constructor = KeyMapping.class.getConstructor(
                    String.class,
                    InputConstants.Type.class,
                    int.class,
                    categoryClass
                );
                KeyMapping key = constructor.newInstance(
                    "key.nickgroups.open_gui",
                    InputConstants.Type.KEYSYM,
                    defaultKey,
                    category
                );
                //? if >=26.1 {
                /*return KeyMappingHelper.registerKeyMapping(key);
                *///?} else {
                return KeyBindingHelper.registerKeyBinding(key);
                //?}
            } else {
                //? if <1.21.2 {
                //? if >=26.1 {
                /*return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                    "key.nickgroups.open_gui",
                    InputConstants.Type.KEYSYM,
                    defaultKey,
                    "key.categories.nickgroups"
                ));
                *///?} else {
                return KeyBindingHelper.registerKeyBinding(new KeyMapping(
                    "key.nickgroups.open_gui",
                    InputConstants.Type.KEYSYM,
                    defaultKey,
                    "key.categories.nickgroups"
                ));
                //?}
                //?} else {
                /*return null;
                *///?}
            }
        } catch (Throwable t) {
            t.printStackTrace();
            //? if <1.21.2 {
            try {
                return KeyBindingHelper.registerKeyBinding(new KeyMapping(
                    "key.nickgroups.open_gui",
                    InputConstants.Type.KEYSYM,
                    defaultKey,
                    "key.categories.nickgroups"
                ));
            } catch (Throwable t2) {
                t2.printStackTrace();
            }
            //?}
            return null;
        }
    }
}
