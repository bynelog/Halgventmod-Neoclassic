package com.nrdhvnstd.halgventmod.network;

import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.client.character.CharacterClient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Сервер → клиент: ответ на CharacterCreatePayload. error пустой — персонаж создан, окно закрывается;
 * иначе — ключ ошибки (gui.halgventmod.creation.error.<error>), окно остаётся.
 */
public record CharacterResultPayload(String error) implements CustomPacketPayload {
    public static final Type<CharacterResultPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HalgventMod.MODID, "character_result"));

    public static final StreamCodec<ByteBuf, CharacterResultPayload> STREAM_CODEC =
            ByteBufCodecs.stringUtf8(64).map(CharacterResultPayload::new, CharacterResultPayload::error);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CharacterResultPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> CharacterClient.result(payload.error()));
    }
}
