package com.nrdhvnstd.halgventmod.character;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

/**
 * Персонаж игрока (ModAttachments.CHARACTER): создан ли, облик и предыстория.
 * Облик виден всем, кто видит игрока, — по нему у них собирается скин.
 */
public record CharacterData(State state, Map<String, String> appearance, String backstory) {
    public enum State {
        /** Ещё не создан: весь путь — облик, предыстория с комплектом, точка появления, имя. */
        NEW,
        /** Играл до создания персонажа (или администратор открыл правку): только облик и имя. */
        EDIT,
        CREATED;

        static State byName(String name) {
            for (State state : values()) {
                if (state.name().toLowerCase(Locale.ROOT).equals(name)) {
                    return state;
                }
            }
            return NEW;
        }
    }

    public static final int MAX_PARTS = 32;
    public static final int MAX_TEXT = 64;

    public static final CharacterData NONE = new CharacterData(State.NEW, Map.of(), "");

    public static final Codec<CharacterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(State::byName, state -> state.name().toLowerCase(Locale.ROOT))
                    .fieldOf("state").forGetter(CharacterData::state),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("appearance", Map.of())
                    .forGetter(CharacterData::appearance),
            Codec.STRING.optionalFieldOf("backstory", "").forGetter(CharacterData::backstory))
            .apply(instance, CharacterData::new));

    // Синхронизация: состояние и облик (предыстория клиентам не нужна)
    public static final AttachmentSyncHandler<CharacterData> SYNC = new AttachmentSyncHandler<>() {
        @Override
        public void write(RegistryFriendlyByteBuf buf, CharacterData data, boolean initialSync) {
            buf.writeEnum(data.state());
            writeAppearance(buf, data.appearance());
        }

        @Override
        public CharacterData read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable CharacterData previous) {
            State state = buf.readEnum(State.class);
            return new CharacterData(state, readAppearance(buf), "");
        }
    };

    public boolean created() {
        return state == State.CREATED;
    }

    // ===== Облик в пакетах =====

    public static void writeAppearance(FriendlyByteBuf buf, Map<String, String> appearance) {
        int count = Math.min(appearance.size(), MAX_PARTS);
        buf.writeVarInt(count);
        appearance.entrySet().stream().limit(count).forEach(entry -> {
            buf.writeUtf(entry.getKey(), MAX_TEXT);
            buf.writeUtf(entry.getValue(), MAX_TEXT);
        });
    }

    public static Map<String, String> readAppearance(FriendlyByteBuf buf) {
        int count = Math.min(buf.readVarInt(), MAX_PARTS);
        Map<String, String> appearance = new HashMap<>();
        for (int i = 0; i < count; i++) {
            appearance.put(buf.readUtf(MAX_TEXT), buf.readUtf(MAX_TEXT));
        }
        return Map.copyOf(appearance);
    }
}
