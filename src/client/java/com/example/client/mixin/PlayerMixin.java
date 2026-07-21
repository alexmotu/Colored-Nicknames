package com.example.client.mixin;

import com.example.client.NicknameManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Component> cir) {
        Player player = (Player) (Object) this;
        Component formatted = NicknameManager.getFormat(player);
        if (formatted != null) {
            if (NicknameManager.getConfig().keepTeamPrefixes && player.getTeam() != null) {
                Component prefix = player.getTeam().getPlayerPrefix();
                Component suffix = player.getTeam().getPlayerSuffix();
                cir.setReturnValue(Component.empty().copy().append(prefix).append(formatted).append(suffix));
            } else {
                cir.setReturnValue(formatted);
            }
        }
    }
}
