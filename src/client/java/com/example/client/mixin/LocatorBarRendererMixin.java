package com.example.client.mixin;

import com.example.client.NicknameManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(targets = "net.minecraft.client.gui.contextualbar.LocatorBarRenderer")
public class LocatorBarRendererMixin {

    @Inject(
        method = "method_70870",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void onRenderWaypoint(
        net.minecraft.world.entity.Entity cameraEntity,
        net.minecraft.world.level.Level level,
        Object partialTickSupplier,
        net.minecraft.client.gui.GuiGraphics guiGraphics,
        int x,
        Object trackedWaypoint,
        CallbackInfo ci
    ) {
        if (trackedWaypoint != null) {
            try {
                java.lang.reflect.Method idMethod = trackedWaypoint.getClass().getMethod("id");
                com.mojang.datafixers.util.Either<?, ?> id = (com.mojang.datafixers.util.Either<?, ?>) idMethod.invoke(trackedWaypoint);
                if (id != null && id.left().isPresent()) {
                    UUID uuid = (UUID) id.left().get();
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.getConnection() != null) {
                        PlayerInfo info = mc.getConnection().getPlayerInfo(uuid);
                        if (info != null) {
                            String name = NicknameManager.getProfileName(info.getProfile());
                            if (NicknameManager.getConfig().onlyGroupsInLocator && !NicknameManager.isInAnyGroup(name)) {
                                ci.cancel();
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
