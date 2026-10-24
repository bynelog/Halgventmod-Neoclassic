package com.nrdhvnstd.halgventmod.sound;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.character.Appearance;
import com.nrdhvnstd.halgventmod.character.CharacterData;
import com.nrdhvnstd.halgventmod.registry.ModAttachments;
import com.nrdhvnstd.halgventmod.registry.ModSounds;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

/**
 * Голосовые наборы игрока: halgventmod:voice.<набор>.<крик> в sounds.json, в реестр не попадают
 * (TZ_character_fx.md, раздел 13): новый набор добавляется только ресурсами. Сервер sounds.json не видит,
 * поэтому состав наборов пока списком здесь; позже его заменят assets/halgventmod/voices/<id>.json.
 */
public final class Voices {
    public static final String DEFAULT_SET = "male_1"; // пока персонаж не создан

    /** Вид крика. Субтитр один на вид для всех наборов: subtitles.halgventmod.voice.<вид>. */
    public enum Cry {
        HURT_LIGHT    ("hurt_light",    "Игрок стонет",               "Player groans"),
        HURT_HEAVY    ("hurt_heavy",    "Игрок кричит от боли",       "Player screams in pain"),
        DEATH         ("death",         "Игрок умирает",              "Player dies"),
        EFFORT        ("effort",        "Игрок кряхтит от усилия",    "Player grunts with effort"),
        DROWN         ("drown",         "Игрок захлёбывается",        "Player chokes"),
        OUT_OF_BREATH ("out_of_breath", "Игрок задыхается",           "Player gasps for air"),
        WARCRY        ("warcry",        "Игрок издаёт боевой клич",   "Player shouts war cry");
        // позже: downed, bandage, burn — когда будут файлы

        private final String id;
        private final String subtitleRu;
        private final String subtitleEn;

        Cry(String id, String subtitleRu, String subtitleEn) {
            this.id = id;
            this.subtitleRu = subtitleRu;
            this.subtitleEn = subtitleEn;
        }

        public String id() {
            return id;
        }

        public String subtitleRu() {
            return subtitleRu;
        }

        public String subtitleEn() {
            return subtitleEn;
        }

        public String subtitleKey() {
            return ModSounds.subtitleKey("voice." + id);
        }
    }

    // Какие крики есть в каком наборе. Чего нет — тишина, а не чужой голос
    private static final Map<String, Set<Cry>> SETS = Map.of(
            "male_1", EnumSet.complementOf(EnumSet.of(Cry.WARCRY)),
            "female_1", EnumSet.complementOf(EnumSet.of(Cry.WARCRY)),
            "male_2", EnumSet.of(Cry.HURT_LIGHT, Cry.DEATH),
            "female_2", EnumSet.of(Cry.HURT_LIGHT, Cry.DEATH),
            "male_3", EnumSet.of(Cry.WARCRY));

    private Voices() {}

    /** Набор голоса игрока: выбран в облике при создании персонажа (Appearance.Part.VOICE). */
    public static String setOf(Player player) {
        CharacterData data = player.getExistingDataOrNull(ModAttachments.CHARACTER);
        String voice = data == null ? "" : data.appearance().getOrDefault(Appearance.Part.VOICE.id(), "");
        return SETS.containsKey(voice) ? voice : DEFAULT_SET;
    }

    public static boolean has(String set, Cry cry) {
        Set<Cry> cries = SETS.get(set);
        return cries != null && cries.contains(cry);
    }

    /** Звук крика из набора или null, если в наборе такого нет. */
    @Nullable
    public static Holder<SoundEvent> sound(String set, Cry cry) {
        if (!has(set, cry)) {
            return null;
        }
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(HalgventMod.MODID, "voice." + set + "." + cry.id());
        return Holder.direct(SoundEvent.createVariableRangeEvent(id));
    }
}
