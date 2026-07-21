package com.example.client.mixin;

import com.example.client.NicknameManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInfo.class)
public class PlayerInfoMixin {

    @Inject(method = "getTabListDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Component> cir) {
        PlayerInfo info = (PlayerInfo) (Object) this;
        GameProfile profile = info.getProfile();
        if (profile == null) return;

        Component formatted = NicknameManager.getFormat(profile);
        if (formatted != null) {
            if (NicknameManager.getConfig().keepTeamPrefixes && info.getTeam() != null) {
                Component prefix = info.getTeam().getPlayerPrefix();
                Component suffix = info.getTeam().getPlayerSuffix();
                cir.setReturnValue(Component.empty().copy().append(prefix).append(formatted).append(suffix));
            } else {
                cir.setReturnValue(formatted);
            }
        }
    }
}
