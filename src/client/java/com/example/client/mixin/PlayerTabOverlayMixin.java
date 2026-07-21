package com.example.client.mixin;

import com.example.client.NicknameManager;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(method = "getPlayerInfos", at = @At("RETURN"), cancellable = true)
    private void onGetPlayerInfos(CallbackInfoReturnable<List<PlayerInfo>> cir) {
        List<PlayerInfo> originalList = cir.getReturnValue();
        if (originalList == null || originalList.isEmpty()) return;

        if (NicknameManager.getConfig().prioritizeServerPlayersInTab) {
            List<PlayerInfo> sortedList = new ArrayList<>(originalList);
            sortedList.sort((p1, p2) -> {
                boolean p1HasSynced = false;
                if (p1 != null && p1.getProfile() != null) {
                    p1HasSynced = NicknameManager.isInAnyGroup(NicknameManager.getProfileName(p1.getProfile()));
                }

                boolean p2HasSynced = false;
                if (p2 != null && p2.getProfile() != null) {
                    p2HasSynced = NicknameManager.isInAnyGroup(NicknameManager.getProfileName(p2.getProfile()));
                }

                if (p1HasSynced && !p2HasSynced) {
                    return -1;
                } else if (!p1HasSynced && p2HasSynced) {
                    return 1;
                } else {
                    return 0;
                }
            });
            cir.setReturnValue(sortedList);
        }
    }
}
