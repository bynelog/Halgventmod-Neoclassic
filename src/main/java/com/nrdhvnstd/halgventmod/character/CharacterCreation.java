package com.nrdhvnstd.halgventmod.character;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.Permissions;
import com.nrdhvnstd.halgventmod.armor.ArmorInventory;
import com.nrdhvnstd.halgventmod.armor.ArmorSlot;
import com.nrdhvnstd.halgventmod.armor.ModArmor;
import com.nrdhvnstd.halgventmod.name.RpNames;
import com.nrdhvnstd.halgventmod.network.CharacterCreatePayload;
import com.nrdhvnstd.halgventmod.network.CharacterOpenPayload;
import com.nrdhvnstd.halgventmod.network.CharacterResultPayload;
import com.nrdhvnstd.halgventmod.point.MapPoint;
import com.nrdhvnstd.halgventmod.point.MapPoints;
import com.nrdhvnstd.halgventmod.registry.ModAttachments;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Создание персонажа (геймдок 3.1): при первом входе игрок проходит 5 шагов — облик, предыстория (1 из 12,
 * даёт стартовый комплект), точка появления, итог, имя — и только потом входит в мир.
 * Окно — на клиенте (client/character/CharacterCreationScreen), здесь — сервер: кому открыть окно,
 * «прихожая» на время выбора и проверка выбранного.
 *
 * Прихожая: пока окно открыто, игрок стоит на месте, его нельзя ранить, сам он ничего не трогает.
 * Кто играл до появления создания персонажа, выбирает только облик и имя: снаряжение и место у него свои.
 * Точки появления — точки карты с галочкой «Точка появления»; пока их нет, персонаж начинает там, где стоит.
 *
 * /character reset <игроки> — создать персонажа заново, весь путь с комплектом (администрация)
 * /character edit <игроки>  — заново выбрать облик и имя (администрация)
 */
@EventBusSubscriber(modid = HalgventMod.MODID)
public final class CharacterCreation {
    private static final double ANCHOR_SLACK_SQR = 0.25; // на сколько можно отойти от места в прихожей (в квадрате)

    // Кто сейчас создаёт персонажа — и где стоит. Не сохраняется: при входе окно откроется снова
    private static final Map<UUID, Vec3> ANCHORS = new HashMap<>();

    private CharacterCreation() {}

    public static boolean creating(ServerPlayer player) {
        return ANCHORS.containsKey(player.getUUID());
    }

    // ===== Вход =====

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!player.hasData(ModAttachments.CHARACTER)) {
            // Первый вход с созданием персонажа: кто уже играл — только облик и имя
            boolean played = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) > 0;
            player.setData(ModAttachments.CHARACTER, new CharacterData(
                    played ? CharacterData.State.EDIT : CharacterData.State.NEW, Map.of(), ""));
            player.syncData(ModAttachments.CHARACTER);
        }
        if (!player.getData(ModAttachments.CHARACTER).created()) {
            open(player);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ANCHORS.remove(event.getEntity().getUUID());
    }

    /** Открыть игроку окно создания по его состоянию и поставить его в прихожую. */
    private static void open(ServerPlayer player) {
        CharacterData data = player.getData(ModAttachments.CHARACTER);
        boolean full = data.state() == CharacterData.State.NEW;
        ANCHORS.put(player.getUUID(), player.position());
        List<CharacterOpenPayload.Spawn> spawns = full
                ? spawnPoints(player.server).stream()
                        .map(point -> new CharacterOpenPayload.Spawn(point.id, point.name,
                                point.description.isEmpty() ? point.hoverText : point.description))
                        .toList()
                : List.of();
        PacketDistributor.sendToPlayer(player, new CharacterOpenPayload(full, data.appearance(), RpNames.get(player),
                full ? Backstories.all() : List.of(), spawns));
    }

    private static List<MapPoint> spawnPoints(MinecraftServer server) {
        return MapPoints.get(server).all().stream().filter(point -> point.spawn).toList();
    }

    // ===== Прихожая =====

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        Vec3 anchor = ANCHORS.get(player.getUUID());
        if (anchor != null && player.position().distanceToSqr(anchor) > ANCHOR_SLACK_SQR) {
            player.connection.teleport(anchor.x, anchor.y, anchor.z, player.getYRot(), player.getXRot());
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && creating(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player && creating(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer player && creating(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player && creating(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() instanceof ServerPlayer player && creating(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && creating(player)) {
            event.setCanceled(true);
        }
    }

    // ===== Готово =====

    /** Игрок подтвердил персонажа: проверить и применить. Ответ — CharacterResultPayload. */
    public static void finish(ServerPlayer player, CharacterCreatePayload payload) {
        CharacterData data = player.getExistingDataOrNull(ModAttachments.CHARACTER);
        if (data == null || data.created() || !creating(player)) {
            return;
        }
        boolean full = data.state() == CharacterData.State.NEW;
        Map<String, String> appearance = Appearance.validated(payload.appearance());
        if (appearance == null) {
            reply(player, "appearance");
            return;
        }
        String name = RpNames.normalize(payload.name());
        String nameError = RpNames.characterNameError(name);
        if (nameError != null) {
            reply(player, nameError);
            return;
        }
        Backstory backstory = null;
        MapPoint spawn = null;
        if (full) {
            backstory = Backstories.get(ResourceLocation.tryParse(payload.backstory()));
            if (backstory == null && !Backstories.all().isEmpty()) {
                reply(player, "backstory");
                return;
            }
            List<MapPoint> spawns = spawnPoints(player.server);
            if (!spawns.isEmpty()) {
                spawn = spawns.stream().filter(point -> point.id == payload.spawn()).findFirst().orElse(null);
                if (spawn == null) {
                    reply(player, "spawn");
                    return;
                }
            }
        }

        String backstoryId = backstory != null ? backstory.id().toString() : data.backstory();
        player.setData(ModAttachments.CHARACTER, new CharacterData(CharacterData.State.CREATED, appearance, backstoryId));
        player.syncData(ModAttachments.CHARACTER);
        ANCHORS.remove(player.getUUID());
        RpNames.apply(player, name);
        if (backstory != null) {
            giveKit(player, backstory);
        }
        if (spawn != null) {
            moveTo(player, spawn);
        }
        reply(player, "");
        HalgventMod.LOGGER.info("{} создал персонажа «{}»: {}, {}", player.getGameProfile().getName(), name,
                backstoryId.isEmpty() ? "без предыстории" : backstoryId,
                spawn != null ? "точка «" + spawn.name + "»" : "на месте");
    }

    private static void reply(ServerPlayer player, String error) {
        PacketDistributor.sendToPlayer(player, new CharacterResultPayload(error));
    }

    /** Комплект предыстории: броня — в свои ячейки, вещи — в свободные ячейки хотбара; что не влезло — в инвентарь. */
    private static void giveKit(ServerPlayer player, Backstory backstory) {
        ArmorInventory armor = player.getData(ModAttachments.ARMOR);
        for (ItemStack each : backstory.armor()) {
            ItemStack stack = each.copy();
            ArmorSlot slot = ModArmor.slotOf(stack);
            if (slot != null && armor.get(slot).isEmpty()) {
                armor.set(slot, stack);
            } else {
                give(player, stack);
            }
        }
        Inventory inventory = player.getInventory();
        for (ItemStack each : backstory.items()) {
            ItemStack stack = each.copy();
            int free = -1;
            for (int i = 0; i < Inventory.getSelectionSize(); i++) {
                if (inventory.getItem(i).isEmpty()) {
                    free = i;
                    break;
                }
            }
            if (free >= 0) {
                inventory.setItem(free, stack);
            } else {
                give(player, stack);
            }
        }
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    /** На поверхность у середины точки — как телепорт по карте. */
    private static void moveTo(ServerPlayer player, MapPoint point) {
        ResourceLocation dimension = ResourceLocation.tryParse(point.dimension);
        ServerLevel level = dimension == null ? null
                : player.server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
        if (level == null) {
            HalgventMod.LOGGER.warn("Точка появления «{}»: нет измерения {}", point.name, point.dimension);
            return;
        }
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, point.x, point.z); // загрузит чанк
        if (y <= level.getMinBuildHeight()) {
            y = point.y;
        }
        player.teleportTo(level, point.x + 0.5, y, point.z + 0.5, player.getYRot(), player.getXRot());
    }

    // ===== Команда =====

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("character")
                .requires(source -> source.hasPermission(Permissions.ADMIN_LEVEL))
                .then(Commands.literal("reset")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> restart(context.getSource(),
                                        EntityArgument.getPlayers(context, "targets"), CharacterData.State.NEW))))
                .then(Commands.literal("edit")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> restart(context.getSource(),
                                        EntityArgument.getPlayers(context, "targets"), CharacterData.State.EDIT)))));
    }

    private static int restart(CommandSourceStack source, Collection<ServerPlayer> players, CharacterData.State state) {
        String key = state == CharacterData.State.NEW ? "reset" : "edit";
        for (ServerPlayer player : players) {
            CharacterData data = player.getData(ModAttachments.CHARACTER);
            player.setData(ModAttachments.CHARACTER, new CharacterData(state, data.appearance(), data.backstory()));
            player.syncData(ModAttachments.CHARACTER);
            open(player);
            source.sendSuccess(() -> Component.translatable("command.halgventmod.character." + key,
                    player.getDisplayName()), true);
        }
        return players.size();
    }
}
