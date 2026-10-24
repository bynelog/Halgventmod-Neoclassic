package com.nrdhvnstd.halgventmod.name;

import javax.annotation.Nullable;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.Permissions;
import com.nrdhvnstd.halgventmod.client.RpNameClient;
import com.nrdhvnstd.halgventmod.registry.ModAttachments;
import com.nrdhvnstd.halgventmod.squad.SquadSync;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * RP-имя игрока: показывается вместо ника над головой, в чате, в списке игроков (Tab)
 * и в сообщениях игры (вход, выход, смерть). Настоящий ник остаётся для команд
 * и виден при наведении на имя в чате.
 *
 * Игрок выбирает имя при создании персонажа (CharacterCreation): только кириллица и римские числа из I, V, X.
 * Команда — только администрации, свободнее (латиница тоже можно):
 * /nick           — своё текущее имя
 * /nick <имя>     — сменить имя (кириллица, латиница, пробел, дефис, апостроф; 2–32 знака)
 * /nick reset     — вернуть настоящий ник
 */
@EventBusSubscriber(modid = HalgventMod.MODID)
public final class RpNames {
    public static final int MIN_LENGTH = 2;
    public static final int MAX_LENGTH = 32;

    /** Синхронизация: имя видно всем, кто видит игрока; после получения клиент обновляет его имя. */
    public static final AttachmentSyncHandler<String> SYNC = new AttachmentSyncHandler<>() {
        @Override
        public void write(RegistryFriendlyByteBuf buf, String name, boolean initialSync) {
            buf.writeUtf(name, MAX_LENGTH * 4);
        }

        @Override
        public String read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, String previousValue) {
            String name = buf.readUtf(MAX_LENGTH * 4);
            if (holder instanceof Player player) {
                RpNameClient.refreshLater(player); // имя ещё не записано — обновляем на следующем шаге
            }
            return name;
        }
    };

    private RpNames() {}

    /** RP-имя или пустая строка, если не задано. */
    public static String get(Player player) {
        return player.getData(ModAttachments.RP_NAME);
    }

    // ----- Подмена имени -----

    @SubscribeEvent
    public static void onNameFormat(PlayerEvent.NameFormat event) {
        String name = get(event.getEntity());
        if (!name.isEmpty()) {
            event.setDisplayname(Component.literal(name));
        }
    }

    @SubscribeEvent
    public static void onTabListNameFormat(PlayerEvent.TabListNameFormat event) {
        String name = get(event.getEntity());
        if (!name.isEmpty()) {
            event.setDisplayName(Component.literal(name));
        }
    }

    // ----- Команда -----

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("nick")
                .requires(source -> source.hasPermission(Permissions.ADMIN_LEVEL))
                .executes(context -> show(context.getSource().getPlayerOrException()))
                .then(Commands.literal("reset")
                        .executes(context -> set(context.getSource(), "")))
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(RpNames::setFromArgument)));
    }

    private static int show(ServerPlayer player) {
        String name = get(player);
        player.sendSystemMessage(name.isEmpty()
                ? Component.translatable("command.halgventmod.nick.none")
                : Component.translatable("command.halgventmod.nick.current", name));
        return 1;
    }

    private static int setFromArgument(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String name = normalize(StringArgumentType.getString(context, "name"));
        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
            context.getSource().sendFailure(Component.translatable("command.halgventmod.nick.length",
                    MIN_LENGTH, MAX_LENGTH));
            return 0;
        }
        if (!allowed(name)) {
            context.getSource().sendFailure(Component.translatable("command.halgventmod.nick.characters"));
            return 0;
        }
        return set(context.getSource(), name);
    }

    private static int set(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        apply(player, name);
        source.sendSuccess(() -> name.isEmpty()
                ? Component.translatable("command.halgventmod.nick.reset")
                : Component.translatable("command.halgventmod.nick.set", name), false);
        return 1;
    }

    /** Записать имя (пустое — настоящий ник) и разослать всем. */
    public static void apply(ServerPlayer player, String name) {
        player.setData(ModAttachments.RP_NAME, name);
        player.syncData(ModAttachments.RP_NAME);
        player.refreshDisplayName();
        player.refreshTabListName();
        SquadSync.changed(player.server); // имя в окне «Отряд» у товарищей
    }

    // ----- Проверка имени -----

    /** Пробелы по краям убраны, несколько пробелов подряд — один. */
    public static String normalize(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    /** Буквы кириллицы и латиницы, пробел, дефис, апостроф; начинается и кончается буквой. */
    static boolean allowed(String name) {
        if (!Character.isLetter(name.charAt(0)) || !Character.isLetter(name.charAt(name.length() - 1))) {
            return false;
        }
        return name.codePoints().allMatch(c -> c == ' ' || c == '-' || c == '\'' || c == '’'
                || Character.isLetter(c) && (Character.UnicodeScript.of(c) == Character.UnicodeScript.CYRILLIC
                        || Character.UnicodeScript.of(c) == Character.UnicodeScript.LATIN));
    }

    /**
     * Имя персонажа (геймдок 3.1): слова через пробел или дефис; слово — кириллица (апостроф внутри можно)
     * или римское число из I, V, X («Гюнтер III»). Первое слово — кириллица.
     * null — имя годится, иначе ключ ошибки: name_length или name_characters.
     */
    @Nullable
    public static String characterNameError(String name) {
        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
            return "name_length";
        }
        String[] words = name.split("[ -]", -1);
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.isEmpty()) {
                return "name_characters"; // два разделителя подряд или по краю
            }
            if (i > 0 && word.chars().allMatch(c -> c == 'I' || c == 'V' || c == 'X')) {
                continue;
            }
            if (!cyrillicLetter(word.codePointAt(0)) || !cyrillicLetter(word.codePointBefore(word.length()))
                    || !word.codePoints().allMatch(c -> cyrillicLetter(c) || c == '\'' || c == '’')) {
                return "name_characters";
            }
        }
        return null;
    }

    private static boolean cyrillicLetter(int c) {
        return Character.isLetter(c) && Character.UnicodeScript.of(c) == Character.UnicodeScript.CYRILLIC;
    }
}
