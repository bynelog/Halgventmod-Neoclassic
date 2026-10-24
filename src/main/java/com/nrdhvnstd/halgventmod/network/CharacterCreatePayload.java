package com.nrdhvnstd.halgventmod.network;

import java.util.Map;

import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.character.CharacterCreation;
import com.nrdhvnstd.halgventmod.character.CharacterData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Клиент → сервер: персонаж создан — облик, предыстория (id; "" — без), точка появления (id точки карты;
 * NO_SPAWN — остаться на месте) и имя. Всё проверяет сервер (CharacterCreation), ответ — CharacterResultPayload.
 */
public record CharacterCreatePayload(Map<String, String> appearance, String backstory, int spawn, String name)
        implements CustomPacketPayload {
    public static final int NO_SPAWN = 0;
    public static final int MAX_TEXT = 128;

    public static final Type<CharacterCreatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HalgventMod.MODID, "character_create"));

    public static final StreamCodec<FriendlyByteBuf, CharacterCreatePayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                CharacterData.writeAppearance(buf, payload.appearance());
                buf.writeUtf(payload.backstory(), MAX_TEXT);
                buf.writeVarInt(payload.spawn());
                buf.writeUtf(payload.name(), MAX_TEXT);
            },
            buf -> new CharacterCreatePayload(CharacterData.readAppearance(buf), buf.readUtf(MAX_TEXT),
                    buf.readVarInt(), buf.readUtf(MAX_TEXT)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CharacterCreatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                CharacterCreation.finish(player, payload);
            }
        });
    }
}
