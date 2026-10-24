package com.nrdhvnstd.halgventmod.point;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.nrdhvnstd.halgventmod.banner.BannerEntry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Точка карты: общая для всех игроков, ставит её администратор. Видна всем на карте и на компасе;
 * у каждого игрока она либо открыта (он побывал внутри её границы), либо нет.
 * Граница — круг радиусом radius вокруг (x; z) или многоугольник по вершинам (xs; zs) в блоках мира.
 * Поля открыты: их правят редактор границы и окно точки на клиенте, проверяет сервер (MapPoints).
 */
public final class MapPoint {
    public enum Shape { CIRCLE, POLYGON }

    public static final int NAME_MAX = 32;
    public static final int ICON_MAX = 32;
    public static final int HOVER_TEXT_MAX = 256;   // ⚠️
    public static final int DESCRIPTION_MAX = 2000; // ⚠️
    public static final int INFO_MAX = 2000;        // ⚠️
    private static final Pattern ICON_ID = Pattern.compile("[a-z_]{0," + ICON_MAX + "}");
    public static final int MIN_RADIUS = 1;
    public static final int MAX_RADIUS = 4096;
    public static final int MIN_VERTICES = 3;
    public static final int MAX_VERTICES = 64;
    /** Сколько блоков от середины до вершин у нового многоугольника и у нового круга. */
    public static final int DEFAULT_SIZE = 16;

    /** 0 — ещё не создана (номер даёт сервер). */
    public int id;
    public String name = "";
    /** Измерение — ResourceLocation строкой (minecraft:overworld). */
    public String dimension = "";
    public int x;
    public int y;
    public int z;
    /** Значок после открытия — MapIcon.id() на клиенте; сервер хранит как есть. */
    public String icon = "";
    /** Цвет флага — имя красителя (для значка-знаменосца; к знамени из реестра отношения не имеет). */
    public String dye = "";
    /** Значок до открытия: "" — «?», иначе MapIcon.id(). Неизвестный клиенту — тоже «?». */
    public String hiddenIcon = "";
    /** Текст в окне у курсора, пока точка не открыта. */
    public String hoverText = "";
    /** Точка с описанием: открытая кликается и показывает панель со знаменем, описанием и сведениями. */
    public boolean described;
    /** id знамени из реестра знамён; "" — без знамени. */
    public String banner = "";
    public String description = "";
    /** Нижний блок «Сведения»; строка «Слово: текст» рисует слово тусклым. */
    public String info = "";
    /** Точка появления: новый персонаж может начать здесь (CharacterCreation). */
    public boolean spawn;
    public Shape shape = Shape.CIRCLE;
    public int radius = DEFAULT_SIZE;
    public List<Integer> xs = new ArrayList<>();
    public List<Integer> zs = new ArrayList<>();

    public MapPoint copy() {
        MapPoint copy = new MapPoint();
        copy.id = id;
        copy.name = name;
        copy.dimension = dimension;
        copy.x = x;
        copy.y = y;
        copy.z = z;
        copy.icon = icon;
        copy.dye = dye;
        copy.hiddenIcon = hiddenIcon;
        copy.hoverText = hoverText;
        copy.described = described;
        copy.banner = banner;
        copy.description = description;
        copy.info = info;
        copy.spawn = spawn;
        copy.shape = shape;
        copy.radius = radius;
        copy.xs = new ArrayList<>(xs);
        copy.zs = new ArrayList<>(zs);
        return copy;
    }

    /** Многоугольник по умолчанию — квадрат вокруг середины. */
    public void squareAround(int half) {
        xs = new ArrayList<>(List.of(x - half, x + half, x + half, x - half));
        zs = new ArrayList<>(List.of(z - half, z - half, z + half, z + half));
    }

    /** Сдвинуть точку вместе с границей. */
    public void moveBy(int dx, int dz) {
        x += dx;
        z += dz;
        for (int i = 0; i < xs.size(); i++) {
            xs.set(i, xs.get(i) + dx);
            zs.set(i, zs.get(i) + dz);
        }
    }

    /** Внутри ли границы место (px; pz) в блоках мира. */
    public boolean contains(double px, double pz) {
        if (shape == Shape.CIRCLE) {
            double dx = px - (x + 0.5);
            double dz = pz - (z + 0.5);
            return dx * dx + dz * dz <= (double) radius * radius;
        }
        // Луч вправо: нечётное число пересечений с рёбрами — внутри
        boolean inside = false;
        int count = xs.size();
        for (int i = 0, j = count - 1; i < count; j = i++) {
            double xi = xs.get(i) + 0.5;
            double zi = zs.get(i) + 0.5;
            double xj = xs.get(j) + 0.5;
            double zj = zs.get(j) + 0.5;
            if ((zi > pz) != (zj > pz) && px < (xj - xi) * (pz - zi) / (zj - zi) + xi) {
                inside = !inside;
            }
        }
        return inside;
    }

    /** Всё ли в порядке для сохранения: имя, размер, вершины. */
    public boolean valid() {
        if (name == null || name.isBlank() || name.length() > NAME_MAX || dimension.isEmpty()
                || icon.length() > 64 || dye.length() > 32 || !ICON_ID.matcher(hiddenIcon).matches()
                || hoverText.length() > HOVER_TEXT_MAX || description.length() > DESCRIPTION_MAX || info.length() > INFO_MAX
                || !(banner.isEmpty() || BannerEntry.validId(banner))) {
            return false;
        }
        if (shape == Shape.CIRCLE) {
            return radius >= MIN_RADIUS && radius <= MAX_RADIUS;
        }
        if (xs.size() != zs.size() || xs.size() < MIN_VERTICES || xs.size() > MAX_VERTICES) {
            return false;
        }
        for (int i = 0; i < xs.size(); i++) {
            if (Math.abs(xs.get(i) - x) > MAX_RADIUS || Math.abs(zs.get(i) - z) > MAX_RADIUS) {
                return false;
            }
        }
        return true;
    }

    // ===== Сеть =====

    public static final StreamCodec<FriendlyByteBuf, MapPoint> STREAM_CODEC = StreamCodec.of(
            (buf, point) -> point.write(buf), MapPoint::read);

    private void write(FriendlyByteBuf buf) {
        buf.writeVarInt(id);
        buf.writeUtf(name, NAME_MAX * 4);
        buf.writeUtf(dimension);
        buf.writeVarInt(x);
        buf.writeVarInt(y);
        buf.writeVarInt(z);
        buf.writeUtf(icon);
        buf.writeUtf(dye);
        buf.writeUtf(hiddenIcon, ICON_MAX);
        buf.writeUtf(hoverText, HOVER_TEXT_MAX * 4);
        buf.writeBoolean(described);
        buf.writeUtf(banner, BannerEntry.ID_MAX);
        buf.writeUtf(description, DESCRIPTION_MAX * 4);
        buf.writeUtf(info, INFO_MAX * 4);
        buf.writeBoolean(spawn);
        buf.writeEnum(shape);
        buf.writeVarInt(radius);
        int count = Math.min(xs.size(), MAX_VERTICES);
        buf.writeVarInt(count);
        for (int i = 0; i < count; i++) {
            buf.writeVarInt(xs.get(i));
            buf.writeVarInt(zs.get(i));
        }
    }

    private static MapPoint read(FriendlyByteBuf buf) {
        MapPoint point = new MapPoint();
        point.id = buf.readVarInt();
        point.name = buf.readUtf(NAME_MAX * 4);
        point.dimension = buf.readUtf();
        point.x = buf.readVarInt();
        point.y = buf.readVarInt();
        point.z = buf.readVarInt();
        point.icon = buf.readUtf();
        point.dye = buf.readUtf();
        point.hiddenIcon = buf.readUtf(ICON_MAX);
        point.hoverText = buf.readUtf(HOVER_TEXT_MAX * 4);
        point.described = buf.readBoolean();
        point.banner = buf.readUtf(BannerEntry.ID_MAX);
        point.description = buf.readUtf(DESCRIPTION_MAX * 4);
        point.info = buf.readUtf(INFO_MAX * 4);
        point.spawn = buf.readBoolean();
        point.shape = buf.readEnum(Shape.class);
        point.radius = buf.readVarInt();
        int count = Math.min(buf.readVarInt(), MAX_VERTICES);
        for (int i = 0; i < count; i++) {
            point.xs.add(buf.readVarInt());
            point.zs.add(buf.readVarInt());
        }
        return point;
    }

    // ===== Сохранение =====

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("id", id);
        tag.putString("name", name);
        tag.putString("dimension", dimension);
        tag.putIntArray("pos", new int[] {x, y, z});
        tag.putString("icon", icon);
        tag.putString("dye", dye);
        tag.putString("hidden_icon", hiddenIcon);
        tag.putString("hover_text", hoverText);
        tag.putBoolean("described", described);
        tag.putString("banner", banner);
        tag.putString("description", description);
        tag.putString("info", info);
        tag.putBoolean("spawn", spawn);
        tag.putString("shape", shape.name());
        tag.putInt("radius", radius);
        tag.put("xs", new IntArrayTag(xs.stream().mapToInt(Integer::intValue).toArray()));
        tag.put("zs", new IntArrayTag(zs.stream().mapToInt(Integer::intValue).toArray()));
        return tag;
    }

    public static MapPoint load(CompoundTag tag) {
        MapPoint point = new MapPoint();
        point.id = tag.getInt("id");
        point.name = tag.getString("name");
        point.dimension = tag.getString("dimension");
        int[] pos = tag.getIntArray("pos");
        if (pos.length == 3) {
            point.x = pos[0];
            point.y = pos[1];
            point.z = pos[2];
        }
        point.icon = tag.getString("icon");
        point.dye = tag.getString("dye");
        // Нет ключа (мир до этих полей) — пустая строка или false, как по умолчанию
        point.hiddenIcon = tag.getString("hidden_icon");
        point.hoverText = tag.getString("hover_text");
        point.described = tag.getBoolean("described");
        point.banner = tag.getString("banner");
        point.description = tag.getString("description");
        point.info = tag.getString("info");
        point.spawn = tag.getBoolean("spawn");
        point.shape = "POLYGON".equals(tag.getString("shape")) ? Shape.POLYGON : Shape.CIRCLE;
        point.radius = tag.getInt("radius");
        for (int value : tag.getIntArray("xs")) {
            point.xs.add(value);
        }
        for (int value : tag.getIntArray("zs")) {
            point.zs.add(value);
        }
        return point;
    }
}
