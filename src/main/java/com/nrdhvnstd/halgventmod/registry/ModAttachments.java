package com.nrdhvnstd.halgventmod.registry;

import java.util.List;

import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.character.CharacterData;
import com.nrdhvnstd.halgventmod.brewing.Intoxication;
import com.nrdhvnstd.halgventmod.armor.ArmorInventory;
import com.nrdhvnstd.halgventmod.draw.Drawing;
import com.nrdhvnstd.halgventmod.gear.GearInventory;
import com.nrdhvnstd.halgventmod.food.HungerData;
import com.nrdhvnstd.halgventmod.food.ThirstData;
import com.mojang.serialization.Codec;
import com.nrdhvnstd.halgventmod.lock.ChunkLocks;
import com.nrdhvnstd.halgventmod.name.RpNames;
import com.nrdhvnstd.halgventmod.needs.NeedsSync;
import com.nrdhvnstd.halgventmod.needs.StaminaData;
import com.nrdhvnstd.halgventmod.pickup.Pickup;
import com.nrdhvnstd.halgventmod.point.PointDiscovery;
import com.nrdhvnstd.halgventmod.storage.Quiver;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Данные, прикреплённые к сущностям и чанкам. */
public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, HalgventMod.MODID);

    // Текущий штраф за перегруз (0.2 = 20%). Пересчитывается каждые полсекунды, поэтому не сохраняется.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> OVERWEIGHT_PENALTY =
            ATTACHMENTS.register("overweight_penalty", () -> AttachmentType.builder(() -> 0.0F).build());

    // Голод 0–100 (ванильный отключён). Сохраняется; после смерти — снова полный.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HungerData>> HUNGER =
            ATTACHMENTS.register("hunger", () -> AttachmentType.builder(HungerData::new)
                    .serialize(HungerData.CODEC)
                    .build());

    // Жажда. Сохраняется; после смерти — снова полная, как голод.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ThirstData>> THIRST =
            ATTACHMENTS.register("thirst", () -> AttachmentType.builder(ThirstData::new)
                    .serialize(ThirstData.CODEC)
                    .build());

    // Выносливость. Сохраняется; после смерти — снова полная.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<StaminaData>> STAMINA =
            ATTACHMENTS.register("stamina", () -> AttachmentType.builder(StaminaData::new)
                    .serialize(StaminaData.CODEC)
                    .build());

    // Что последним отправлено клиенту о голоде, жажде и выносливости. Не сохраняется.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<NeedsSync.Sent>> NEEDS_SENT =
            ATTACHMENTS.register("needs_sent", () -> AttachmentType.builder(NeedsSync.Sent::new).build());

    // Замки на дверях и сундуках чанка. Сохраняются вместе с чанком.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ChunkLocks>> CHUNK_LOCKS =
            ATTACHMENTS.register("chunk_locks", () -> AttachmentType.builder(ChunkLocks::new)
                    .serialize(ChunkLocks.CODEC)
                    .build());

    // Надетая броня, 12 ячеек. Сохраняется; видна самому игроку и всем, кто его видит.
    // При смерти выпадает (ArmorEvents), с keepInventory переносится в новое тело.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ArmorInventory>> ARMOR =
            ATTACHMENTS.register("armor", () -> AttachmentType.serializable(() -> new ArmorInventory())
                    .copyOnDeath()
                    .sync(ArmorInventory.SYNC)
                    .build());

    // Надетое снаряжение, 9 ячеек: украшения, пояс и что на нём, сумка на спине — вместе с содержимым сумок.
    // Сохраняется; видно самому игроку и всем, кто его видит. При смерти выпадает (GearEvents).
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<GearInventory>> GEAR =
            ATTACHMENTS.register("gear", () -> AttachmentType.serializable(() -> new GearInventory())
                    .copyOnDeath()
                    .sync(GearInventory.SYNC)
                    .build());

    // Доставание предмета в основную руку (Drawing). Не сохраняется: после входа предмет уже в руке.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Drawing.State>> DRAW =
            ATTACHMENTS.register("draw", () -> AttachmentType.builder(Drawing.State::new).build());

    // Стрелы колчана, отданные луку в этом тике (Quiver). Не сохраняется.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Quiver.Lent>> QUIVER_LENT =
            ATTACHMENTS.register("quiver_lent", () -> AttachmentType.builder(Quiver.Lent::new).build());

    // Когда игрок последний раз подбирал предмет (Pickup) — для задержки между подборами. Не сохраняется.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Pickup.State>> PICKUP =
            ATTACHMENTS.register("pickup", () -> AttachmentType.builder(Pickup.State::new).build());

    // RP-имя игрока (/nick); пустое — настоящий ник. Сохраняется, переживает смерть, видно всем.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<String>> RP_NAME =
            ATTACHMENTS.register("rp_name", () -> AttachmentType.builder(() -> "")
                    .serialize(Codec.STRING)
                    .copyOnDeath()
                    .sync(RpNames.SYNC)
                    .build());

    // Персонаж: создан ли, облик и предыстория (CharacterCreation). Сохраняется, переживает смерть; облик видят все.
    // Нет вложения — игрок ещё не входил после появления создания персонажа.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CharacterData>> CHARACTER =
            ATTACHMENTS.register("character", () -> AttachmentType.builder(() -> CharacterData.NONE)
                    .serialize(CharacterData.CODEC)
                    .copyOnDeath()
                    .sync(CharacterData.SYNC)
                    .build());

    // Открытые игроком точки карты (MapPoints). Сохраняется, переживает смерть; клиенту — PointsDiscoveredPayload.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PointDiscovery>> POINT_DISCOVERY =
            ATTACHMENTS.register("point_discovery", () -> AttachmentType.builder(() -> new PointDiscovery())
                    .serialize(PointDiscovery.CODEC)
                    .copyOnDeath()
                    .build());

    // Последние съеденные блюда (Diet: приедается одно и то же). Сохраняется, после смерти — заново
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<List<String>>> RECENT_MEALS =
            ATTACHMENTS.register("recent_meals", () -> AttachmentType.<List<String>>builder(() -> List.of())
                    .serialize(Codec.STRING.listOf())
                    .build());

    // Опьянение и похмелье (brewing/Intoxication). Сохраняется; после смерти — трезв
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Intoxication.Data>> INTOXICATION =
            ATTACHMENTS.register("intoxication", () -> AttachmentType.builder(() -> Intoxication.Data.NONE)
                    .serialize(Intoxication.Data.CODEC)
                    .build());

    private ModAttachments() {}

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }
}