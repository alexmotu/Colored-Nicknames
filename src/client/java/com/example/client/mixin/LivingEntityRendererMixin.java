package com.example.client.mixin;

import com.example.client.NicknameManager;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    //? if <1.21.9 {
    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void onShouldShowName(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player player) {
            if (NicknameManager.getConfig().hideNonGroupNicks) {
                String name = player.getName().getString();
                if (!NicknameManager.isInAnyGroup(name)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
    //?} else {
    /*@Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At("HEAD"), cancellable = true)
    private void onShouldShowName(LivingEntity entity, double distance, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player player) {
            if (NicknameManager.getConfig().hideNonGroupNicks) {
                String name = player.getName().getString();
                if (!NicknameManager.isInAnyGroup(name)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
    *///?}
}
