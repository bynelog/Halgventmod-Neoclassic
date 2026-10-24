package com.nrdhvnstd.halgventmod.character;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.nrdhvnstd.halgventmod.HalgventMod;

import net.minecraft.util.RandomSource;

/**
 * Облик персонажа (геймдок 3.1, «Кастомизация облика»): в каждой части выбран один вариант.
 * Хранится как «часть → вариант» (CharacterData), сервер проверяет его здесь же.
 * Клиент собирает из вариантов скин 64×64 (client/character/AppearanceSkins): картинки слоёв лежат в
 * textures/entity/appearance/<часть>/<вариант>.png и перерисовываются без кода. Новый вариант — строка здесь
 * и перевод в ModLanguageProvider. Цвета кожи, глаз и волос — оттенки, которыми красятся серые слои.
 */
public final class Appearance {
    public enum Part {
        SEX("sex", "male", "female"),
        AGE("age", "young", "mature", "old"),
        BUILD("build", "sturdy", "slim"),
        SKIN("skin", "pale", "fair", "ruddy", "olive", "tan", "dark"),
        EYES("eyes", "blue", "grey", "green", "hazel", "brown", "dark"),
        HAIR("hair", "bald", "short", "bowl", "long", "braid"),
        HAIR_COLOR("hair_color", "black", "dark_brown", "brown", "red", "blond", "ash"),
        BEARD("beard", "none", "stubble", "mustache", "short", "full", "long"),
        SCAR("scar", "none", "cheek", "brow", "lip", "eye"),
        TATTOO("tattoo", "none", "cheek", "brow", "chin"),
        VOICE("voice", "male_1", "male_2", "male_3", "female_1", "female_2");

        private final String id;
        private final List<String> options;

        Part(String id, String... options) {
            this.id = id;
            this.options = List.of(options);
        }

        public String id() {
            return id;
        }

        public List<String> options() {
            return options;
        }

        /** Ключ перевода названия части: appearance.halgventmod.hair */
        public String langKey() {
            return "appearance." + HalgventMod.MODID + "." + id;
        }

        /** Ключ перевода варианта: appearance.halgventmod.hair.long */
        public String optionKey(String option) {
            return langKey() + "." + option;
        }

        @Nullable
        public static Part byId(String id) {
            for (Part part : values()) {
                if (part.id.equals(id)) {
                    return part;
                }
            }
            return null;
        }
    }

    // Оттенки (RGB) для серых слоёв ⚠️ подобраны на глаз
    private static final Map<String, Integer> SKIN_COLORS = Map.of(
            "pale", 0xF2D6C4, "fair", 0xE8BE9C, "ruddy", 0xE0A98A,
            "olive", 0xC99A6E, "tan", 0xB07D55, "dark", 0x7E5434);
    private static final Map<String, Integer> EYE_COLORS = Map.of(
            "blue", 0x4A7BB5, "grey", 0x8A949C, "green", 0x5E8A4A,
            "hazel", 0x8A6A3A, "brown", 0x6B4226, "dark", 0x2E1E14);
    private static final Map<String, Integer> HAIR_COLORS = Map.of(
            "black", 0x2A2420, "dark_brown", 0x4A3222, "brown", 0x7A5434,
            "red", 0xA8502A, "blond", 0xD8B878, "ash", 0xB8B0A0);
    private static final int GREY_HAIR = 0xD0D0CC;

    /** Облик, пока его не выбрали: первый вариант каждой части. */
    public static final Map<String, String> DEFAULT = defaults();

    private Appearance() {}

    private static Map<String, String> defaults() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Part part : Part.values()) {
            map.put(part.id(), part.options().get(0));
        }
        return Map.copyOf(map);
    }

    /** Вариант части; нет или неизвестный — первый. */
    public static String get(Map<String, String> appearance, Part part) {
        String value = appearance.get(part.id());
        return value != null && part.options().contains(value) ? value : part.options().get(0);
    }

    public static boolean female(Map<String, String> appearance) {
        return "female".equals(get(appearance, Part.SEX));
    }

    /** Облик от клиента: все части на месте и варианты известные — тот же облик, иначе null. */
    @Nullable
    public static Map<String, String> validated(Map<String, String> appearance) {
        if (appearance.size() != Part.values().length) {
            return null;
        }
        for (Part part : Part.values()) {
            String value = appearance.get(part.id());
            if (value == null || !part.options().contains(value)) {
                return null;
            }
        }
        return Map.copyOf(appearance);
    }

    /** Случайный облик. Женщине — без бороды и с женским голосом, мужчине — с мужским. */
    public static Map<String, String> random(RandomSource random) {
        Map<String, String> map = new LinkedHashMap<>();
        for (Part part : Part.values()) {
            map.put(part.id(), part.options().get(random.nextInt(part.options().size())));
        }
        return matchSex(map);
    }

    /** Голос и борода под пол: сменили пол — голос того же пола, у женщины бороды нет. */
    public static Map<String, String> matchSex(Map<String, String> appearance) {
        Map<String, String> map = new LinkedHashMap<>(appearance);
        String sex = get(map, Part.SEX);
        if (!get(map, Part.VOICE).startsWith(sex + "_")) {
            map.put(Part.VOICE.id(), sex + "_1");
        }
        if ("female".equals(sex)) {
            map.put(Part.BEARD.id(), "none");
        }
        return map;
    }

    /** Строка, одинаковая у одинаковых обликов: ключ кэша собранных скинов. */
    public static String key(Map<String, String> appearance) {
        StringBuilder key = new StringBuilder();
        for (Part part : Part.values()) {
            key.append(get(appearance, part)).append('/');
        }
        return key.toString();
    }

    // ===== Цвета =====

    public static int skinColor(Map<String, String> appearance) {
        return SKIN_COLORS.get(get(appearance, Part.SKIN));
    }

    public static int eyeColor(Map<String, String> appearance) {
        return EYE_COLORS.get(get(appearance, Part.EYES));
    }

    /** Цвет волос с сединой по возрасту: в зрелые годы — чуть, в старости — наполовину. */
    public static int hairColor(Map<String, String> appearance) {
        int color = HAIR_COLORS.get(get(appearance, Part.HAIR_COLOR));
        return switch (get(appearance, Part.AGE)) {
            case "mature" -> mix(color, GREY_HAIR, 0.2F);
            case "old" -> mix(color, GREY_HAIR, 0.6F);
            default -> color;
        };
    }

    private static int mix(int from, int to, float share) {
        int result = 0;
        for (int shift = 0; shift <= 16; shift += 8) {
            int a = from >> shift & 0xFF;
            int b = to >> shift & 0xFF;
            result |= Math.round(a + (b - a) * share) << shift;
        }
        return result;
    }
}
