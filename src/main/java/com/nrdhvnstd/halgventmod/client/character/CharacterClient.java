package com.nrdhvnstd.halgventmod.client.character;

import javax.annotation.Nullable;

import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.network.CharacterOpenPayload;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Создание персонажа на клиенте: окно открывает сервер (CharacterOpenPayload) и закрывает его ответ
 * (CharacterResultPayload). Пока персонаж не создан, окно возвращается, если его что-то закрыло
 * (экран смерти, чужое окно) — с уже выбранным.
 */
@EventBusSubscriber(modid = HalgventMod.MODID, value = Dist.CLIENT)
public final class CharacterClient {
    @Nullable
    private static CharacterCreationScreen pending;

    private CharacterClient() {}

    public static void open(CharacterOpenPayload payload) {
        pending = new CharacterCreationScreen(payload);
        Minecraft.getInstance().setScreen(pending);
    }

    /** Ответ сервера: пустая ошибка — персонаж создан. */
    public static void result(String error) {
        if (pending == null) {
            return;
        }
        if (error.isEmpty()) {
            CharacterCreationScreen screen = pending;
            pending = null;
            if (Minecraft.getInstance().screen == screen) {
                Minecraft.getInstance().setScreen(null);
            }
        } else {
            pending.showError(error);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (pending != null && minecraft.screen == null && minecraft.player != null && minecraft.player.isAlive()) {
            minecraft.setScreen(pending);
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        pending = null;
    }
}
