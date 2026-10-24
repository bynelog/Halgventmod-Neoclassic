package com.nrdhvnstd.halgventmod.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.character.Backstory;
import com.nrdhvnstd.halgventmod.character.CharacterData;
import com.nrdhvnstd.halgventmod.client.character.CharacterClient;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Сервер → клиент: открыть создание персонажа. full — весь путь (облик, предыстория, точка появления, итог, имя),
 * иначе только облик и имя. appearance и name — что уже выбрано (пустые — ничего).
 * Предыстории и точки появления приходят только для полного пути.
 */
public record CharacterOpenPayload(boolean full, Map<String, String> appearance, String name,
                                   List<Backstory> backstories, List<Spawn> spawns) implements CustomPacketPayload {
    public static final int MAX_ENTRIES = 256;

    /** Точка появления в окне выбора: id точки карты, название, описание. */
    public record Spawn(int id, String name, String description) {
        private void write(FriendlyByteBuf buf) {
            buf.writeVarInt(id);
            buf.writeUtf(name);
            buf.writeUtf(description);
        }

        private static Spawn read(FriendlyByteBuf buf) {
            return new Spawn(buf.readVarInt(), buf.readUtf(), buf.readUtf());
        }
    }

    public static final Type<CharacterOpenPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HalgventMod.MODID, "character_open"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CharacterOpenPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> payload.write(buf), CharacterOpenPayload::read);

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(full);
        CharacterData.writeAppearance(buf, appearance);
        buf.writeUtf(name);
        int backstoryCount = Math.min(backstories.size(), MAX_ENTRIES);
        buf.writeVarInt(backstoryCount);
        for (int i = 0; i < backstoryCount; i++) {
            Backstory.STREAM_CODEC.encode(buf, backstories.get(i));
        }
        int spawnCount = Math.min(spawns.size(), MAX_ENTRIES);
        buf.writeVarInt(spawnCount);
        for (int i = 0; i < spawnCount; i++) {
            spawns.get(i).write(buf);
        }
    }

    private static CharacterOpenPayload read(RegistryFriendlyByteBuf buf) {
        boolean full = buf.readBoolean();
        Map<String, String> appearance = CharacterData.readAppearance(buf);
        String name = buf.readUtf();
        int backstoryCount = Math.min(buf.readVarInt(), MAX_ENTRIES);
        List<Backstory> backstories = new ArrayList<>(backstoryCount);
        for (int i = 0; i < backstoryCount; i++) {
            backstories.add(Backstory.STREAM_CODEC.decode(buf));
        }
        int spawnCount = Math.min(buf.readVarInt(), MAX_ENTRIES);
        List<Spawn> spawns = new ArrayList<>(spawnCount);
        for (int i = 0; i < spawnCount; i++) {
            spawns.add(Spawn.read(buf));
        }
        return new CharacterOpenPayload(full, appearance, name, List.copyOf(backstories), List.copyOf(spawns));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CharacterOpenPayload payload, IPayloadContext context) {
        // Окно открывает CharacterClient (статический вызов), чтобы сервер не трогал классы интерфейса
        context.enqueueWork(() -> CharacterClient.open(payload));
    }
}
