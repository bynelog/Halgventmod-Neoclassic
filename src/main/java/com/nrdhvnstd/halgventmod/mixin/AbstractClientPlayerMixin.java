package com.nrdhvnstd.halgventmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.nrdhvnstd.halgventmod.client.character.AppearanceSkins;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {

    // Скин лаунчера не используется: скин собирается из облика персонажа (геймдок 3.1)
    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    private void halgventmod$appearanceSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        PlayerSkin skin = AppearanceSkins.skinOf((AbstractClientPlayer) (Object) this);
        if (skin != null) {
            cir.setReturnValue(skin);
        }
    }
}
