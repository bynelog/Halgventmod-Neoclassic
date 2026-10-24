package com.nrdhvnstd.halgventmod.datagen;

import java.util.Map;

import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.armor.ArmorSlot;
import com.nrdhvnstd.halgventmod.gear.GearSlot;
import com.nrdhvnstd.halgventmod.item.Category;
import com.nrdhvnstd.halgventmod.item.Gender;
import com.nrdhvnstd.halgventmod.item.ModLore;
import com.nrdhvnstd.halgventmod.item.ModTraits;
import com.nrdhvnstd.halgventmod.leather.Animals;
import com.nrdhvnstd.halgventmod.leather.Solution;
import com.nrdhvnstd.halgventmod.leather.Tannage;
import com.nrdhvnstd.halgventmod.registry.ModEffects;
import com.nrdhvnstd.halgventmod.registry.ModItems;
import com.nrdhvnstd.halgventmod.registry.ModSounds;
import com.nrdhvnstd.halgventmod.sound.Voices;
import com.nrdhvnstd.halgventmod.world.Calendar;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;

/** Языковые файлы ru_ru и en_us. Все переводы мода живут здесь. */
public class ModLanguageProvider extends LanguageProvider {
    private final boolean russian;

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, HalgventMod.MODID, locale);
        this.russian = locale.equals("ru_ru");
    }

    @Override
    protected void addTranslations() {
        // Вкладки: по одной на категорию
        for (Category category : Category.values()) {
            add(category.langKey(), russian ? category.nameRu() : category.nameEn());
        }

        // Названия предметов мода
        for (ModItems.Definition def : ModItems.definitions()) {
            add(def.item().get(), russian ? def.nameRu() : def.nameEn());
        }

        // Описания: и предметов мода, и ванильных, и чужих
        for (Map.Entry<ResourceLocation, ModLore.Entry> lore : ModLore.entries().entrySet()) {
            add(ModLore.langKey(lore.getKey()),
                    ModLore.markup(russian ? lore.getValue().ru() : lore.getValue().en()));
        }

        // Признаки во всех родах (в английском формы одинаковые)
        for (ModTraits.Trait trait : ModTraits.all()) {
            for (Gender gender : Gender.values()) {
                add(ModTraits.langKey(trait.id(), gender), russian ? trait.ru(gender) : trait.en());
            }
        }

        // Вес
        add("weight.halgventmod.grams", russian ? "%s гр" : "%s g");
        add("weight.halgventmod.kilograms", russian ? "%s кг" : "%s kg");
        add("weight.halgventmod.decimal", russian ? "," : ".");
        add("tooltip.halgventmod.weight", russian ? "Вес: %s" : "Weight: %s");
        add("hud.halgventmod.carry", "%s / %s");

        // Компас вверху экрана: стороны света
        String[] compassRu = {"С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "З", "СЗ"};
        String[] compassEn = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        String[] compassKeys = {"n", "ne", "e", "se", "s", "sw", "w", "nw"};
        for (int i = 0; i < compassKeys.length; i++) {
            add("hud.halgventmod.compass." + compassKeys[i], russian ? compassRu[i] : compassEn[i]); // CompassHud.langKey
        }
        add("attribute.name.halgventmod.carry_capacity", russian ? "Грузоподъёмность" : "Carry Capacity");

        // Броня: названия слотов и кнопка каталога в инвентаре
        for (ArmorSlot slot : ArmorSlot.values()) {
            add(slot.langKey(), russian ? slot.nameRu() : slot.nameEn());
        }
        add("gui.halgventmod.catalog", russian ? "Каталог" : "Catalog");
        add("gui.halgventmod.clear_inventory",
                russian ? "Shift+щелчок — очистить весь инвентарь" : "Shift+click to clear the whole inventory");
        add("tooltip.halgventmod.armor_slot", russian ? "Слот: %s" : "Slot: %s");
        add("tooltip.halgventmod.protection", russian ? "Защита: %s, твёрдость %s" : "Protection: %s, toughness %s");
        add("tooltip.halgventmod.visor_raised", russian ? "Забрало поднято" : "Visor raised");
        add("tooltip.halgventmod.visor_lowered", russian ? "Забрало опущено" : "Visor lowered");
        add("message.halgventmod.hood_over_helmet",
                russian ? "Капюшон не надеть поверх этого шлема" : "The hood won't fit over this helmet");
        add("message.halgventmod.remove_hood_first", russian ? "Сначала снимите капюшон" : "Take off the hood first");
        add("message.halgventmod.arming_cap_first",
                russian ? "Шлем надевается только поверх подшлемника" : "A helmet goes on only over an arming cap");
        add("message.halgventmod.remove_helmet_first", russian ? "Сначала снимите шлем" : "Take off the helmet first");

        // Снаряжение и хранилище (концепт инвентаря)
        for (GearSlot slot : GearSlot.values()) {
            add(slot.langKey(), russian ? slot.nameRu() : slot.nameEn());
        }
        add("gear_slot.halgventmod.pouch", russian ? "Подсумок" : "Pouch"); // подсказка вещи (GearTooltip)
        add("message.halgventmod.belt_first", russian ? "Сначала наденьте пояс" : "Put on a belt first");
        add("message.halgventmod.remove_belt_items_first",
                russian ? "Сначала снимите то, что висит на поясе" : "First take off what hangs from the belt");
        add("message.halgventmod.no_room", russian ? "Некуда положить" : "No room for it");
        add("gui.halgventmod.storage.back", russian ? "Спина" : "Back");
        add("gui.halgventmod.storage.pouch_1", russian ? "Подсумок 1" : "Pouch 1");
        add("gui.halgventmod.storage.pouch_2", russian ? "Подсумок 2" : "Pouch 2");
        add("gui.halgventmod.storage.pockets", russian ? "Карманы" : "Pockets");
        add("gui.halgventmod.storage.locked.back", russian ? "Наденьте рюкзак или сумку" : "Put on a backpack or a bag");
        add("gui.halgventmod.storage.locked.pouch", russian ? "Наденьте пояс и подсумок" : "Put on a belt and a pouch");
        add("tooltip.halgventmod.capacity", russian ? "Вместимость: %s" : "Capacity: %s");
        add("tooltip.halgventmod.contents", russian ? "Внутри: %s из %s" : "Inside: %s of %s");
        add("tooltip.halgventmod.carry_bonus", russian ? "Грузоподъёмность: +%s, пока надето" : "Carry capacity: +%s while worn");
        add("tooltip.halgventmod.gear_slot", russian ? "Носится: %s" : "Worn: %s");
        add("gui.halgventmod.load.none", russian ? "ЛЕГКО" : "LIGHT");
        add("gui.halgventmod.load.burden", russian ? "ОБУЗА" : "BURDENED");
        add("gui.halgventmod.load.encumbered", russian ? "СКОВАННОСТЬ" : "ENCUMBERED");
        add("gui.halgventmod.load.overloaded", russian ? "ТЯЖЕСТЬ" : "OVERLOADED");

        // Отладка: хитбоксы (F3+B) — только администраторам
        add("debug.halgventmod.hitboxes.no_permission",
                russian ? "Хитбоксы доступны только администраторам" : "Hitboxes are available to admins only");

        // Ники над головами игроков (/nametags)
        add("command.halgventmod.nametags.current", russian ? "Ники над головами: %s" : "Name tags: %s");
        add("command.halgventmod.nametags.set",
                russian ? "Ники над головами теперь: %s" : "Name tags are now: %s");
        add("command.halgventmod.nametags.mode.off", russian ? "отключены" : "off");
        add("command.halgventmod.nametags.mode.admins",
                russian ? "видны только администраторам" : "visible to admins only");
        add("command.halgventmod.nametags.mode.all", russian ? "видны всем" : "visible to everyone");

        // Каналы чата
        add("chat.halgventmod.global", russian ? "[Глобальный] %s: %s" : "[Global] %s: %s");
        add("chat.halgventmod.local", russian ? "[Локальный] %s: %s" : "[Local] %s: %s");
        add("chat.halgventmod.emote", russian ? "[Локальный] * %s %s" : "[Local] * %s %s");
        add("chat.halgventmod.global_admins_only",
                russian ? "В глобальный чат («!» в начале) пишут только администраторы" : "Only admins can write to the global chat (\"!\" prefix)");
        add("chat.halgventmod.nobody_heard", russian ? "Рядом никого — вас никто не услышал" : "Nobody is near enough to hear you");

        // Отряды (/squad)
        squad("none", "Вы не в отряде. Создать: /squad create <название>", "You are not in a squad. Create one: /squad create <name>");
        squad("info.header", "Отряд «%s»", "Squad \"%s\"");
        squad("info.leader", "Лидер: %s", "Leader: %s");
        squad("info.members", "Участники (%s): %s", "Members (%s): %s");
        squad("already_in", "Вы уже в отряде «%s»", "You are already in squad \"%s\"");
        squad("name_length", "Название отряда — от %s до %s знаков", "The squad name must be %s to %s characters long");
        squad("name_characters", "В названии можно использовать буквы (кириллица, латиница), цифры, пробел, дефис и апостроф",
                "Only letters (Cyrillic, Latin), digits, spaces, hyphens and apostrophes are allowed");
        squad("name_taken", "Отряд «%s» уже есть", "Squad \"%s\" already exists");
        squad("not_in", "Вы не в отряде", "You are not in a squad");
        squad("not_leader", "Это может только лидер отряда", "Only the squad leader can do this");
        squad("invite_self", "Нельзя пригласить самого себя", "You can't invite yourself");
        squad("target_in_squad", "%s уже в отряде", "%s is already in a squad");
        squad("invite_sent", "Приглашение отправлено: %s. Оно действует пять минут", "Invitation sent to %s. It lasts five minutes");
        squad("invited", "%s приглашает вас в отряд «%s».", "%s invites you to squad \"%s\".");
        squad("button.accept", "[Принять]", "[Accept]");
        squad("button.decline", "[Отклонить]", "[Decline]");
        squad("no_invite", "У вас нет приглашений в отряд", "You have no squad invitations");
        squad("joined", "%s вступает в отряд «%s»", "%s joins squad \"%s\"");
        squad("declined", "%s отклоняет приглашение в отряд", "%s declines the squad invitation");
        squad("leader_cant_leave", "Лидер не может просто уйти: передайте лидерство (/squad leader <игрок>) или распустите отряд (/squad disband)",
                "The leader can't just leave: pass leadership (/squad leader <player>) or disband the squad (/squad disband)");
        squad("you_left", "Вы покинули отряд «%s»", "You left squad \"%s\"");
        squad("left", "%s покидает отряд", "%s leaves the squad");
        squad("kick_self", "Себя исключить нельзя: /squad leave или /squad disband", "You can't kick yourself: /squad leave or /squad disband");
        squad("not_member", "В отряде нет игрока %s", "There is no %s in the squad");
        squad("kicked", "%s исключён из отряда", "%s was kicked from the squad");
        squad("you_kicked", "Вас исключили из отряда «%s»", "You were kicked from squad \"%s\"");
        squad("new_leader", "Новый лидер отряда: %s", "New squad leader: %s");
        squad("disbanded", "Отряд «%s» распущен", "Squad \"%s\" has been disbanded");
        squad("chat", "[Отряд] %s: %s", "[Squad] %s: %s");
        squad("chat_not_in", "Вы не в отряде — писать в чат отряда некому", "You are not in a squad — nobody to talk to");
        squad("created_title", "Создан отряд", "Squad founded");
        squad("banner_too_many", "На знамени не больше %s узоров", "A banner can have at most %s patterns");
        squad("player_offline", "Игрок уже не в сети", "The player is no longer online");
        squad("renamed", "Отряд теперь называется «%s»", "The squad is now called \"%s\"");
        squad("rank_leader", "Звание лидера — «Лидер»", "The leader's rank is always \"Leader\"");
        squad("rank_length", "Звание — не длиннее %s знаков", "A rank can be at most %s characters long");
        squad("friendly_fire_on", "Лидер разрешил урон по своим", "The leader allowed friendly fire");
        squad("friendly_fire_off", "Лидер запретил урон по своим", "The leader disabled friendly fire");
        squad("map_mode", "Отображение отряда на карте: %s", "Squad map display: %s");
        squad("which_invite", "Приглашений несколько — укажите отряд: /squad accept <отряд>", "You have several invitations — name the squad: /squad accept <squad>");

        // Окно «Отряд»
        squadGui("title", "Отряд", "Squad");
        squadGui("invites", "Приглашения в отряд", "Squad invitations");
        squadGui("invites_count", "Приглашения в отряд (%s)", "Squad invitations (%s)");
        squadGui("create", "Создать отряд", "Create squad");
        squadGui("column.squad", "Отряд", "Squad");
        squadGui("column.inviter", "Пригласил", "Invited by");
        squadGui("no_invites", "Приглашений нет", "No invitations");
        squadGui("rename", "Переименовать отряд", "Rename squad");
        squadGui("edit_banner", "Изменить знамя отряда", "Change squad banner");
        squadGui("members", "Список игроков %s/%s", "Players %s/%s");
        squadGui("rank.leader", "Лидер", "Leader");
        squadGui("rank.member", "Участник", "Member");
        squadGui("friendly_fire.on", "Урон по своим включен", "Friendly fire is on");
        squadGui("friendly_fire.off", "Урон по своим выключен", "Friendly fire is off");
        squadGui("invite_members", "Пригласить участников", "Invite players");
        squadGui("disband", "Распустить отряд", "Disband squad");
        squadGui("disband.confirm", "Вы уверены, что хотите распустить отряд?", "Are you sure you want to disband the squad?");
        squadGui("leave", "Покинуть отряд", "Leave squad");
        squadGui("leave.confirm", "Вы уверены, что хотите покинуть отряд?", "Are you sure you want to leave the squad?");
        squadGui("menu.rank", "Изменить звание", "Change rank");
        squadGui("menu.kick", "Выгнать из отряда", "Kick from squad");
        squadGui("menu.leader", "Сделать лидером", "Make leader");
        squadGui("kick.confirm", "Выгнать %s из отряда?", "Kick %s from the squad?");
        squadGui("leader.confirm", "Сделать %s лидером отряда? Вы станете обычным участником", "Make %s the squad leader? You will become a regular member");
        squadGui("save", "Сохранить", "Save");
        squadGui("name", "Название отряда", "Squad name");
        squadGui("banner", "Знамя отряда", "Squad banner");
        squadGui("banner.hint", "Выберите цвет, затем узор. Щелчок по слою — перекрасить его", "Pick a color, then a pattern. Click a layer to recolor it");
        squadGui("banner.layers", "Слои %s/%s", "Layers %s/%s");
        squadGui("banner.base", "Основа: %s", "Base: %s");
        squadGui("invited", "Приглашён", "Invited");
        squadGui("invited_mark", "приглашён", "invited");
        squadGui("invite_to_squad", "Пригласить в отряд", "Invite to squad");
        squadGui("map.tooltip", "Отображение на карте: %s", "Shown on map: %s");
        squadGui("map.off", "отключено", "off");
        squadGui("map.ranks", "включено только для избранных рангов", "only for chosen ranks");
        squadGui("map.all", "включено для всех", "for everyone");
        squadGui("map.ranks_list", "Ранги: %s", "Ranks: %s");
        squadGui("map.ranks.none", "не выбраны", "none chosen");
        squadGui("map.hint", "ЛКМ — сменить режим, ПКМ — выбрать ранги", "LMB — change mode, RMB — choose ranks");
        squadGui("map.ranks_title", "Кому видны товарищи на карте", "Who sees squad members on the map");
        squadGui("map.ranks_hint", "Лидеру видно всегда", "The leader always sees them");
        squadGui("nobody_to_invite", "Все игроки в сети уже в отрядах", "Everyone online is already in a squad");
        squadGui("rank_of", "Звание: %s", "Rank: %s");
        squadGui("rank.hint", "Пустое поле — звание «Участник»", "Leave empty for \"Member\"");

        // RP-имя (/nick)
        add("command.halgventmod.nick.none",
                russian ? "У вас нет RP-имени. Задать: /nick <имя>" : "You have no RP name. Set one: /nick <name>");
        add("command.halgventmod.nick.current", russian ? "Ваше RP-имя: %s" : "Your RP name: %s");
        add("command.halgventmod.nick.set", russian ? "Теперь вас зовут: %s" : "Your name is now: %s");
        add("command.halgventmod.nick.reset", russian ? "RP-имя снято, виден настоящий ник" : "RP name removed, your real name is shown");
        add("command.halgventmod.nick.length",
                russian ? "Имя должно быть от %s до %s знаков" : "The name must be %s to %s characters long");
        add("command.halgventmod.nick.characters",
                russian ? "В имени можно использовать только буквы (кириллица, латиница), пробел, дефис и апостроф; начинаться и заканчиваться оно должно буквой"
                        : "Only letters (Cyrillic, Latin), spaces, hyphens and apostrophes are allowed; the name must start and end with a letter");

        // Срок годности
        add("tooltip.halgventmod.spoils_in", russian ? "Испортится через: %s сут." : "Spoils in: %s d.");
        add("tooltip.halgventmod.spoiled", russian ? "Испорчено" : "Spoiled");

        // Правила фермерства (/gamerule и экран «Правила игры»)
        add("gamerule.farmlandTrampling", russian ? "Вытаптывание пашни" : "Farmland trampling");
        add("gamerule.farmlandDriesToDirt", russian ? "Сухая пашня превращается в землю" : "Dry farmland turns to dirt");
        add("gamerule.cropsNeedWater", russian ? "Растениям нужна сырая пашня" : "Crops need wet farmland");

        // Календарь: дни недели, месяцы, сезоны
        for (int i = 0; i < Calendar.DAYS_PER_WEEK; i++) {
            add(Calendar.weekdayKey(i + 1), russian ? Calendar.WEEKDAYS_RU[i] : Calendar.WEEKDAYS_EN[i]);
        }
        for (int i = 0; i < Calendar.MONTHS_PER_YEAR; i++) {
            add(Calendar.monthKey(i + 1), russian ? Calendar.MONTHS_RU[i] : Calendar.MONTHS_EN[i]);
            add(Calendar.monthKey(i + 1) + ".of", russian ? Calendar.MONTHS_RU_OF[i] : Calendar.MONTHS_EN[i]);
        }
        for (Calendar.Season season : Calendar.Season.values()) {
            add(season.langKey(), russian ? season.nameRu : season.nameEn);
        }
        add("calendar.halgventmod.dawn.title", russian ? "%1$s, %2$s %3$s" : "%1$s, %2$s of %3$s");
        add("calendar.halgventmod.dawn.subtitle", russian ? "%1$s, %2$s г." : "%1$s, year %2$s");
        add("calendar.halgventmod.date", russian ? "%1$s %2$s %3$s г." : "%1$s of %2$s, year %3$s");
        add("calendar.halgventmod.command.date", russian
                ? "Сегодня: %1$s, %2$s %3$s %4$s г., %6$s. %5$s."
                : "Today: %1$s, %2$s of %3$s, year %4$s, %6$s. %5$s.");
        add("calendar.halgventmod.command.jumped", russian
                ? "Время промотано вперёд: %1$s, %2$s" : "Time moved forward: %1$s, %2$s");
        add("calendar.halgventmod.command.rewound", russian
                ? "Время переведено назад: %1$s, %2$s" : "Time moved back: %1$s, %2$s");
        add("calendar.halgventmod.command.time_set_blocked", russian
                ? "Команда /time set отключена: она сбила бы календарь. Используйте /calendar time, /calendar skip или /calendar goto"
                : "/time set is disabled: it would break the calendar. Use /calendar time, /calendar skip or /calendar goto");
        add("calendar.halgventmod.command.too_early", russian
                ? "Раньше начала мира (%s) нельзя" : "Cannot go before the start of the world (%s)");
        add("calendar.halgventmod.command.event_added", russian ? "Событие на %1$s: %2$s" : "Event on %1$s: %2$s");
        add("calendar.halgventmod.command.events_removed", russian ? "События на %s удалены" : "Events on %s removed");
        add("calendar.halgventmod.command.no_events", russian ? "На %s событий нет" : "No events on %s");
        add("calendar.halgventmod.command.daylight", russian
                ? "День: %1$s ч %2$s мин, ночь: %3$s ч %4$s мин"
                : "Day: %1$sh %2$sm, night: %3$sh %4$sm");

        // Окно календаря и клавиша
        add("key.categories.halgventmod", "Halgventmod: Neoclassic");
        add("key.halgventmod.calendar", russian ? "Календарь" : "Calendar");
        add("key.halgventmod.character", russian ? "Персонаж" : "Character");
        add("key.halgventmod.map", russian ? "Карта" : "Map");
        add("key.halgventmod.squad", russian ? "Отряд" : "Squad");
        add("key.halgventmod.codex", russian ? "Кодекс" : "Codex");

        // Вкладки над окнами игрока
        add("gui.halgventmod.tab.inventory", russian ? "Инвентарь" : "Inventory");
        add("gui.halgventmod.tab.character", russian ? "Персонаж" : "Character");
        add("gui.halgventmod.tab.map", russian ? "Карта" : "Map");
        add("gui.halgventmod.tab.squad", russian ? "Отряд" : "Squad");
        add("gui.halgventmod.tab.codex", russian ? "Кодекс" : "Codex");
        add("gui.halgventmod.tab.hint", "%s (%s)");
        add("gui.halgventmod.tab.wip", russian ? "Раздел в разработке" : "Coming soon");

        // Окно «Карта»
        add("gui.halgventmod.map.cursor", "X: %s   Z: %s");
        // Меню правого клика по карте
        add("gui.halgventmod.map.menu.add_waypoint", russian ? "Поставить метку" : "Add Waypoint");
        add("gui.halgventmod.map.menu.edit_waypoint", russian ? "Изменить метку" : "Edit Waypoint");
        add("gui.halgventmod.map.menu.add_text", russian ? "Добавить надпись" : "Add Inscription");
        add("gui.halgventmod.map.menu.edit_text", russian ? "Изменить надпись" : "Edit Inscription");
        add("gui.halgventmod.map.menu.delete_text", russian ? "Удалить надпись" : "Delete Inscription");
        add("gui.halgventmod.map.menu.teleport", russian ? "Телепортация" : "Teleport");
        // Окно надписи на карте
        add("gui.halgventmod.map.text.new", russian ? "Новая надпись" : "New Inscription");
        add("gui.halgventmod.map.text.edit", russian ? "Надпись" : "Inscription");
        add("gui.halgventmod.map.text.text", russian ? "Текст" : "Text");
        add("gui.halgventmod.map.text.size", russian ? "Размер: %s" : "Size: %s");
        add("gui.halgventmod.map.text.color", russian ? "Цвет" : "Color");
        add("gui.halgventmod.map.tool.move", russian ? "Рука" : "Hand");
        add("gui.halgventmod.map.tool.draw", russian ? "Карандаш" : "Pencil");
        add("gui.halgventmod.map.tool.style", russian ? "Стиль" : "Style");

        // Карта: рисование
        add("gui.halgventmod.map.draw.color", russian ? "Цвет" : "Color");
        add("gui.halgventmod.map.draw.brush", russian ? "Кисть" : "Brush");
        add("gui.halgventmod.map.draw.brush_size", russian ? "%s бл." : "%s blocks");
        add("gui.halgventmod.map.draw.hint.draw", russian ? "ЛКМ: рисовать" : "LMB: draw");
        add("gui.halgventmod.map.draw.hint.erase", russian ? "ПКМ: стирать" : "RMB: erase");
        add("gui.halgventmod.map.draw.hint.pan", russian ? "СКМ: двигать карту" : "MMB: move the map");
        add("gui.halgventmod.map.draw.hint.undo", russian ? "Ctrl+Z: отменить штрих" : "Ctrl+Z: undo a stroke");
        add("gui.halgventmod.map.draw.clear", russian ? "Удалить все пометки" : "Erase All Drawings");
        add("gui.halgventmod.map.draw.clear_confirm", russian
                ? "Все рисунки на карте этого мира и измерения пропадут. Отменить будет нельзя."
                : "All drawings on the map of this world and dimension will be lost. This cannot be undone.");

        // Карта: стиль
        add("gui.halgventmod.map.style.base", russian ? "Основа" : "Base");
        add("gui.halgventmod.map.style.base.vanilla", russian ? "Ваниль" : "Vanilla");
        add("gui.halgventmod.map.style.base.parchment", russian ? "Пергамент" : "Parchment");
        add("gui.halgventmod.map.style.base.engraving", russian ? "Гравюра" : "Engraving");
        add("gui.halgventmod.map.style.base.atlas", russian ? "Атлас" : "Atlas");
        add("gui.halgventmod.map.style.group.color", russian ? "Цвет" : "Color");
        add("gui.halgventmod.map.style.group.relief", russian ? "Рельеф" : "Relief");
        add("gui.halgventmod.map.style.group.paper", russian ? "Бумага" : "Paper");
        add("gui.halgventmod.map.style.saturation", russian ? "Насыщенность" : "Saturation");
        add("gui.halgventmod.map.style.contrast", russian ? "Контраст" : "Contrast");
        add("gui.halgventmod.map.style.brightness", russian ? "Яркость" : "Brightness");
        add("gui.halgventmod.map.style.sepia", russian ? "Сепия" : "Sepia");
        add("gui.halgventmod.map.style.hue", russian ? "Оттенок" : "Hue");
        add("gui.halgventmod.map.style.biome_color", russian ? "Цвет биомов" : "Biome Color");
        add("gui.halgventmod.map.style.green_mute", russian ? "Приглушение зелени" : "Mute Greens");
        add("gui.halgventmod.map.style.slopes", russian ? "Склоны" : "Slopes");
        add("gui.halgventmod.map.style.heights", russian ? "Светотень высот" : "Elevation Shading");
        add("gui.halgventmod.map.style.grain", russian ? "Зерно" : "Grain");
        add("gui.halgventmod.map.style.percent", "%s: %s%%");
        add("gui.halgventmod.map.style.degrees", "%s: %s°");
        add("gui.halgventmod.map.style.biome_labels", russian ? "Названия биомов" : "Biome Names");
        add("gui.halgventmod.map.style.reset", russian ? "Сброс к основе" : "Reset to Base");

        // Метки
        add("key.halgventmod.waypoint", russian ? "Поставить метку" : "Add Waypoint");
        add("key.halgventmod.pickup", russian ? "Подобрать / использовать" : "Pick Up / Use");
        add("key.halgventmod.visor", russian ? "Поднять/опустить забрало" : "Raise/Lower Visor");
        add("hud.halgventmod.pickup", russian ? "[%s] Подобрать" : "[%s] Pick up");
        add("hud.halgventmod.pickup_equip", russian ? "[Shift+%s] Надеть" : "[Shift+%s] Put on");
        // Музыка биомов и источники звука
        add("command.halgventmod.music.biome", russian ? "Биом: %s" : "Biome: %s");
        add("command.halgventmod.music.tags", russian ? "Теги: %s" : "Tags: %s");
        add("command.halgventmod.music.no_tags", russian ? "Тегов нет" : "No tags");
        add("command.halgventmod.music.set", russian ? "Набор: %s" : "Music set: %s");
        add("command.halgventmod.music.no_set", russian ? "Набора нет — тишина" : "No music set — silence");
        add("command.halgventmod.music.playing_set", russian
                ? "Пока играет набор %s (смена через несколько секунд)" : "Still playing %s (switching in a few seconds)");
        add("command.halgventmod.music.track", russian ? "Трек: %s (%s из %s)" : "Track: %s (%s of %s)");
        add("command.halgventmod.music.pause", russian ? "Трек: пауза между треками" : "Track: pause between tracks");
        add("command.halgventmod.music.muted", russian
                ? "Громкость от источников звука рядом: %s%%" : "Volume from sound sources nearby: %s%%");
        add("command.halgventmod.music.skipped", russian ? "Следующий трек" : "Next track");
        add("command.halgventmod.music.nothing", russian ? "Сейчас ничего не играет" : "Nothing is playing");
        add("gui.halgventmod.sound_emitter.title", russian ? "Источник звука" : "Sound Emitter");
        add("gui.halgventmod.sound_emitter.scene", russian ? "Сцена" : "Scene");
        add("gui.halgventmod.sound_emitter.no_scene", russian ? "— без сцены —" : "— no scene —");
        add("gui.halgventmod.sound_emitter.missing", russian
                ? "Сцены %s нет — источник молчит" : "No scene %s — the source is silent");
        add("gui.halgventmod.sound_emitter.radius", russian
                ? "Радиус 1–64, пусто — из сцены (%s)" : "Radius 1–64, empty — from the scene (%s)");
        add("hud.halgventmod.use.open", russian ? "Открыть: %s" : "Open: %s");
        add("hud.halgventmod.use.close", russian ? "Закрыть: %s" : "Close: %s");
        add("hud.halgventmod.use.use", russian ? "Использовать: %s" : "Use: %s");
        add("tooltip.halgventmod.arrows", russian ? "Стрел: %s из %s" : "Arrows: %s of %s");
        add("gui.halgventmod.waypoint.new", russian ? "Новая метка" : "New Waypoint");
        add("gui.halgventmod.waypoint.edit", russian ? "Метка" : "Waypoint");
        add("gui.halgventmod.waypoint.name", russian ? "Название" : "Name");
        add("gui.halgventmod.waypoint.color", russian ? "Цвет" : "Color");
        add("gui.halgventmod.waypoint.save", russian ? "Сохранить" : "Save");
        add("gui.halgventmod.waypoint.delete", russian ? "Удалить" : "Delete");
        add("gui.halgventmod.waypoint.icon", russian ? "Значок: %s" : "Icon: %s");
        add("gui.halgventmod.waypoint.flag_color", russian ? "Цвет флага: %s" : "Flag color: %s");
        add("gui.halgventmod.waypoint.compass", russian ? "Видимость на компасе" : "Show on compass");

        // Точки карты (ставит администрация, видны всем)
        add("gui.halgventmod.map.menu.add_point", russian ? "Поставить точку" : "Add Point");
        add("gui.halgventmod.map.menu.edit_point", russian ? "Изменить точку" : "Edit Point");
        add("gui.halgventmod.map.menu.edit_point_bounds", russian ? "Изменить границы точки" : "Edit Point Bounds");
        add("gui.halgventmod.point.new", russian ? "Новая точка" : "New Point");
        add("gui.halgventmod.point.edit", russian ? "Точка" : "Point");
        add("gui.halgventmod.point.shape.circle", russian ? "Круг" : "Circle");
        add("gui.halgventmod.point.shape.polygon", russian ? "Многоугольник" : "Polygon");
        add("gui.halgventmod.point.next", russian ? "Далее" : "Next");
        add("gui.halgventmod.point.hint.circle", russian
                ? "Колёсико или стрелки — радиус · ручку на краю можно тянуть · Shift+колёсико — масштаб"
                : "Scroll or arrows — radius · drag the handle on the edge · Shift+scroll — zoom");
        add("gui.halgventmod.point.hint.polygon", russian
                ? "Тяните углы · кружок на стороне — новый угол · ПКМ по углу — убрать · стрелки — сдвиг угла"
                : "Drag corners · dot on a side — new corner · right-click a corner — remove · arrows — nudge");
        add("gui.halgventmod.point.open", russian ? "Открыта" : "Discovered");
        add("gui.halgventmod.point.hidden", russian ? "Не открыта" : "Undiscovered");
        add("gui.halgventmod.point.unknown", russian ? "Неизвестное место" : "Unknown Place");
        add("gui.halgventmod.point.info", russian ? "Сведения" : "Details");
        add("gui.halgventmod.point.described", russian ? "С описанием" : "With description");
        add("gui.halgventmod.point.description", russian ? "Описание" : "Description");
        add("gui.halgventmod.point.hover_text", russian ? "Текст при наведении (пока не открыта)" : "Hover text (while undiscovered)");
        add("gui.halgventmod.point.icon_hidden", russian ? "Значок до открытия: %s" : "Icon before discovery: %s");
        add("gui.halgventmod.point.icon_open", russian ? "Значок после открытия: %s" : "Icon after discovery: %s");
        add("gui.halgventmod.point.icon_unknown", russian ? "Знак вопроса" : "Question mark");
        add("gui.halgventmod.point.banner", russian ? "Знамя из реестра: %s" : "Registry banner: %s");
        add("gui.halgventmod.point.no_banner", russian ? "нет" : "none");
        add("gui.halgventmod.point.missing_banner", russian ? "%s (нет в реестре)" : "%s (not in the registry)");
        add("gui.halgventmod.point.all_banners", russian ? "Весь реестр" : "All banners");
        add("gui.halgventmod.point.click_description", russian ? "ЛКМ — описание" : "Left-click: description");
        add("hud.halgventmod.point.discovered", russian ? "Открытая локация" : "Location discovered");
        add("gui.halgventmod.map.icon.flag", russian ? "Флаг" : "Flag");
        add("gui.halgventmod.map.icon.castle", russian ? "Крепость" : "Fortress");
        add("gui.halgventmod.map.icon.tower", russian ? "Башня" : "Tower");
        add("gui.halgventmod.map.icon.house", russian ? "Дом" : "House");
        add("gui.halgventmod.map.icon.village", russian ? "Деревня" : "Village");
        add("gui.halgventmod.map.icon.ruins", russian ? "Руины" : "Ruins");
        add("gui.halgventmod.map.icon.temple", russian ? "Храм" : "Temple");
        add("gui.halgventmod.map.icon.mill", russian ? "Мельница" : "Mill");
        add("gui.halgventmod.map.icon.mine", russian ? "Шахта" : "Mine");
        add("gui.halgventmod.map.icon.cave", russian ? "Пещера" : "Cave");
        add("gui.halgventmod.map.icon.camp", russian ? "Лагерь" : "Camp");
        add("gui.halgventmod.map.icon.port", russian ? "Пристань" : "Harbor");
        add("gui.halgventmod.map.icon.treasure", russian ? "Клад" : "Treasure");
        add("gui.halgventmod.map.icon.grave", russian ? "Могила" : "Grave");
        add("gui.halgventmod.map.icon.portal", russian ? "Портал" : "Portal");
        add("gui.halgventmod.map.icon.knight", russian ? "Рыцарь" : "Knight");
        add("gui.halgventmod.map.icon.rider", russian ? "Всадник" : "Rider");
        add("gui.halgventmod.map.icon.caravan", russian ? "Обоз" : "Caravan");
        add("gui.halgventmod.map.icon.guard", russian ? "Стража" : "Guard");
        add("gui.halgventmod.map.icon.mark", russian ? "Отметка" : "Mark");
        add("gui.halgventmod.map.icon.gathering", russian ? "Место сбора" : "Gathering Point");
        add("gui.halgventmod.map.icon.goal", russian ? "Цель" : "Goal");
        add("gui.halgventmod.map.icon.battle", russian ? "Битва" : "Battle");
        add("gui.halgventmod.map.icon.dragon", russian ? "Дракон" : "Dragon");
        add("gui.halgventmod.map.icon.beast", russian ? "Чудовище" : "Beast");
        add("gui.halgventmod.waypoint.coords", "X: %s  Y: %s  Z: %s");
        add("gui.halgventmod.waypoint.distance", russian ? "%s м" : "%s m");
        add("gui.halgventmod.waypoint.in_world", russian ? "%s (%s м)" : "%s (%s m)");

        // Окно «Персонаж»
        String character = "gui.halgventmod.character.";
        add(character + "body", russian ? "Тело и дух" : "Body and Spirit");
        add(character + "stats", russian ? "Параметры" : "Attributes");
        add(character + "traits", russian ? "Особенности" : "Traits");
        add(character + "effects", russian ? "Эффекты" : "Effects");
        add(character + "stat", "%s: %s");
        add(character + "health", russian ? "Здоровье" : "Health");
        add(character + "hunger", russian ? "Голод" : "Hunger");
        add(character + "saturation", russian ? "Насыщение" : "Saturation");
        add(character + "stamina", russian ? "Выносливость" : "Stamina");
        add(character + "thirst", russian ? "Жажда" : "Thirst");
        add(character + "intoxication", russian ? "Интоксикация" : "Intoxication");
        add(character + "pain", russian ? "Боль" : "Pain");
        add(character + "speed", russian ? "Скорость" : "Speed");
        add(character + "attack_damage", russian ? "Урон" : "Attack Damage");
        add(character + "attack_speed", russian ? "Скорость атаки" : "Attack Speed");
        add(character + "armor", russian ? "Броня" : "Armor");
        add(character + "armor_toughness", russian ? "Твёрдость брони" : "Armor Toughness");
        add(character + "traits.body", russian ? "Телесные особенности" : "Physical Traits");
        add(character + "traits.mind", russian ? "Душевное состояние" : "State of Mind");
        add(character + "traits.experience", russian ? "Жизненный опыт" : "Life Experience");
        add(character + "effects.none", russian ? "Нет эффектов" : "No effects");
        add(character + "effects.more", russian ? "…и ещё %s" : "…and %s more");
        add("calendar.halgventmod.screen.title", russian ? "Календарь" : "Calendar");
        add("calendar.halgventmod.button", russian ? "Календарь (%s)" : "Calendar (%s)");
        add("calendar.halgventmod.screen.header", russian ? "%1$s %2$s г." : "%1$s, year %2$s");
        add("calendar.halgventmod.screen.arrows", russian
                ? "Shift — на год, Ctrl — на 10 лет, Home — к сегодня"
                : "Shift — by a year, Ctrl — by 10 years, Home — today");
        add("calendar.halgventmod.screen.today", russian ? "Сегодня: %1$s, %2$s %3$s" : "Today: %1$s, %2$s of %3$s");
        add("calendar.halgventmod.screen.daylight", russian ? "День %1$s, ночь %2$s" : "Day %1$s, night %2$s");
        add("calendar.halgventmod.screen.until_sunset", russian ? "До заката: %s" : "Until sunset: %s");
        add("calendar.halgventmod.screen.until_sunrise", russian ? "До рассвета: %s" : "Until sunrise: %s");
        add("calendar.halgventmod.duration.hours", russian ? "%1$s ч %2$s мин" : "%1$sh %2$sm");
        add("calendar.halgventmod.duration.minutes", russian ? "%s мин" : "%sm");

        // Замки и ключи
        add("lock.halgventmod.locked", russian ? "Заперто" : "Locked");
        add("lock.halgventmod.unlocked", russian ? "Отперто" : "Unlocked");
        add("lock.halgventmod.wrong_key", russian ? "Ключ не подходит" : "The key doesn't fit");
        add("lock.halgventmod.installed", russian ? "Замок повешен" : "Padlock hung");
        add("lock.halgventmod.mortise_installed", russian ? "Врезной замок установлен" : "Mortise lock fitted");
        add("lock.halgventmod.removed", russian ? "Замок снят" : "Lock removed");
        add("lock.halgventmod.cannot_remove", russian ? "Врезной замок не снять" : "A mortise lock can't be removed");
        add("lock.halgventmod.no_lock", russian ? "Здесь нет замка" : "There is no lock here");
        add("lock.halgventmod.no_permission", russian
                ? "Набор ключника — только для администрации и творческого режима"
                : "The locksmith's kit is for administration and creative mode only");
        add("lock.halgventmod.info", russian
                ? "Замок: секрет %1$s, %2$s, %3$s" : "Lock: secret %1$s, %2$s, %3$s");
        add("lock.halgventmod.type.mortise", russian ? "врезной" : "mortise");
        add("lock.halgventmod.type.padlock", russian ? "навесной" : "padlock");
        add("lock.halgventmod.state.locked", russian ? "заперт" : "locked");
        add("lock.halgventmod.state.unlocked", russian ? "отперт" : "unlocked");
        add("lock.halgventmod.blank_key", russian ? "Без бородки: не подходит ни к одному замку" : "Uncut: fits no lock");
        add("lock.halgventmod.advanced.secret", russian ? "Секрет: %s" : "Secret: %s");
        add("lock.halgventmod.command.no_target", russian ? "Посмотрите на дверь или сундук" : "Look at a door or a chest");
        add("lock.halgventmod.command.rekeyed", russian
                ? "Новый секрет замка: %s. Старые ключи больше не подходят" : "New lock secret: %s. Old keys no longer fit");
        add("lock.halgventmod.command.key_given", russian ? "Выдан ключ к секрету %s" : "Key for secret %s given");
        add("lock.halgventmod.command.named", russian ? "Ключ подписан: %s" : "Key named: %s");
        add("lock.halgventmod.command.not_key", russian
                ? "Возьмите ключ в основную руку" : "Hold a key in your main hand");
        // Реестр знамён (BannerCommand)
        add("banner.halgventmod.prefix", russian ? "[Реестр] " : "[Registry] ");
        add("banner.halgventmod.registered", russian ? "Знамя «%s» внесено под id %s" : "Banner \"%s\" registered as %s");
        add("banner.halgventmod.updated", russian ? "Рисунок знамени «%s» (%s) обновлён" : "Design of banner \"%s\" (%s) updated");
        add("banner.halgventmod.renamed", russian ? "Знамя %s теперь называется «%s»" : "Banner %s is now called \"%s\"");
        add("banner.halgventmod.described", russian ? "Описание знамени «%s» обновлено" : "Description of banner \"%s\" updated");
        add("banner.halgventmod.given", russian ? "Выдано знамя «%s» ×%s: %s" : "Gave banner \"%s\" ×%s to %s");
        add("banner.halgventmod.removed", russian ? "Знамя «%s» (%s) убрано из реестра" : "Banner \"%s\" (%s) removed from the registry");
        add("banner.halgventmod.unknown", russian ? "В реестре нет знамени %s" : "No banner %s in the registry");
        add("banner.halgventmod.taken", russian
                ? "id %1$s уже занят. Сменить рисунок: /banner update %1$s"
                : "id %1$s is taken. To change the design: /banner update %1$s");
        add("banner.halgventmod.bad_id", russian
                ? "id — латиница в нижнем регистре, цифры и _, не длиннее %s"
                : "id: lowercase latin letters, digits and _, at most %s");
        add("banner.halgventmod.no_banner", russian ? "Возьмите баннер в основную руку" : "Hold a banner in your main hand");
        add("banner.halgventmod.name_too_long", russian ? "Название длиннее %s символов" : "Name is longer than %s characters");
        add("banner.halgventmod.description_too_long", russian
                ? "Описание длиннее %s символов" : "Description is longer than %s characters");
        add("banner.halgventmod.edited", russian ? "Знамя «%s» исправлено" : "Banner \"%s\" edited");
        String banners = "gui.halgventmod.banners.";
        add(banners + "title", russian ? "Реестр знамён" : "Banner Registry");
        add(banners + "pick", russian ? "Выберите знамя" : "Choose a Banner");
        add(banners + "hint", russian ? "Внести новое: /banner register <id> <название>" : "Add new: /banner register <id> <name>");
        add(banners + "pick_hint", russian ? "Щёлкните по знамени" : "Click a banner");
        add(banners + "search", russian ? "Поиск" : "Search");
        add(banners + "empty", russian ? "Реестр пуст" : "The registry is empty");
        add(banners + "not_found", russian ? "Ничего не найдено" : "Nothing found");
        add(banners + "in_points", russian ? "В точках: %s" : "In points: %s");
        add(banners + "take",russian ? "Взять" : "Take");
        add(banners + "edit", russian ? "Править" : "Edit");
        add(banners + "delete", russian ? "Удалить" : "Delete");
        add(banners + "delete_title", russian ? "Удалить знамя из реестра?" : "Remove the banner from the registry?");
        add(banners + "delete_question", russian
                ? "«%s» (%s) пропадёт из точек и книг. Внесёте снова под тем же id — вернётся."
                : "\"%s\" (%s) will vanish from points and books. Register it again under the same id to bring it back.");
        add(banners + "edit_title", russian ? "Знамя" : "Banner");
        add(banners + "name", russian ? "Название" : "Name");
        add(banners + "description", russian ? "Описание" : "Description");
        // Гербовник (ArmorialCommand, ArmorialScreen)
        String armorial = "armorial.halgventmod.";
        add(armorial + "prefix", russian ? "[Гербовник] " : "[Armorial] ");
        add(armorial + "created", russian ? "Книга «%s» создана под id %s" : "Book \"%s\" created as %s");
        add(armorial + "renamed", russian ? "Книга %s теперь называется «%s»" : "Book %s is now called \"%s\"");
        add(armorial + "given", russian ? "Выдана книга %s: %s" : "Gave book %s to %s");
        add(armorial + "removed", russian ? "Книга %s удалена" : "Book %s removed");
        add(armorial + "missing", russian ? "Книги %s нет" : "There is no book %s");
        add(armorial + "taken", russian ? "id %s уже занят" : "id %s is taken");
        add(armorial + "bad_id", russian
                ? "id — латиница в нижнем регистре, цифры и _, не длиннее %s"
                : "id: lowercase latin letters, digits and _, at most %s");
        add(armorial + "title_too_long", russian ? "Название длиннее %s символов" : "Title is longer than %s characters");
        add(armorial + "list_empty", russian ? "Книг пока нет (общая книга main есть всегда)" : "No books yet (the common book main always exists)");
        add(armorial + "list_header", russian ? "Книг: %s" : "Books: %s");
        add(armorial + "list_entry", russian ? "  %s — «%s», страниц: %s" : "  %s — \"%s\", pages: %s");
        add(armorial + "empty", russian ? "Книга пуста" : "The book is empty");
        add(armorial + "take_copy", russian ? "ПКМ — взять копию" : "Right-click: take a copy");
        add(armorial + "edit_title", russian ? "Правка книги «%s»" : "Editing \"%s\"");
        add(armorial + "add_page", russian ? "+ Страница в конец" : "+ Page at the end");
        add(armorial + "type_large", russian ? "1 знамя" : "1 banner");
        add(armorial + "type_grid", russian ? "9 знамён" : "9 banners");
        add(armorial + "page_title", russian ? "Название страницы" : "Page title");
        add(armorial + "change_banner", russian ? "Сменить знамя" : "Change banner");
        add(armorial + "edit_banner", russian ? "Править в реестре" : "Edit in registry");
        add(armorial + "move_left", russian ? "Сдвинуть страницу назад" : "Move page back");
        add(armorial + "move_right", russian ? "Сдвинуть страницу вперёд" : "Move page forward");
        add(armorial + "delete_page", russian ? "Удалить страницу" : "Delete page");
        add(armorial + "delete_page_question", russian ? "Удалить страницу %s?" : "Delete page %s?");
        add(armorial + "clear_cell", russian ? "Убрать знамя" : "Remove banner");
        add(armorial + "save_question", russian ? "Сохранить изменения в книге?" : "Save changes to the book?");
        add(armorial + "save_hint", russian ? "«Нет» — закрыть без сохранения" : "\"No\" closes without saving");
        add("tooltip.halgventmod.registry_banner", russian ? "Реестр знамён: %s" : "Banner registry: %s");
        add("lock.halgventmod.name_screen", russian ? "Подпись" : "Label");
        add("lock.halgventmod.ring_count", russian ? "Ключей: %1$s из %2$s" : "Keys: %1$s of %2$s");
        add("lock.halgventmod.ring_unnamed_key", russian ? "ключ без подписи" : "unlabelled key");

        // Эффекты перегруза
        add(ModEffects.BURDEN.get(), russian ? "Обуза" : "Burden");
        add(ModEffects.ENCUMBERED.get(), russian ? "Скованность" : "Encumbered");
        add(ModEffects.OVERLOADED.get(), russian ? "Тяжесть" : "Overloaded");
        // Еда (food/FoodEffects)
        add(ModEffects.SATIETY.get(), russian ? "Сытость" : "Satiety");
        add(ModEffects.WELL_FED.get(), russian ? "Улучшенное насыщение" : "Well fed");
        add(ModEffects.POISONING.get(), russian ? "Отравление" : "Food poisoning");
        add(ModEffects.VIGOR.get(), russian ? "Бодрость" : "Vigour");
        foodTranslations();

        // Субтитры звуков: обычные события и голоса (один ключ на вид крика для всех наборов)
        for (ModSounds.Definition sound : ModSounds.definitions()) {
            add(sound.subtitleKey(), russian ? sound.subtitleRu() : sound.subtitleEn());
        }
        for (Voices.Cry cry : Voices.Cry.values()) {
            add(cry.subtitleKey(), russian ? cry.subtitleRu() : cry.subtitleEn());
        }

        // ----- Кожевенное дело -----
        leatherTranslations();
        cookingTranslations();
        qualityTranslations();
        brewingTranslations();
        potteryTranslations();
        weavingTranslations();
        carpentryTranslations();
        herbsTranslations();
        alchemyTranslations();
        glassTranslations();
        recipeTabsTranslations();
        characterTranslations();
        SmithingLang.add((key, ru, en) -> add(key, russian ? ru : en));
    }

    /** Алхимия (alchemy/*): реторта, реагенты бродильной бочки, порошки и эссенции трав, сулема, нашатырь. */
    private void alchemyTranslations() {
        // Порошок и эссенция каждой травы (Herb.mark: item.halgventmod.<предмет>.<трава>)
        String[][] herbs = {
                {"chamomile", "Ромашковый порошок", "Ромашковая эссенция", "Chamomile powder", "Chamomile essence"},
                {"mint", "Мятный порошок", "Мятная эссенция", "Mint powder", "Mint essence"},
                {"st_johns_wort", "Порошок зверобоя", "Эссенция зверобоя", "St John's wort powder", "St John's wort essence"},
                {"yarrow", "Порошок тысячелистника", "Эссенция тысячелистника", "Yarrow powder", "Yarrow essence"},
                {"wormwood", "Полынный порошок", "Полынная эссенция", "Wormwood powder", "Wormwood essence"},
                {"nettle", "Крапивный порошок", "Крапивная эссенция", "Nettle powder", "Nettle essence"},
                {"thyme", "Порошок чабреца", "Эссенция чабреца", "Thyme powder", "Thyme essence"},
                {"lavender", "Лавандовый порошок", "Лавандовая эссенция", "Lavender powder", "Lavender essence"}};
        for (String[] h : herbs) {
            add("item.halgventmod.herb_powder." + h[0], russian ? h[1] : h[3]);
            add("item.halgventmod.herb_essence." + h[0], russian ? h[2] : h[4]);
        }
        add("tooltip.halgventmod.herb.in_essence", russian ? "В эссенции: %s (в %s раза дольше)" : "In an essence: %s (%s times as long)");
        add("tooltip.halgventmod.herb.essence_effect", russian ? "Действие: %s (в %s раза дольше)" : "Effect: %s (%s times as long)");
        add("tooltip.halgventmod.alchemy.coat", russian ? "Намазан сулемой: отравит ещё %s ударов" : "Coated with sublimate: %s more poisoned blows");

        // Реторта (RetortScreen)
        gui("retort.title", "Реторта", "Retort");
        gui("retort.in", "в колбу", "flask");
        gui("retort.additive", "добавка", "additive");
        gui("retort.out", "приёмник", "receiver");
        gui("retort.residue", "остаток", "residue");
        gui("retort.source_heat", "Под ретортой огонь: жар %s. Жар 3 — быстрее, но часть летучего уходит дымом.",
                "Fire under the retort: heat %s. Heat 3 is faster, but some of the vapour goes up in smoke.");
        gui("retort.source_none", "Под ретортой нет огня: поставьте её на очаг или над костром.",
                "No fire under the retort: put it on a hearth or over a campfire.");
        gui("retort.state_empty", "Положите в колбу то, что возгоняют (книга слева)", "Put in something to sublime (see the book)");
        gui("retort.state_unknown", "Это не возгоняют", "That doesn't sublime");
        gui("retort.state_needs_additive", "Нужна добавка: купорос к ртути", "Needs an additive: vitriol for quicksilver");
        gui("retort.state_cold", "Холодно — нужен огонь", "Cold: it needs a fire");
        gui("retort.state_full", "Приёмник или остаток полны — заберите", "The receiver or residue is full: take it");
        gui("retort.state_working", "Возгоняется", "Subliming");
        gui("retort.state_hot", "Возгоняется, но жар велик — часть уходит дымом", "Subliming, but too hot: some goes up in smoke");
        gui("retort.book_hint", "Летучее оседает в приёмнике, остаток — в колбе.", "The vapour settles in the receiver, the rest stays in the flask.");
        book("retort.book.pyrite", "Сера из колчедана", "Sulphur from pyrite",
                "Медную руду (необожжённую) — в колбу, реторту — на огонь.", "Unroasted copper ore into the flask, the retort over a fire.",
                "Сера в приёмнике, обожжённая руда в колбе.", "Sulphur in the receiver, roasted ore in the flask.");
        book("retort.book.sulfur", "Серный цвет", "Flowers of sulphur",
                "Серу — в колбу.", "Sulphur into the flask.",
                "Серный цвет («дух серы») — для серного бальзама.", "Flowers of sulphur, for sulphur balm.");
        book("retort.book.bone", "Нашатырь", "Sal ammoniac",
                "Кость — в колбу.", "A bone into the flask.",
                "Нашатырь в приёмнике, костяная зола в колбе.", "Sal ammoniac in the receiver, bone ash in the flask.");
        book("retort.book.sublimate", "Сулема", "Corrosive sublimate",
                "Ртуть — в колбу, купорос — в добавку.", "Quicksilver into the flask, vitriol as the additive.",
                "Сулема — яд для клинка; склянка от ртути остаётся.", "Sublimate, a blade poison; the quicksilver vial is left.");

        // Бродильная бочка: алхимия
        gui("barrel.reagent", "реагент: %s (%s из %s)", "reagent: %s (%s of %s)");
        gui("barrel.needs_reagent", "Нужен реагент — по одному на склянку (или к нему мёд)", "Needs a reagent, one per bottle (or honey with it)");
        gui("barrel.steeping", "Выйдет %s к %s (ещё %s сут.)", "%s on %s (%s days left)");
        gui("barrel.ready_solid", "Осело — выгребите пустой рукой (E)", "Settled: scoop it out with an empty hand (E)");
        book("barrel.book.white_lead", "Белая краска", "White paint",
                "Уксус + свинцовый пепел (по одному на склянку), запечатать на 2 сут.", "Vinegar + lead ash (one per bottle), seal for 2 days.",
                "Свинцовые белила: 2 белой краски со склянки. Выгрести рукой.", "White lead: 2 white dye per bottle. Scoop out by hand.");
        book("barrel.book.verdigris", "Ярь-медянка", "Verdigris",
                "Уксус + медный слиток (по одному на склянку), 4 сут.", "Vinegar + a copper ingot (one per bottle), 4 days.",
                "2 ярь-медянки со склянки. Выгрести рукой.", "2 verdigris per bottle. Scoop out by hand.");
        book("barrel.book.vitriol", "Купорос", "Blue vitriol",
                "Вода (ведро, кувшин, склянка) + обожжённая медная руда, 3 сут.", "Water (bucket, jug, bottle) + roasted copper ore, 3 days.",
                "Купорос — для сулемы. Выгрести рукой.", "Vitriol, for sublimate. Scoop out by hand.");
        book("barrel.book.wound_balm", "Ранозаживляющий бальзам", "Wound balm",
                "Крепкое + ярь-медянка, 3 сут.", "Spirit + verdigris, 3 days.",
                "+2 сердца и Регенерация I на 30 с.", "+2 hearts and Regeneration I for 30 s.");
        book("barrel.book.sulfur_balm", "Серный бальзам", "Sulphur balm",
                "Крепкое + серный цвет, 3 сут.", "Spirit + flowers of sulphur, 3 days.",
                "Огнестойкость на 3 мин.", "Fire Resistance for 3 min.");
        book("barrel.book.theriac", "Териак", "Theriac",
                "Крепкое + мёд (добавка) + костяная зола, 5 сут.", "Spirit + honey (extra) + bone ash, 5 days.",
                "Противоядие: снимает яд, иссушение, отравление.", "Antidote: clears poison, wither, food poisoning.");
        book("barrel.book.essence", "Эссенция травы", "Herb essence",
                "Крепкое + травяной порошок (из ступы), 3 сут.", "Spirit + herb powder (from a mortar), 3 days.",
                "Как отвар этой травы, но втрое дольше.", "Like a tea of that herb, but three times as long.");
        brewing("barrel_reagent_first", "Сначала влейте основу: крепкое, уксус или воду", "Pour in a base first: spirit, vinegar or water");
        brewing("barrel_reagent_wrong", "С этой основой этот реагент ничего не даст", "This reagent does nothing with this base");
        brewing("barrel_reagent_mixed", "Реагент уже другой — разные не кладут", "There is another reagent already: they don't mix");
        brewing("barrel_reagent_enough", "Реагента хватает: по одному на склянку", "Enough reagent: one per bottle");
        brewing("barrel_reagent_added", "Положено: %s (%s из %s)", "Added: %s (%s of %s)");
        brewing("barrel_needs_reagent", "Налито %s, реагента %s: нужно по одному на склянку", "%s in, %s reagent: one per bottle is needed");
        brewing("barrel_ready_solid", "Осело: %s склянок — распечатайте и выгребите пустой рукой", "Settled: %s bottles' worth, unseal and scoop out by hand");
        brewing("barrel_solid", "Это не разливают — выгребите пустой рукой", "This isn't poured: scoop it out with an empty hand");
        brewing("barrel_took_solid", "Выгребли: %s × %s", "Scooped out: %s × %s");

        // Сообщения алхимии
        add("message.halgventmod.alchemy.retort_other", russian ? "В реторте уже другое" : "Something else is in the retort");
        add("message.halgventmod.alchemy.retort_full", russian ? "Больше не влезет" : "No more fits");
        add("message.halgventmod.alchemy.retort_empty", russian ? "Реторта пуста: положите медную руду, серу, кость или ртуть"
                : "The retort is empty: put in copper ore, sulphur, a bone or quicksilver");
        add("message.halgventmod.alchemy.retort_unknown", russian ? "Это не возгоняют" : "That doesn't sublime");
        add("message.halgventmod.alchemy.retort_needs_additive", russian ? "Нужна добавка: купорос" : "Needs an additive: vitriol");
        add("message.halgventmod.alchemy.retort_take", russian ? "Приёмник полон — заберите пустой рукой" : "The receiver is full: take it with an empty hand");
        add("message.halgventmod.alchemy.retort_cold", russian ? "Под ретортой нет огня" : "No fire under the retort");
        add("message.halgventmod.alchemy.retort_working", russian ? "Возгоняется: %s%%" : "Subliming: %s%%");
        add("message.halgventmod.alchemy.sniffed", russian ? "Резкий дух нашатыря — в голове прояснилось" : "The sharp smell of sal ammoniac clears your head");
        add("message.halgventmod.alchemy.coated", russian ? "%s намазан сулемой: %s удара отравят" : "%s is coated with sublimate: %s blows will poison");
        add("message.halgventmod.alchemy.coat_full", russian ? "Клинок уже намазан" : "The blade is already coated");
        add("message.halgventmod.alchemy.coat_worn", russian ? "Яд на клинке стёрся" : "The poison on the blade has worn off");
    }

    /** Вкладки тем у списков рецептов (RecipeTabs): книга станций, шитейный стол, верстак, кухонный стол. */
    private void recipeTabsTranslations() {
        gui("recipes.group.brewing", "Брожение", "Brewing");
        gui("recipes.group.sediment", "Осадки: краска, купорос, поташ", "Sediments: paint, vitriol, potash");
        gui("recipes.group.remedy", "Снадобья", "Remedies");
        gui("sewing_table.group.gear", "Снаряжение: ремни, сумки, колчан", "Gear: straps, bags, quiver");
        gui("sewing_table.group.hands", "Варежки и перчатки", "Mittens and gloves");
        gui("sewing_table.group.armor", "Поддоспешное и доспех", "Padding and armour");
        gui("sewing_table.group.clothes", "Одежда: капюшоны, плащи, сюрко", "Clothes: hoods, cloaks, surcoats");
        gui("carpenter_bench.group.weaving", "Для ткачества", "For weaving");
        gui("carpenter_bench.group.household", "Утварь и снасть", "Household and gear");
        gui("carpenter_bench.group.tools", "Инструмент плотника", "Carpenter's tools");
        gui("carpenter_bench.group.parts", "Рукояти и древки для кузни", "Handles and shafts for the smith");
        gui("kitchen.group.baking", "Тесто и выпечка", "Dough and baking");
        gui("kitchen.group.dishes", "Блюда для печи", "Oven dishes");
        gui("kitchen.group.other", "Прочее: морс, зерно, мёд", "Other: juice, grain, honey");
    }

    /** Стекольное дело (glass/*): печь, стеклодув, формы, цвета. */
    private void glassTranslations() {
        String[][] forms = {{"bottles", "Склянки", "Bottles"}, {"goblet", "Бокал", "Goblet"}, {"flask", "Бутыль", "Flask"},
                {"window", "Оконное стекло", "Window glass"}};
        for (String[] f : forms) {
            add("glass.halgventmod.form." + f[0], russian ? f[1] : f[2]);
        }
        String[][] tints = {{"forest", "лесное, зеленоватое", "forest green"}, {"green", "зелёное", "green"},
                {"crystal", "хрусталь", "crystal"}, {"milk", "молочное", "milk white"}, {"amber", "бурое", "amber brown"},
                {"ruby", "рубиновое", "ruby"}};
        for (String[] t : tints) {
            add("glass.halgventmod.tint." + t[0], russian ? t[1] : t[2]);
        }
        add("tooltip.halgventmod.glass.tint", russian ? "Стекло: %s" : "Glass: %s");
        add("tooltip.halgventmod.pottery.unannealed", russian ? "Не отожжено: хрупкое" : "Not annealed: brittle");

        // Окно печи (GlassFurnaceScreen)
        gui("glass.title", "Стекломасса", "Glass");
        gui("glass.forms", "Выдувать", "Blow");
        gui("glass.fire", "Огонь", "Fire");
        gui("glass.blow_button", "Выдувать", "Blow");
        gui("glass.take", "Забрать отожжённое", "Take annealed");
        gui("glass.cost", "%s порц.", "%s port.");
        gui("glass.no_pipe", "нет трубки при себе", "no blowpipe on you");
        gui("glass.blow", "Выдувать: %s (%s)", "Blow: %s (%s)");
        gui("glass.melting", "варится стекло", "melting glass");
        gui("glass.workable", "можно выдувать", "workable");
        gui("glass.cold", "варится от %s°", "melts from %s°");
        gui("glass.batch_1", "песок %s · поташ %s", "sand %s · potash %s");
        gui("glass.batch_2", "флюс %s · бой %s", "flux %s · cullet %s");
        gui("glass.molten", "Стекломасса: %s из %s", "Glass: %s of %s");
        gui("glass.tint", "цвет: %s", "colour: %s");
        gui("glass.no_tint", "склянки не красят", "bottles aren't tinted");
        gui("glass.lehr", "отжиг", "lehr");
        book("glass.book.potash", "Поташ", "Potash",
                "Зола из очага + вода в бродильной бочке, запечатать на 2 сут.", "Hearth ash + water in a fermenting barrel, sealed for 2 days.",
                "Поташ — выгрести рукой.", "Potash: scoop it out by hand.");
        book("glass.book.melt", "Стекломасса", "Glass",
                "Песок ×2 + поташ (+ флюс — чище) в шихту, жар от 1100°.", "Sand ×2 + potash (+ flux for clearer glass) into the batch, heat from 1100°.",
                "Порция за 30 с; бой стекла — порция вдвое быстрее.", "A portion in 30 s; cullet makes one twice as fast.");
        book("glass.book.blow", "Выдувание", "Blowing",
                "Выберите изделие кнопкой, E трубкой по печи от 1000°: набрать, выдуть, катать.",
                "Choose the piece with the button, E with a blowpipe at the furnace from 1000°: gather, blow, marver.",
                "Склянки ×4, бокал, бутыль, оконное стекло ×8. Плохо — лопнет в бой.",
                "Bottles ×4, goblet, flask, window glass ×8. Badly done, it bursts into cullet.");
        book("glass.book.anneal", "Отжиг", "Annealing",
                "Выдутое само ложится в камеру отжига — минута. E пустой рукой — забрать отожжённое.",
                "Blown work goes into the lehr by itself, a minute. E with an empty hand takes what is annealed.",
                "Вынутое раньше хрупко: посуда бьётся чаще, склянки и окна — половина лопнет.",
                "Taken out early it is brittle: ware breaks sooner, half the bottles and panes burst.");
        book("glass.book.tint", "Цвет", "Colour",
                "Добавка в ячейку цвета — по одной на изделие.", "A colourant in its slot, one per piece.",
                "Ярь-медянка — зелёное, свинцовый пепел — хрусталь, костяная зола — молочное, болотная руда — бурое, "
                        + "золотой песок — рубиновое.",
                "Verdigris: green; lead ash: crystal; bone ash: milk white; bog ore: brown; gold dust: ruby.");
        book("barrel.book.potash", "Поташ", "Potash",
                "Вода + зола из очага (по одной на склянку), 2 сут.", "Water + hearth ash (one per bottle), 2 days.",
                "Поташ — для стекла. Выгрести рукой.", "Potash, for glass. Scoop out by hand.");

        // Стеклодув в окне кузни (SmithWorkScreen)
        gui("smith_work.glass_title", "Стеклодув: %s", "Glassblowing: %s");
        gui("smith_work.glass_in_furnace", "В окне печи… пробел — вынуть", "In the glory hole… space takes it out");
        gui("smith_work.glass_furnace_hint", "%s Пробел — разогреть в окне печи.", "%s Space reheats it in the glory hole.");
        gui("smith_work.glass_burnt", "Перегрели — стекло потекло!", "Overheated: the glass runs!");
        gui("smith_work.glass_cold", "Остыло — трещина!", "Cooled: a crack!");
        gui("smith_work.op.gather", "Набрать", "Gather");
        gui("smith_work.op.blow", "Выдуть", "Blow");
        gui("smith_work.op.spin", "Катать", "Marver");
        gui("smith_work.op.spin_sheet", "Раскрутить лист", "Spin the sheet");
        gui("smith_work.gather_status", "Капля: %s%%, стекло %s раз", "Gob: %s%%, dripped %s times");
        gui("smith_work.gather_hint", "ЛКМ — набирать; всё время вращайте мышью влево-вправо; отпустите в зелёной зоне.",
                "Hold left click to gather; keep turning with the mouse left and right; let go in the green zone.");
        gui("smith_work.gather_drip", "Стекает — вращайте!", "It drips: keep turning!");
        gui("smith_work.gather_more", "Мало — наберите ещё", "Too little: gather more");
        gui("smith_work.blow_status", "Пузырь: %s%%, давление %s%%, лопнуло %s", "Bubble: %s%%, pressure %s%%, burst %s");
        gui("smith_work.blow_hint", "ЛКМ — дуть, не выше красной черты; пузырь в зелёной зоне — ПКМ.",
                "Hold left click to blow, below the red line; bubble in the green zone: right click.");
        gui("smith_work.blow_burst", "Лопнуло!", "Burst!");
        gui("smith_work.blow_more", "Мал ещё — дуйте", "Still small: blow");
        gui("smith_work.spin_status", "Форма: %s%%, огрехи %s", "Shape: %s%%, flaws %s");
        gui("smith_work.spin_hint", "Водите мышью влево-вправо: скорость — в зелёной зоне. Медленно — оседает, быстро — бьёт.",
                "Move the mouse left and right: keep the speed in the green zone. Too slow it sags, too fast it wobbles.");
        gui("smith_work.spin_sheet_hint", "Раскручивайте лист мышью влево-вправо: скорость — в зелёной зоне.",
                "Spin the sheet with the mouse left and right: keep the speed in the green zone.");

        // Сообщения
        String[][] messages = {
                {"already_lit", "Печь уже горит", "The furnace is already lit"},
                {"no_fuel", "Нет дров", "No firewood"},
                {"fuel_full", "Дров хватает", "Enough firewood"},
                {"need_lighter", "Нужно огниво, огненный заряд или факел", "You need a flint and steel, a fire charge or a torch"},
                {"colorant_other", "В ячейке цвета другая добавка", "Another colourant is in the slot"},
                {"colorant_full", "Добавки хватает", "Enough colourant"},
                {"too_cold", "Печь холодная: %s°, выдувать — от %s°", "The furnace is cold: %s°, blowing from %s°"},
                {"not_enough", "Стекломассы %s, а на «%3$s» нужно %2$s", "%s glass, but «%3$s» needs %2$s"},
                {"burst", "Изделие лопнуло — осталось в бой", "The piece burst: cullet is all that's left"},
                {"annealing", "%s — в камере отжига", "%s is in the lehr"},
                {"lehr_full", "Камера отжига полна — изделие не отожжено", "The lehr is full: the piece is not annealed"},
                {"need_pipe", "Нужна стеклодувная трубка при себе", "You need a blowpipe on you"},
        {"status", "Печь %s°. Шихта: песок %s, поташ %s, флюс %s, бой %s. Стекломасса %s из %s",
                        "Furnace %s°. Batch: sand %s, potash %s, flux %s, cullet %s. Glass %s of %s"}};
        for (String[] m : messages) {
            add("message.halgventmod.glass." + m[0], russian ? m[1] : m[2]);
        }
    }

    /** Строка книги станции: название, что делать (.how), что выйдет (.serve). */
    private void book(String key, String ru, String en, String howRu, String howEn, String serveRu, String serveEn) {
        gui(key, ru, en);
        gui(key + ".how", howRu, howEn);
        gui(key + ".serve", serveRu, serveEn);
    }

    /** Травы: кустики, виды, сушёные и отвары (herbs/*). */
    private void herbsTranslations() {
        add("hud.halgventmod.use.cut_herbs", russian ? "Срезать: %s" : "Cut: %s");
        herbs("cut", "Срезано: %s × %s", "Cut: %s × %s");
        herbs("regrowing", "%s: здесь уже срезали — отрастёт за пару дней (зимой не растёт)",
                "%s: already cut here, it grows back in a couple of days (not in winter)");
        herbs("need_cutter", "%s: срезать ножом, серпом или косой (E с ними в руке)",
                "%s: cut it with a knife, sickle or scythe (E with one in hand)");
        add("tooltip.halgventmod.herb.effect", russian ? "Действие: %s" : "Effect: %s");
        add("tooltip.halgventmod.herb.in_tea", russian ? "В отваре: %s" : "In a tea: %s");
        herb("chamomile", "Сушёная ромашка", "Ромашковый отвар", "снимает тошноту и отравление, Регенерация I на 8 с",
                "Dried chamomile", "Chamomile tea", "clears nausea and food poisoning, Regeneration I for 8 s");
        herb("mint", "Сушёная мята", "Мятный отвар", "снимает тошноту, Скорость I на 30 с",
                "Dried mint", "Mint tea", "clears nausea, Speed I for 30 s");
        herb("st_johns_wort", "Сушёный зверобой", "Отвар зверобоя", "Сопротивление I на 60 с",
                "Dried St John's wort", "St John's wort tea", "Resistance I for 60 s");
        herb("yarrow", "Сушёный тысячелистник", "Отвар тысячелистника", "Регенерация I на 15 с",
                "Dried yarrow", "Yarrow tea", "Regeneration I for 15 s");
        herb("wormwood", "Сушёная полынь", "Полынный отвар", "снимает отравление, голод и яд",
                "Dried wormwood", "Wormwood tea", "clears food poisoning, hunger and poison");
        herb("nettle", "Сушёная крапива", "Крапивный отвар", "Бодрость на 60 с: выносливость восстанавливается ×1,3",
                "Dried nettle", "Nettle tea", "Vigour for 60 s: stamina recovers ×1.3");
        herb("thyme", "Сушёный чабрец", "Отвар чабреца", "Поглощение I на 60 с",
                "Dried thyme", "Thyme tea", "Absorption I for 60 s");
        herb("lavender", "Сушёная лаванда", "Лавандовый отвар", "снимает тошноту, слепоту и тьму, Регенерация I на 4 с",
                "Dried lavender", "Lavender tea", "clears nausea, blindness and darkness, Regeneration I for 4 s");
    }

    private void herbs(String key, String ru, String en) {
        add("message.halgventmod.herbs." + key, russian ? ru : en);
    }

    private void herb(String id, String driedRu, String teaRu, String effectRu, String driedEn, String teaEn, String effectEn) {
        add("item.halgventmod.dried_herbs." + id, russian ? driedRu : driedEn);
        add("item.halgventmod.herbal_tea." + id, russian ? teaRu : teaEn);
        add("tooltip.halgventmod.herb." + id, russian ? effectRu : effectEn);
    }

    /** Кожевенное дело: шкуры, растворы, станции, окно шитья (leather/*). */
    private void leatherTranslations() {
        for (Animals.Kind kind : Animals.all()) {
            add(Animals.langKey(kind.id()), russian ? kind.nameRu() : kind.nameEn());
            // «солёная волчья шкура», «мех волка» (HideItem)
            add(Animals.langKey(kind.id()) + ".lower", russian ? kind.nameRu().toLowerCase(java.util.Locale.ROOT) : kind.nameEn().toLowerCase(java.util.Locale.ROOT));
            add(Animals.langKey(kind.id()) + ".of", russian ? kind.ofRu() : kind.nameEn());
        }
        for (Tannage tannage : Tannage.values()) {
            add(tannage.langKey(), russian ? switch (tannage) {
                case TANNED -> "дублёную кожу";
                case SOFT -> "мягкую кожу";
                case FUR -> "выделанный мех";
                case RAWHIDE -> "сыромять";
            } : switch (tannage) {
                case TANNED -> "tanned leather";
                case SOFT -> "soft leather";
                case FUR -> "dressed fur";
                case RAWHIDE -> "rawhide";
            });
        }
        solution(Solution.NONE, "пусто", "empty");
        solution(Solution.WATER, "вода", "water");
        solution(Solution.LYE, "зольный щёлок", "ash lye");
        solution(Solution.BARK, "корьевой раствор", "bark liquor");
        solution(Solution.ALUM, "квасцовый раствор", "alum bath");
        solution(Solution.FAT, "жировой раствор", "fat liquor");

        add("item.halgventmod.raw_hide.of", russian ? "%s шкура" : "%s hide");
        add("item.halgventmod.salted_hide.of", russian ? "Солёная %s шкура" : "Salted %s hide");
        add("item.halgventmod.dressed_fur.of", russian ? "Мех %s" : "%s fur");
        add("tooltip.halgventmod.hide", russian ? "%s шкура · кусков кожи: %s" : "%s hide · leather pieces: %s");
        add("tooltip.halgventmod.fleshed", russian ? "Мездра снята" : "Fleshed");
        add("tooltip.halgventmod.not_fleshed", russian ? "Мездра не снята" : "Not fleshed");
        add("tooltip.halgventmod.has_fur", russian ? "С мехом: в жировой чан — выйдет мех" : "Furred: the fat vat makes fur");
        add("tooltip.halgventmod.dries_into", russian ? "На раме высохнет в %s" : "Dries on the rack into %s");
        add("tooltip.halgventmod.oiled", russian ? "Смазана, пересохнет через %s сут." : "Oiled, dries out in %s days");
        add("tooltip.halgventmod.dried_out",
                russian ? "Пересохла: хуже защищает. Смажьте на шитейном столе" : "Dried out: protects less. Oil it at the sewing table");

        add("gui.halgventmod.sewing.title", russian ? "Шитьё: %s" : "Sewing: %s");
        add("gui.halgventmod.sewing.progress", russian ? "Стежки: %s / %s · промахи: %s" : "Stitches: %s / %s · misses: %s");
        add("gui.halgventmod.sewing.hint",
                russian ? "ЛКМ или Пробел — проколоть, когда игла над отметкой" : "Click or Space to pierce when the needle is over the mark");
        add("gui.halgventmod.sewing.done", russian ? "Сшито!" : "Sewn!");

        add("gui.halgventmod.pattern.title", russian ? "Выкройки" : "Patterns");
        add("gui.halgventmod.sewing_table.on_table", russian ? "На столе" : "On the table");
        add("gui.halgventmod.sewing_table.cut", russian ? "Раскроить" : "Cut");
        add("gui.halgventmod.sewing_table.sew", russian ? "Шить" : "Sew");
        add("gui.halgventmod.sewing_table.cut_already", russian ? "Раскроено — шейте" : "Cut — now sew");
        add("gui.halgventmod.sewing_table.leather", russian ? "Кожа — кроить ножом" : "Leather — cut with a knife");
        add("gui.halgventmod.sewing_table.cloth", russian ? "Ткань — кроить ножницами" : "Cloth — cut with shears");
        add("gui.halgventmod.cut.title", russian ? "Раскрой: %s" : "Cutting: %s");
        add("gui.halgventmod.cut.hint_start", russian ? "Зажмите ЛКМ на метке и ведите по мелу" : "Hold the button on the mark, follow the chalk");
        add("gui.halgventmod.cut.hint", russian ? "Ровно и не спеша: рывок или сход с линии — срыв" : "Steady: a jerk or leaving the line is a slip");
        add("gui.halgventmod.cut.progress", russian ? "Раскроено %s%% · срывы %s · неровность %s" : "Cut %s%% · slips %s · wobble %s");
        add("gui.halgventmod.cut.done", russian ? "Раскроено!" : "Cut!");
        add("gui.halgventmod.cut.slip", russian ? "Срыв!" : "Slip!");
        add("gui.halgventmod.pattern.ingredients", russian ? "Материалы" : "Materials");
        add("gui.halgventmod.pattern.tools", russian ? "Инструменты" : "Tools");
        add("gui.halgventmod.pattern.current", russian ? "Выбрана на столе" : "On the table now");
        add("gui.halgventmod.pattern.leather", russian ? "Кожа (кроится ножом):" : "Leather (cut with the knife):");
        add("gui.halgventmod.pattern.extras", russian ? "Фурнитура (после раскроя):" : "Fittings (after cutting):");
        add("gui.halgventmod.pattern.thread", russian ? "Нитки: %s / %s" : "Thread: %s / %s");
        add("gui.halgventmod.pattern.stitches", russian ? "Стежков: %s" : "Stitches: %s");
        add("gui.halgventmod.pattern.cut_only", russian ? "Только раскроить, без шитья" : "Just cut, no sewing");
        leather("rotten", "Шкура сгнила. Годится только на клей: в чан с водой над огнём", "The hide has rotted. Only good for glue: a water vat over a fire");
        leather("beam_empty", "Положите на колоду шкуру: парную, солёную, золёную или сыромять", "Lay a hide on the beam: raw, salted, limed or rawhide");
        leather("salt_only_raw", "Солят только парную шкуру", "Only a raw hide is salted");
        leather("salted", "Шкура посолена: пролежит 16 суток", "Hide salted: it keeps for 16 days");
        leather("knife_wrong", "Мездряком работают по парной или солёной шкуре", "The fleshing knife is for a raw or salted hide");
        leather("already_fleshed", "Мездра уже снята. Дальше — чан: щёлок (зола) или жир для меха", "Already fleshed. Next: the vat — lye (ash) or fat for fur");
        leather("fleshed", "Мездра снята", "Fleshed");
        leather("flesh_layer", "Слой снят, на этом клочке ещё: %s", "A layer off, this patch has %s more");
        leather("flesh_left", "Клочок счищен. Осталось клочков мездры: %s", "Patch cleared. Flesh patches left: %s");
        leather("flesh_miss", "Здесь чисто — бейте по клочкам мездры (осталось: %s)", "Clean here: strike the flesh patches (%s left)");
        leather("dehaired", "Волос снят — получилось голье", "Hair scraped off — you have a pelt");
        leather("parchment_small_only", "Пергамент выходит только из мелкой шкуры: козы, овцы, кролика", "Parchment comes only from a small hide: goat, sheep, rabbit");
        leather("parchment", "Выскоблен пергамент", "Parchment scraped");
        leather("scraper_wrong", "Стругом снимают волос с золёной шкуры или скоблят сыромять", "The scraper is for a limed hide or rawhide");
        leather("cut", "Спешка! Нож прорезал шкуру — она стала хуже", "Too hasty! The blade cut the hide — it is worse now");
        leather("stroke", "%s / %s", "%s / %s");

        leather("vat_busy", "В чане уже что-то есть", "Something is already in the vat");
        leather("vat_dry", "Чан пуст: налейте воды ведром", "The vat is empty: pour water with a bucket");
        leather("vat_take_first", "Сначала выньте всё из чана", "Take everything out of the vat first");
        leather("vat_already", "В чане уже %s: добавлять нечего", "The vat already holds %s");
        leather("additives", "В воде: зола %s/%s · кора %s/%s · квасцы %s/%s, соль %s · сало %s/%s",
                "In the water: ash %s/%s · bark %s/%s · alum %s/%s, salt %s · fat %s/%s");
        leather("solution_ready", "Готов %s", "Ready: %s");
        leather("vat_full", "В чане нет места", "The vat is full");
        leather("flesh_first", "Сначала снимите мездру на колоде", "Flesh the hide on the beam first");
        leather("fat_needs_fur", "В жир кладут шкуру с мехом", "Only a furred hide goes into fat");
        leather("vat_wrong", "Это не идёт в %s", "This doesn't go into %s");
        leather("vat_spent", "Раствор выработан на шкуры, что уже лежат", "The bath is used up by the hides already in it");
        leather("needs_fire", "Опущено. Разведите огонь под чаном", "Sunk. Light a fire under the vat");
        leather("soaking", "Мокнет: %s", "Soaking: %s");
        leather("undertanned", "Вынута раньше срока: кожа недодублена", "Taken out early: the leather is under-tanned");
        leather("too_early", "Рано: готово на %s%%. Работа сначала", "Too early: %s%% done. Starting over");
        leather("vat_status", "%s, хватит ещё на шкур: %s", "%s, enough for %s more hides");

        leather("rack_empty", "Натяните мокрую кожу или голье", "Stretch wet leather or a pelt on it");
        leather("rack_placed", "Натянута. В эту пору сохнет суток: %s", "Stretched. In this season it dries in %s days");
        leather("rack_not_dry", "Ещё не высохла: %s%%", "Not dry yet: %s%%");
        leather("rack_taken", "Снято с рамы", "Taken off the rack");
        leather("already_softened", "Уже разбита колотушкой", "Already softened");
        leather("mallet_rush", "Не спешите: бейте ровно", "Don't rush: strike evenly");
        leather("softened", "Кожа размята: стала лучше", "The leather is softened: it is better now");
        leather("already_oiled", "Уже пропитана", "Already dressed with oil");
        leather("oiled", "Пропитана жиром: стала лучше", "Dressed with oil: it is better now");

        leather("table_busy", "Сначала доделайте начатое или заберите кожу", "Finish what you started or take the leather back");
        leather("pick_pattern", "Выберите выкройку в окне стола", "Pick a pattern in the table window");
        leather("pattern", "Выкройка: %1$s — кожа: %2$s; затем: %3$s; нитки %4$s, стежков %5$s",
                "Pattern: %1$s — leather: %2$s; then: %3$s; thread %4$s, stitches %5$s");
        leather("pattern_simple", "Выкройка: %1$s — кожа: %2$s; нитки %4$s, стежков %5$s",
                "Pattern: %1$s — leather: %2$s; thread %4$s, stitches %5$s");
        leather("pattern_cut_only", "Выкройка: %1$s — кожа: %2$s; только раскроить", "Pattern: %1$s — leather: %2$s; just cut");
        leather("enough_of", "%s хватает", "Enough %s");
        leather("wrong_leather", "Это не по выкройке", "Not for this pattern");
        leather("wrong_extra", "Это не нужно этой вещи", "This piece doesn't need that");
        leather("need_leather", "Не хватает кожи или ткани в ячейках стола", "Not enough leather or cloth in the table slots");
        leather("already_cut", "Уже раскроено: шейте", "Already cut: sew it");
        leather("need_knife", "Нужен нож в инвентаре: кожу кроят ножом", "You need a knife in your inventory: leather is cut with a knife");
        leather("need_shears", "Нужны ножницы в инвентаре: ткань кроят ножницами", "You need shears in your inventory: cloth is cut with shears");
        leather("cut_clean", "Раскроено точно по мелу — вещь выйдет лучше", "Cut right on the chalk — the piece will come out better");
        leather("cut_done", "Раскроено (срывов: %s). Теперь шейте", "Cut (slips: %s). Now sew it");
        leather("cut_rough", "Раскроено криво (срывов: %s) — вещь выйдет хуже", "A rough cut (slips: %s) — the piece will come out worse");
        leather("cut_sew", "Раскроено. Шейте иглой", "Cut. Sew it with the needle");
        leather("cut_extras", "Раскроено. Добавьте фурнитуру и шейте иглой", "Cut. Add the fittings and sew with the needle");
        leather("cut_first", "Сначала раскроите", "Cut it first");
        leather("need_extras", "Не хватает фурнитуры в ячейках стола", "Fittings are missing from the table slots");
        leather("need_thread", "Нужно ниток (или нити): %s", "You need thread: %s");
        leather("need_needle", "Нужна игла в инвентаре", "You need a needle in your inventory");
        leather("sewn_clean", "Ни одного промаха — вещь вышла лучше", "Not a single miss — the piece came out better");
        leather("sewn", "Сшито, промахов: %s", "Sewn, misses: %s");
        leather("sewn_sloppy", "Шов кривой (промахов: %s) — вещь вышла хуже", "A crooked seam (misses: %s) — the piece came out worse");
        leather("made", "Готово: %s", "Done: %s");
        leather("table_cut", "Раскроенное ещё не сшито — выкройку не сменить", "The cut piece isn't sewn yet — the pattern can't change");
        leather("item_oiled", "%s смазана", "%s oiled");
        leather("need_oil", "Нужно сало или воск в инвентаре", "You need salo or wax in your inventory");
    }

    private void solution(Solution solution, String ru, String en) {
        add(solution.langKey(), russian ? ru : en);
    }

    private void leather(String key, String ru, String en) {
        add("message.halgventmod.leather." + key, russian ? ru : en);
    }

    /** Готовка: очаг, горшок, жарка, печь, жёрнов, заготовки (cooking/*). */
    private void cookingTranslations() {
        // Окна очага, костра, печи и горна (HearthScreen, OvenScreen, KilnScreen)
        add(com.nrdhvnstd.halgventmod.registry.ModBlocks.FIRE_PIT.get(), russian ? "Костёр" : "Campfire");
        gui("hearth.fire", "Огонь", "Fire");
        gui("hearth.ignite", "Разжечь", "Light");
        gui("hearth.extinguish", "Затушить", "Put out");
        gui("hearth.heat_0", "Жара нет", "No heat");
        gui("hearth.heat_1", "Жар слабый", "Low heat");
        gui("hearth.heat_2", "Жар средний", "Medium heat");
        gui("hearth.heat_3", "Жар сильный", "High heat");
        gui("hearth.burns", "Гореть: %s", "Burns: %s");
        gui("hearth.out", "Не горит (%s)", "Out (%s)");
        gui("hearth.fuel", "Дрова:", "Firewood:");
        gui("hearth.pot", "Горшок", "Pot");
        gui("hearth.pot_scorched", "Горшок · пригорал %s", "Pot · scorched %s");
        gui("hearth.water", "Вода налита", "Water in");
        gui("hearth.no_water", "Нет воды: E ведром", "No water: E a bucket");
        gui("hearth.stir_in", "мешать через %s", "stir in %s");
        gui("hearth.cooking", "Варится %s%% · %s", "Cooking %s%% · %s");
        gui("hearth.stir", "Мешать", "Stir");
        gui("hearth.stir_now", "Пригорает — мешайте!", "Catching — stir!");
        gui("hearth.empty", "Вылить", "Empty");
        gui("hearth.remove_pot", "Снять", "Take off");
        gui("hearth.scorched", "пригорало: %s", "scorched: %s");
        gui("hearth.ladle_bowl", "миской", "a bowl");
        gui("hearth.ladle_bottle", "склянкой", "a bottle");
        gui("hearth.grill", "На углях", "On the coals");
        gui("hearth.grill_hint", "E куском мяса или рыбы — положить; E по куску — перевернуть", "E with meat or fish to lay it; E on a piece to flip it");
        gui("oven.hot", "Прогрета", "Hot");
        gui("oven.warming", "Прогрев: %s%%", "Warming: %s%%");
        gui("oven.inside", "В печи", "Inside");
        gui("oven.empty_place", "пусто", "empty");
        gui("oven.cold", "холодно", "too cold");
        gui("oven.left", "ждать %s", "wait %s");
        gui("oven.ready", "готово!", "done!");
        gui("oven.over", "пересыхает!", "drying out!");
        gui("oven.burnt", "сгорело", "burnt");
        gui("kiln.target", "к %s°", "to %s°");
        gui("kiln.graph", "Жар горна", "Kiln heat");
        gui("kiln.draft", "Тяга", "Draft");
        gui("kiln.draft_0", "0 · %s° тлеет", "0 · %s° smoulder");
        gui("kiln.draft_1", "1 · %s° закал", "1 · %s° temper");
        gui("kiln.draft_2", "2 · %s° греть", "2 · %s° warm");
        gui("kiln.draft_3", "3 · %s° обжиг", "3 · %s° fire");
        gui("kiln.draft_4", "4 · %s° обжиг", "4 · %s° fire");
        gui("kiln.draft_5", "5 · %s° пережог", "5 · %s° overfire");
        gui("kiln.step_0", "1 Закал", "1 Temper");
        gui("kiln.step_1", "2 Обжиг", "2 Firing");
        gui("kiln.step_2", "3 Остыть", "3 Cool");
        gui("kiln.st_going", "идёт", "going");
        gui("kiln.st_hot", "жарко!", "too hot!");
        gui("kiln.st_low", "мало жара", "too low");
        gui("kiln.st_zone", "в зоне", "in zone");
        gui("kiln.st_over", "пережог!", "overfire!");
        gui("kiln.st_cooling", "остывает", "cooling");
        gui("kiln.st_take", "вынимать", "take out");
        gui("kiln.fuel_left", "хватит на %s", "lasts %s");
        gui("kiln.fuel", "Дрова", "Firewood");
        gui("kiln.add_fuel", "дрова — в запас", "firewood into stock");
        gui("kiln.inside", "В горне %s из %s", "Inside %s of %s");
        // Бочка для выдержки (AgingCaskScreen)
        brewingMessage("barrel_sealed", "Бочка запечатана: сначала распечатайте (ПКМ — окно)", "The barrel is sealed: unseal it first (right-click)");
        brewingMessage("barrel_sealed_now", "Запечатано: бродит", "Sealed: fermenting");
        brewingMessage("barrel_unsealed", "Распечатано: брожение стоит", "Unsealed: fermentation stops");
        brewingMessage("barrel_open", "Открыта, налито %s: запечатайте, чтобы бродило (ПКМ — окно)", "Open, %s in: seal it to ferment (right-click)");
        brewingMessage("vessel_no", "Сюда не налить: сосуд треснул, запечатан, полон, с водой или в нём другое", "Can't pour here: the vessel is cracked, sealed, full, holds water or something else");
        add("message.halgventmod.cooking.vessel_no", russian ? "Сюда не налить: сосуд треснул, запечатан, полон, с водой или в нём другое" : "Can't pour here: the vessel is cracked, sealed, full, holds water or something else");
        brewingMessage("still_poured_vessel", "Налито крепкого: %s в %s", "Spirit poured: %s into %s");
        brewingMessage("still_nothing_to_pour", "Крепкого ещё нет", "No spirit yet");
        brewingMessage("still_vessel_no", "Сюда не налить: сосуд треснул, запечатан, полон, с водой или в нём другое", "Can't pour here: the vessel is cracked, sealed, full, holds water or something else");
        gui("cask.title", "Выдержка", "Ageing");
        gui("cask.into", "Влить", "Pour in");
        gui("cask.in", "питьё", "drink");
        gui("cask.empties", "пустое", "empties");
        gui("cask.vessel", "склянки, сосуд", "bottles, vessel");
        gui("cask.out", "налитое", "drawn");
        gui("cask.pour_out", "Вылить", "Pour out");
        gui("cask.measures", "налито %s из %s", "%s of %s in");
        gui("cask.empty", "Пусто: влейте вино, медовуху, сидр, крепкое или настойку. Пиво не выдерживают. "
                        + "Доливают то же питьё: качество смешивается.",
                "Empty: pour in wine, mead, cider, a spirit or a tincture. Beer is not aged. Top up with the same drink: "
                        + "the quality blends.");
        gui("cask.cellar", "Погреб: ступень качества за %s сут.", "Cellar: a quality step every %s days");
        gui("cask.house", "Под крышей: ступень за %s сут. (в погребе быстрее)", "Under a roof: a step every %s days (faster in a cellar)");
        gui("cask.exposed", "Под открытым небом: не выдерживается", "Under the open sky: nothing ages");
        gui("cask.placed", "Заложено: %s", "Laid down: %s");
        gui("cask.placed_today", "Заложено сегодня", "Laid down today");
        gui("cask.steps", "Выдержка: +%s к качеству", "Aged: +%s quality");
        gui("cask.next", "Следующая ступень: %s", "Next step: %s");
        gui("cask.next_never", "Дальше не выдерживается", "Ages no further");
        gui("cask.masterwork", "Лучше уже не станет", "It can't get any better");
        // Бродильная бочка (FermentingBarrelScreen)
        gui("barrel.into", "В бочку", "Into");
        gui("barrel.title", "Брожение", "Fermenting");
        gui("barrel.in", "влить", "pour in");
        gui("barrel.flavour", "добавка", "extra");
        gui("barrel.portions", "налито %s из %s", "%s of %s in");
        gui("barrel.with", "добавки:", "with:");
        gui("barrel.empty", "Пусто: влейте сусло или крепкое", "Empty: pour in must or spirit");
        gui("barrel.needs_flavour", "Крепкое без добавки не настаивается: положите мёд, травы или ягоды", "Spirit needs something to steep: add honey, herbs or berries");
        gui("barrel.fermenting", "Бродит до %s (ещё %s сут.)", "Fermenting until %s (%s days left)");
        gui("barrel.ready", "Готово! Разлейте до %s — потом скиснет", "Ready! Bottle it before %s — then it sours");
        gui("barrel.ready_open", "Готово! Открытая не киснет — разливайте", "Ready! Open, it won't sour — bottle it");
        gui("barrel.ready_tincture", "Настоялось — разливайте, не перестоит", "Steeped — bottle it, it won't spoil");
        gui("barrel.sour", "Скисло: выйдет уксус", "Turned sour: it gives vinegar");
        gui("barrel.vessel", "склянки, сосуд", "bottles, vessel");
        gui("barrel.out", "готовое", "ready");
        gui("barrel.pour_out", "Вылить", "Pour out");
        gui("barrel.seal", "Запечатать", "Seal");
        gui("barrel.unseal", "Распечатать", "Unseal");
        gui("barrel.open_hint", "Открыта — не бродит. Влейте и запечатайте", "Open — not fermenting. Pour in and seal it");
        gui("barrel.sealed_hint", "Запечатана: чтобы долить или разлить — распечатайте", "Sealed: unseal to add or bottle");
        // Перегонный куб (StillScreen)
        gui("still.title", "Куб", "Still");
        gui("still.in", "сырьё", "to distil");
        gui("still.poured", "залито %s из %s", "%s of %s in");
        gui("still.cold", "огня нет", "no fire");
        gui("still.hot", "жар сильный — первач", "too hot — harsh spirit");
        gui("still.working", "гонит: %s из %s до крепкого", "distilling: %s of %s to a spirit");
        gui("still.bottles", "склянки, сосуд", "bottles, vessel");
        gui("still.spirit", "крепкое: %s", "spirit: %s");
        gui("still.drain", "Слить", "Drain");
        gui("still.source_heat", "Огонь не очаговый: жар %s. Очагом под кубом можно править прямо отсюда.", "Not a hearth: heat %s. A hearth under the still can be run from here.");
        gui("still.source_none", "Под кубом нет огня: поставьте его на очаг или костёр.", "No fire under the still: put it on a hearth or campfire.");
        gui("kiln.smoke", "дымит: залощённое почернеет", "smoky: burnished ware turns black");
        gui("kiln.open_damper", "Открыть заслонку", "Open the damper");
        gui("kiln.close_damper", "Закрыть заслонку", "Close the damper");
        gui("recipes.title", "Что сварить", "What to cook");
        gui("recipes.hint", "Налейте воды (E ведром или кувшином), положите продукты в ячейки горшка и разожгите огонь. При среднем жаре варится около %s с. Когда полоса «мешать» краснеет — жмите «Мешать» (нужна поварёшка), иначе пригорит. Готовое черпают E миской или склянкой.",
                "Pour in water (E a bucket or jug), put food into the pot slots and light the fire. At medium heat it cooks in about %s s. When the stir bar turns red press Stir (you need a ladle) or it scorches. Ladle the result with E and a bowl or bottle.");
        recipe("meat_stew", "Мясная похлёбка", "Больше всего мяса (сырое, солонина, вяленое). Соль, травы, чеснок — приправы: вкуснее.", "Порций — по числу основ. Черпать миской.",
                "Meat stew", "Mostly meat (raw, salted, dried). Salt, herbs, garlic are seasoning: tastier.", "One portion per main ingredient. Ladle into a bowl.");
        recipe("fish_soup", "Уха", "Больше всего рыбы. С луком и травами — лучше.", "Порций — по числу рыб. Черпать миской.",
                "Fish soup", "Mostly fish. Better with onion and herbs.", "One portion per fish. Ladle into a bowl.");
        recipe("porridge", "Каша", "Зерно или мука: пшеница, рожь, ячмень, овёс — каша по зерну, которого больше. С молоком — сытнее, с мёдом — слаще.", "Порций — по числу основ. Черпать миской.",
                "Porridge", "Grain or flour: wheat, rye, barley, oats — named after the grain there is most of. Milk makes it richer, honey sweeter.", "One portion per main ingredient. Ladle into a bowl.");
        recipe("mushroom_soup", "Грибная похлёбка", "Больше всего грибов.", "Порций — по числу основ. Черпать миской.",
                "Mushroom soup", "Mostly mushrooms.", "One portion per main ingredient. Ladle into a bowl.");
        recipe("potato_stew", "Картофельная похлёбка", "Из овощей — только картофель (лук и чеснок не в счёт).", "Порций — по числу основ. Черпать миской.",
                "Potato stew", "Potatoes are the only vegetable (onion and garlic do not count).", "One portion per main ingredient. Ladle into a bowl.");
        recipe("vegetable_soup", "Овощная похлёбка", "Разные овощи: репа, капуста, морковь, свёкла, бобы. Три и больше разных основ — вкуснее.", "Порций — по числу основ. Черпать миской.",
                "Vegetable soup", "Mixed vegetables: turnip, cabbage, carrot, beetroot, beans. Three or more different bases taste better.", "One portion per main ingredient. Ladle into a bowl.");
        recipe("herbal_tea", "Отвар", "Только травы, без основы. С мёдом — лучше.", "По две склянки на пучок трав.",
                "Herbal tea", "Herbs only, nothing else. Better with honey.", "Two bottles per bunch of herbs.");
        recipe("wort", "Пивное сусло", "Солод: светлый, янтарный или тёмный — по самому тёмному. С травами — пряное, с мёдом — медовое.", "По две склянки на солод — в бродильную бочку.",
                "Beer wort", "Malt: pale, amber or dark — named after the darkest. Herbs make it spiced, honey makes it honeyed.", "Two bottles per malt — for the fermenting barrel.");
        recipe("honey_must", "Медовый взвар", "Только мёд с водой.", "По две склянки на мёд — в бродильную бочку, будет медовуха.",
                "Honey must", "Just honey and water.", "Two bottles per honey — for the fermenting barrel, it becomes mead.");

        add("tooltip.halgventmod.dish_food", russian ? "Сытость %s, жажда %s" : "Nutrition %s, thirst %s");
        add("tooltip.halgventmod.dish_of", russian ? "Сварено из: %s" : "Made of: %s");
        add("tooltip.halgventmod.pie_filling.meat", russian ? "Начинка: мясо" : "Filling: meat");
        add("tooltip.halgventmod.pie_filling.mushroom", russian ? "Начинка: грибы" : "Filling: mushrooms");
        add("tooltip.halgventmod.pie_filling.berry", russian ? "Начинка: ягоды" : "Filling: berries");

        add("gui.halgventmod.kitchen.title", russian ? "Рецепты" : "Recipes");
        add("gui.halgventmod.kitchen.ingredients", russian ? "Продукты" : "Ingredients");
        add("gui.halgventmod.kitchen.extras", russian ? "Добавки" : "Extras");
        add("gui.halgventmod.kitchen.kneads", russian ? "Месить: %s раз" : "Knead: %s times");
        add("tooltip.halgventmod.food_extras", russian ? "С добавками: %s (насыщение +%s)" : "With extras: %s (saturation +%s)");
        cooking("kitchen_extra", "%s — в начинку: насыщение +%s", "%s in the filling: saturation +%s");
        cooking("kitchen_extra_twice", "%s уже есть в начинке", "%s is already in the filling");
        cooking("kitchen_extras_full", "Добавок не больше %s", "No more than %s extras");
        add("message.halgventmod.bees.calmed", russian ? "Пчёлы притихли от дыма" : "The smoke calms the bees");
        add("message.halgventmod.bees.empty_smoke", russian ? "В улье никого нет" : "The hive is empty");
        add("message.halgventmod.bees.empty", russian ? "Улей пуст: ни пчёл, ни мёда" : "The hive is empty: no bees, no honey");
        add("message.halgventmod.bees.no_honey", russian ? "Мёда ещё нет" : "No honey yet");
        add("message.halgventmod.bees.stung", russian ? "Пчёлы жалят! Сначала окурите улей дымарём" : "The bees sting! Smoke the hive first");
        add("message.halgventmod.bees.waiting", russian ? "Пусто. Цветов рядом: %s — весной и летом заселится рой" : "Empty. Flowers nearby: %s — a swarm will settle in spring or summer");
        add("message.halgventmod.bees.few_flowers", russian ? "Пусто, и цветов мало: %s из %s. Посадите цветы рядом" : "Empty, and few flowers: %s of %s. Plant flowers nearby");
        add("message.halgventmod.bees.status", russian ? "Пчёлы живут. Мёда %s из %s, цветов рядом: %s" : "Bees live here. Honey %s of %s, flowers nearby: %s");
        cooking("kitchen_pick", "E пустой рукой — выбрать рецепт", "E with an empty hand picks a recipe");
        cooking("kitchen_busy", "Сначала доделайте или заберите выложенное", "Finish or take back what is on the board first");
        cooking("kitchen_take_first", "Сначала заберите готовое", "Take what is ready first");
        cooking("kitchen_has_water", "Воды уже хватает", "Enough water already");
        cooking("kitchen_has_salt", "Уже посолено", "Already salted");
        cooking("kitchen_enough", "%s хватает", "Enough %s");
        cooking("kitchen_spoiled", "Испорченное не берут", "Spoiled food won't do");
        cooking("kitchen_wrong", "Это не по рецепту", "Not in this recipe");
        cooking("kitchen_need", "Нужно ещё: %s × %s", "Still needed: %s × %s");
        cooking("kitchen_need_water", "Полейте водой из ведра или кувшина", "Pour in water from a bucket or a jug");
        cooking("kitchen_ready_to_knead", "Всё на доске: месите пустой рукой", "All on the board: knead with an empty hand");
        cooking("kitchen_knead", "%s / %s", "%s / %s");
        cooking("kitchen_kneaded", "Уже замешано — доделайте", "Already kneaded — finish it");
        cooking("kitchen_done", "Готово: %s", "Done: %s");
        cooking("diet_bored", "%s приелось: сытость %s%%", "Tired of %s: %s%% as filling");

        cooking("fuel_full", "Все три места под дрова заняты", "All three firewood places are full");
        cooking("fuel_added", "Подложили полено: %s из %s", "Log added: %s of %s");
        cooking("fuel_unlit", "Дрова сложены (%s из %s). Разожгите огнивом или факелом", "Firewood stacked (%s of %s). Light it with a flint or torch");
        cooking("already_lit", "Уже горит", "Already burning");
        cooking("no_fuel", "Нечему гореть: сначала дрова", "Nothing to burn: add firewood first");
        cooking("fire_status", "%s°, поленьев %s из %s. ПКМ — окно очага", "%s°, logs %s of %s. Right-click opens the hearth");
        cooking("fire_out", "Огонь не горит (поленьев %2$s из %3$s)", "The fire is out (logs %2$s of %3$s)");
        cooking("need_lighter", "Нечем разжечь: нужно огниво, огненный заряд или факел", "Nothing to light it with: you need a flint and steel, a fire charge or a torch");
        cooking("need_ladle", "Нужна поварёшка в инвентаре", "You need a ladle in your inventory");
        cooking("stir_not_yet", "Пока не пригорает — мешать рано", "Nothing is catching yet — too early to stir");
        cooking("pot_emptied", "Горшок опорожнён", "The pot is emptied");

        cooking("pot_already", "Горшок уже стоит", "A pot is already on");
        cooking("grill_busy", "Сначала снимите всё с углей", "Take everything off the coals first");
        cooking("still_pot", "Сначала снимите горшок", "Take the pot off first");
        cooking("still_blocked", "Над очагом нет места для куба", "No room above the hearth for the still");
        cooking("pot_has_water", "Вода уже налита", "There is water already");
        cooking("need_water", "Налейте в горшок воды: ведро или полный кувшин", "Pour water into the pot: a bucket or a full jug");
        cooking("pot_water_only", "Одна вода: положите продукты", "Just water: add some food");
        cooking("pot_full", "Горшок полон", "The pot is full");
        cooking("pot_serving", "Из горшка уже разливают — сначала доешьте", "The pot is being served — finish it first");
        cooking("pot_busy", "В горшке варится — не снять", "Something is cooking — can't take it off");
        cooking("pot_cold", "Положено %s из %s. Огонь не горит", "%s of %s added. The fire is out");
        cooking("pot_cooking", "Варится: %s%%, положено %s из %s", "Cooking: %s%%, %s of %s added");
        cooking("pot_stir", "Пригорает! Помешайте половником (%s%%, %s из %s)", "Scorching! Stir it with a ladle (%s%%, %s of %s)");
        cooking("stirred", "Помешали", "Stirred");
        cooking("pot_ready", "Готово: %s, порций %s", "Ready: %s, %s portions");
        cooking("portions_left", "Осталось порций: %s", "Portions left: %s");
        cooking("need_bowl", "Черпают миской", "Serve it with a bowl");
        cooking("need_bottle", "Отвар наливают в склянку", "Pour the tea into a bottle");
        cooking("grill_full", "На углях нет места", "No room on the coals");

        cooking("oven_full", "В печи нет места", "The oven is full");
        cooking("oven_cold", "Печь не прогрета (%s°): не печётся", "The oven is cold (%s°): nothing bakes");
        cooking("oven_status", "Жар %s, поленьев %s. Печётся: %s", "Heat %s, logs %s. Baking: %s");
        cooking("oven_status_empty", "Жар %s, поленьев %s. ПКМ — окно печи", "Heat %s, logs %s. Right-click opens the oven");
        cooking("baked_raw", "Ещё сырое", "Still raw");
        cooking("baked_under", "%s: недопечено", "%s: underbaked");
        cooking("baked_good", "%s: в самый раз", "%s: just right");
        cooking("baked_over", "%s: пересушено", "%s: overbaked");
        cooking("baked_burnt", "Сгорело", "Burnt");

        cooking("quern_full", "Жёрнов полон", "The quern is full");
        cooking("quern_grain", "Засыпано зерна: %s", "Grain in: %s");
        cooking("quern_empty", "Засыпьте пшеницу, рожь или черепки", "Pour in some wheat, rye or sherds");
        cooking("quern_mixed", "Сначала домелите или заберите то, что засыпано", "Finish grinding or take back what is in first");
        cooking("quern_turn", "%s / %s", "%s / %s");
        cooking("quern_flour", "Смолото! Осталось зерна: %s", "Ground! Grain left: %s");
        cooking("quern_spilled", "Спешка — зерно просыпалось. Осталось: %s", "Too hasty — the grain spilled. Left: %s");

        cooking("barrel_empty", "Положите сырое мясо или рыбу", "Put in raw meat or fish");
        cooking("barrel_full", "Бочка полна", "The barrel is full");
        cooking("barrel_spoiled", "Испорченное не солят", "Spoiled food is not salted");
        cooking("barrel_salt_enough", "Соли хватает", "Enough salt");
        cooking("barrel_need_salt", "В бочке %s, соли %s: нужно по соли на кусок", "%s in the barrel, salt %s: one salt per piece");
        cooking("barrel_salting", "Солится %s, ждать суток: %s", "Salting %s, days left: %s");
        cooking("barrel_take_first", "Сначала заберите готовое", "Take out what is ready first");
        cooking("barrel_done", "Засолено: %s", "Salted: %s");

        cooking("hook_empty", "Повесьте сырое мясо или рыбу — коптить, травы или грибы — сушить",
                "Hang raw meat or fish to smoke, herbs or mushrooms to dry");
        cooking("hook_full", "На крюке нет места", "No room on the hook");
        cooking("hook_mixed", "Мясо и травы вместе не вешают", "Meat and herbs don't hang together");
        cooking("hook_take_first", "Сначала снимите готовое", "Take off what is ready first");
        cooking("hook_no_smoke", "Внизу нет огня: не коптится. Повесьте над очагом или костром",
                "No fire below: it won't smoke. Hang it above a hearth or campfire");
        cooking("hook_no_smoke_status", "Коптится %s%%, но внизу нет огня", "Smoked %s%%, but there is no fire below");
        cooking("hook_smoking", "Коптится: %s%%", "Smoking: %s%%");
        cooking("hook_drying", "Сохнет: %s%%", "Drying: %s%%");
        cooking("hook_smoked", "Прокоптилось: %s", "Smoked: %s");
        cooking("hook_dried", "Высохло: %s", "Dried: %s");
    }

    private void cooking(String key, String ru, String en) {
        add("message.halgventmod.cooking." + key, russian ? ru : en);
    }

    /** Алкоголь (brewing/*): сообщения станций, опьянение, названия напитков по составу. */
    private void brewingTranslations() {
        brewing("press_mixed", "Разные плоды вместе не давят", "Different fruit can't be pressed together");
        brewing("press_take_juice", "Сначала наберите сок склянкой", "Draw the juice with a bottle first");
        brewing("press_full", "Давильня полна", "The press is full");
        brewing("press_fruit", "Плодов: %s, сока: %s", "Fruit: %s, juice: %s");
        brewing("press_no_juice", "Сока мало: %s из %s на склянку", "Not enough juice: %s of %s per bottle");
        brewing("press_empty", "Засыпьте яблоки или ягоды. Сока: %s", "Fill it with apples or berries. Juice: %s");
        brewing("press_spilled", "Неровно — плод раздавлен мимо. Осталось: %s", "Uneven — a fruit got squashed to waste. Left: %s");
        brewing("press_juice", "Сока: %s, плодов осталось: %s", "Juice: %s, fruit left: %s");
        brewing("press_turn", "Давите: %s/%s", "Pressing: %s/%s");
        brewing("barrel_pour_first", "Сначала разлейте готовое", "Draw what is ready first");
        brewing("barrel_mixed", "Разное в одну бочку не льют", "Different liquids don't go in one barrel");
        brewing("barrel_full", "Бочка полна", "The barrel is full");
        brewing("barrel_flavour_twice", "Эта добавка уже есть", "This extra is already in");
        brewing("barrel_flavours_full", "Добавок не больше %s", "No more than %s extras");
        brewing("barrel_flavour", "Добавлено: %s", "Added: %s");
        brewing("barrel_empty", "Влейте сусло или крепкое", "Pour in must or a spirit");
        brewing("barrel_poured_out", "Бочка вылита", "The barrel is emptied");
        brewing("barrel_tincture_needs", "Крепкого: %s. Для настойки добавьте ягоды, травы или мёд",
                "Spirit: %s. Add berries, herbs or honey for a tincture");
        brewing("barrel_sour", "Передержано — уксус (%s)", "Kept too long — vinegar (%s)");
        brewing("barrel_ready", "Перебродило: %s склянок. Разлейте до %s — потом скиснет", "Fermented: %s bottles. Bottle before %s or it sours");
        brewing("barrel_ready_open", "Перебродило: %s склянок. Открытая не киснет — разливайте", "Fermented: %s bottles. Open, it won't sour — bottle it");
        brewing("barrel_ready_tincture", "Настоялось: %s склянок — разливайте", "Steeped: %s bottles — bottle it");
        brewing("barrel_fermenting", "Бродит: %s склянок, готово к %s", "Fermenting: %s bottles, ready on %s");
        brewing("still_mixed", "Разное в куб не льют", "Different drinks don't go in the still together");
        brewing("still_take_first", "Сначала наберите готовое крепкое", "Draw the finished spirit first");
        brewing("still_full", "Куб полон", "The still is full");
        brewing("still_cold", "Внизу нет огня. Залито: %s, крепкого: %s (из %s склянок — одна)",
                "No fire below. Poured: %s, spirit: %s (one from %s bottles)");
        brewing("still_status", "Гонит. Залито: %s, крепкого: %s (из %s склянок — одна)",
                "Distilling. Poured: %s, spirit: %s (one from %s bottles)");
        brewing("still_hot", "Слишком жарко — крепкое выйдет хуже. Залито: %s, крепкого: %s (из %s — одна)",
                "Too hot — the spirit will be worse. Poured: %s, spirit: %s (one from %s)");
        brewing("cask_full", "Бочка полна", "The cask is full");
        brewing("cask_no_beer", "Пиво не выдерживают — пейте свежим", "Beer isn't aged — drink it fresh");
        brewing("cask_not_ageable", "Это не выдерживают: только вино, медовуху, сидр, крепкое, настойку",
                "That isn't aged: only wine, mead, cider, spirits and tinctures");
        brewing("cask_mixed", "В бочке другое питьё: разное не смешивают", "The cask holds another drink: they don't mix");
        brewing("cask_empty", "Бочка пуста: влейте вино, медовуху, сидр, крепкое или настойку",
                "The cask is empty: pour in wine, mead, cider, a spirit or a tincture");
        brewing("cask_cellar", "Погреб: налито %s из %s, качество растёт каждые %s сут.", "Cellar: %s of %s in, quality rises every %s days");
        brewing("cask_house", "Налито %s из %s, качество растёт каждые %s сут. В погребе — быстрее",
                "%s of %s in, quality rises every %s days. Faster in a cellar");
        brewing("cask_exposed", "Под открытым небом не выдерживается (налито %s из %s)", "Doesn't age under the open sky (%s of %s in)");
        brewing("cask_poured_out", "Бочка вылита", "The cask is emptied");
        brewing("cask_legacy", "Склянки из старой бочки — вам: теперь в бочке одно питьё", "Bottles from the old cask are yours: it now holds one drink");
        cooking("baked_malt", "Высушено: %s", "Dried: %s");

        drunk("tipsy", "Вы навеселе", "You are tipsy");
        drunk("drunk", "Вы пьяны", "You are drunk");
        drunk("wasted", "Вы мертвецки пьяны", "You are wasted");
        drunk("sober", "Вы протрезвели", "You are sober");
        drunk("hangover", "Похмелье: тяжёлая голова и жажда", "Hangover: a heavy head and thirst");
        drunk("cured", "Полегчало", "That's better");
        drunk("rotgut", "Сивуха! Мутит", "Rotgut! You feel sick");
        drunk("clear_head", "Вы протрезвели — голова ясная, похмелья нет", "You are sober — a clear head, no hangover");
        add("gamerule.drunkChat", russian ? "Пьяные путают буквы в чате" : "Drunk players slur in chat");

        drink("must.apple", "Яблочный сок", "Apple juice");
        drink("must.berry", "Ягодный сок", "Berry juice");
        drink("must.glowberry", "Сок светящихся ягод", "Glow berry juice");
        drink("must.pear", "Грушевый сок", "Pear juice");
        drink("must.grape", "Виноградный сок", "Grape juice");
        drink("must.cherry", "Вишнёвый сок", "Cherry juice");
        drink("must.plum", "Сливовый сок", "Plum juice");
        drink("cider.pear", "Грушевый сидр", "Perry");
        drink("wine.grape", "Виноградное вино", "Grape wine");
        drink("wine.cherry", "Вишнёвое вино", "Cherry wine");
        drink("wine.plum", "Сливовое вино", "Plum wine");
        drink("spirit.pear", "Грушевая водка", "Pear brandy");
        drink("spirit.cherry", "Вишнёвка", "Kirsch");
        drink("spirit.plum", "Сливовица", "Slivovitz");
        drink("tincture.pear", "Грушевая настойка", "Pear tincture");
        drink("tincture.cherry", "Вишнёвая настойка", "Cherry tincture");
        drink("tincture.plum", "Сливовая настойка", "Plum tincture");
        drink("must.honey", "Медовый взвар", "Honey must");
        drink("must.wort_pale", "Светлое сусло", "Pale wort");
        drink("must.wort_amber", "Янтарное сусло", "Amber wort");
        drink("must.wort_dark", "Тёмное сусло", "Dark wort");
        drink("beer.wort_pale", "Светлое пиво", "Pale beer");
        drink("beer.wort_amber", "Янтарное пиво", "Amber beer");
        drink("beer.wort_dark", "Тёмное пиво", "Dark beer");
        drink("cider.apple", "Сидр", "Cider");
        drink("wine.berry", "Ягодное вино", "Berry wine");
        drink("wine.glowberry", "Светящееся вино", "Glow wine");
        drink("mead.honey", "Медовуха", "Mead");
        drink("spirit.bread", "Хлебное вино", "Grain spirit");
        drink("spirit.brandy", "Бренди", "Brandy");
        drink("spirit.apple", "Яблочная водка", "Apple brandy");
        drink("spirit.mead", "Медовая водка", "Honey spirit");
        drink("tincture.bread", "Хлебная настойка", "Grain tincture");
        drink("tincture.brandy", "Настойка на бренди", "Brandy tincture");
        drink("tincture.apple", "Яблочная настойка", "Apple tincture");
        drink("tincture.mead", "Медовая настойка", "Honey tincture");
        drink("flag.honey", " с мёдом", " with honey");
        drink("flag.herbs", " на травах", " with herbs");
        drink("flag.berry", " с ягодами", " with berries");
    }

    /** Гончарное дело (pottery/*): бочка для замеса, круг, полка, горн, посуда. */
    private void potteryTranslations() {
        String[][] forms = {
                {"cup", "Кружка", "кружка", "Cup"}, {"bowl", "Миска", "миска", "Bowl"},
                {"jug", "Кувшин", "кувшин", "Jug"}, {"pot", "Горшок", "горшок", "Pot"},
                {"amphora", "Корчага", "корчага", "Amphora"}, {"lamp", "Светильник", "светильник", "Lamp"},
                {"crucible", "Тигель", "тигель", "Crucible"}};
        for (String[] form : forms) {
            add("pottery.halgventmod.form." + form[0], russian ? form[1] : form[3]);
            add("pottery.halgventmod.form_lower." + form[0], russian ? form[2] : form[3].toLowerCase());
        }
        add("item.halgventmod.greenware.named", russian ? "Сырец: %s" : "Greenware: %s");
        add("item.halgventmod.greenware.dried", russian ? "Сухой сырец: %s" : "Dry greenware: %s");
        add("item.halgventmod.greenware.cracked", russian ? "Треснувший сырец: %s" : "Cracked greenware: %s");

        // Подсказки
        potteryTip("fat_clay", "Жирная глина: трескается чаще", "Fat clay: cracks more often");
        potteryTip("grog_clay", "На шамоте: трескается реже", "Tempered with grog: cracks less often");
        potteryTip("wet", "Сырой — просушите на полке", "Wet — dry it on a shelf");
        potteryTip("dried", "Высох — можно в горн", "Dry — ready for the kiln");
        potteryTip("cracked", "Треснул — размочите в бочке для замеса", "Cracked — soak it back in a clay barrel");
        potteryTip("black", "Чернолощёная", "Black burnished");
        potteryTip("burnished", "Залощена", "Burnished");
        potteryTip("underfired", "Недожог: хрупкая", "Underfired: brittle");
        potteryTip("overfired", "Пережог: повело", "Overfired: warped");
        potteryTip("water", "Воды: %s из %s мер", "Water: %s of %s measures");
        potteryTip("inside", "Внутри: %s ×%s", "Inside: %s ×%s");
        potteryTip("inside_sealed", "Внутри: %s ×%s, под воском", "Inside: %s ×%s, sealed with wax");
        potteryTip("keeps", "Свежо ещё %s сут.; портится в %s раза медленнее", "Fresh for %s more days; spoils %s times slower");
        potteryTip("pot_uses", "Варок: %s из %s", "Cooked in: %s of %s times");
        potteryTip("lamp_lit", "Горит, масла на %s мин.", "Burning, oil for %s min.");
        potteryTip("lamp_fuel", "Масла на %s мин.", "Oil for %s min.");
        potteryTip("served_in", "Подано: %s", "Served in: %s");

        // Посуда в инвентаре
        pottery("amphora_has_drink", "В корчаге питьё — воды не налить", "There is a drink in the amphora — no room for water");
        pottery("water_poured", "Вылили воду", "Poured the water out");
        pottery("ware_cracked", "Треснула посуда: %s. Пользоваться ей больше нельзя", "Your %s has cracked and can't be used any more");
        pottery("ware_cracked_use", "Посуда треснула: можно только опорожнить. Shift + ПКМ — разбить на черепки",
                "It's cracked: you can only empty it. Shift + right-click to smash it into sherds");
        potteryTip("ware_cracked", "Треснула: только опорожнить, Shift + ПКМ — разбить", "Cracked: empty it only, Shift + right-click to smash");
        potteryTip("wear_chance", "Может треснуть: %s%% за раз", "May crack: %s%% per use");
        pottery("lamp_no_fuel", "Светильник пуст: положите на него сало или воск", "The lamp is empty: drop fat or wax onto it");
        pottery("lamp_no_fire", "Нечем зажечь: нужно огниво или факел", "Nothing to light it with: you need a flint and steel or a torch");
        pottery("lamp_full", "Светильник полон", "The lamp is full");
        pottery("empty_first", "Сначала опорожните", "Empty it first");
        pottery("has_water", "Внутри вода — сначала вылейте (Shift + ПКМ)", "There is water inside — pour it out first (Shift + right-click)");
        pottery("sealed", "Запечатано воском — ПКМ по сосуду, чтобы вскрыть", "Sealed with wax — right-click the vessel to break it");
        pottery("other_inside", "Внутри другое: в сосуде одно к одному", "Something else is inside: one kind per vessel");
        pottery("full", "Больше не влезет", "No more fits");
        pottery("seal_broken", "Печать сломана", "The seal is broken");
        pottery("need_bottle", "Наберите пустой склянкой", "Draw it with an empty bottle");
        pottery("seal_empty", "Запечатывать нечего — внутри пусто", "Nothing to seal — it is empty");
        pottery("sealed_now", "Запечатано воском", "Sealed with wax");

        // Бочка для замеса
        pottery("barrel_take_first", "Сначала заберите промятую глину (Shift + E)", "Take the kneaded clay first (Shift + E)");
        pottery("barrel_has_water", "Вода уже налита", "There is water already");
        pottery("barrel_temper_full", "Отощителя хватит", "Enough temper");
        pottery("barrel_full", "Больше %s комьев не замесить", "No more than %s lumps per batch");
        pottery("barrel_empty", "В бочке пусто: положите глину", "The barrel is empty: put some clay in");
        pottery("barrel_need_water", "Налейте воды: ведро или полный кувшин", "Pour in water: a bucket or a full jug");
        pottery("barrel_ready", "Промято! Гончарной глины: %s. Shift + E — забрать; полежит трое суток — станет лучше",
                "Kneaded! Potter's clay: %s. Shift + E to take it; left for three days it gets better");
        pottery("barrel_knead", "Месите: %s / %s", "Kneading: %s / %s");
        pottery("barrel_bubble", "Спешка — в глине пузыри (%s / %s)", "Too hasty — air bubbles in the clay (%s / %s)");
        pottery("barrel_aged", "Глина вылежалась: %s комьев. Shift + E — забрать", "The clay has aged: %s lumps. Shift + E to take it");
        pottery("barrel_aging", "Промятой глины: %s. Лежит %s из %s суток", "Kneaded clay: %s. Aging %s of %s days");
        pottery("barrel_mix_empty", "Отощителя: %s, вода: %s. Положите глину", "Temper: %s, water: %s. Put some clay in");
        pottery("barrel_mix_dry", "Глины: %s, отощителя: %s (%s%%). Налейте воды", "Clay: %s, temper: %s (%s%%). Pour in water");
        pottery("barrel_mix", "Глины: %s, отощителя: %s (%s%%, лучше около 33%%). Месите пустой рукой",
                "Clay: %s, temper: %s (%s%%, about 33%% is best). Knead with an empty hand");
        pottery("barrel_not_ready", "Сначала промните глину", "Knead the clay first");

        // Гончарный круг
        pottery("wheel_full", "На круге уже %s комьев", "There are %s lumps on the wheel already");
        pottery("wheel_lumps", "На круге: %s из %s комьев. E пустой рукой — сесть за круг",
                "On the wheel: %s of %s lumps. E with an empty hand to sit at it");
        pottery("wheel_empty", "Положите на круг гончарную глину", "Put potter's clay on the wheel");
        pottery("wheel_not_enough", "Не хватает глины: нужно %s комьев", "Not enough clay: %s lumps needed");
        pottery("wheel_unlike", "Не похоже на образец «%s» (сходство %s%%)", "Doesn't look like a %s (likeness %s%%)");
        pottery("wheel_done", "Готово: %s (сходство %s%%)", "Done: %s (likeness %s%%)");
        wheel("title", "Гончарный круг", "Potter's wheel");
        wheel("reset", "Смять", "Crumple");
        wheel("reset_done", "Смяли — ком заново", "Crumpled — a fresh lump");
        wheel("done", "Готово", "Done");
        wheel("mode_body", "К кругу", "Back to the wheel");
        wheel("mode_details", "Детали", "Details");
        wheel("spin_first", "Раскрутите круг: пробел", "Spin the wheel: space");
        wheel("no_scraps", "Нет обрезков: сначала срежьте лишнее", "No scraps: cut away some clay first");
        wheel("no_support", "Не на что опереться", "Nothing to rest on");
        wheel("cant_pull", "Вытягивать некуда", "Nowhere to pull it");
        wheel("smudged", "Круг медленный — глина смялась", "The wheel is slow — the clay got smudged");
        wheel("wrenched", "Ком сорвало с круга!", "The lump flew off the wheel!");
        wheel("flung", "Слишком быстро — нависающее сорвало", "Too fast — the overhang flew off");
        wheel("collapsed", "Обвал: глина без опоры", "Collapse: the clay had no support");
        wheel("detail_loose", "Не держится: лепите к сосуду или к детали", "It won't hold: build onto the vessel or a detail");
        wheel("need_lumps", "Нужно комьев: %s", "Lumps needed: %s");
        wheel("stopped_for_details", "Круг остановлен: лепите детали", "The wheel is stopped: shape the details");
        wheel("lumps", "Глины: %s", "Clay: %s");
        wheel("on_wheel", "На круге: %s", "On the wheel: %s");
        wheel("section", "Разрез через ось", "Section through the axis");
        wheel("slice", "Срез %s из %s (колесо мыши)", "Slice %s of %s (mouse wheel)");
        wheel("stopped", "стоит", "stopped");
        wheel("speed", "маховик", "flywheel");
        wheel("stats", "Глина %s/%s · обрезки %s · сходство %s%% · неровности %s",
                "Clay %s/%s · scraps %s · likeness %s%% · unevenness %s");
        wheel("unlike", "Сходство с образцом меньше %s%%", "Likeness is under %s%%");
        wheel("forecast", "Выйдет: %s сырец", "Result: %s greenware");
        wheel("hint_body", "ЛКМ — срезать, ПКМ — налепить, Shift+ЛКМ — вытянуть, пробел — маховик",
                "LMB cut, RMB add, Shift+LMB pull up, space spins");
        wheel("hint_details", "ЛКМ — убрать, ПКМ — налепить воксель, колесо мыши — глубина",
                "LMB remove, RMB add a voxel, mouse wheel — depth");
        wheel("problem.no_bottom", "Нет дна", "No bottom");
        wheel("problem.loose", "Что-то не держится", "Something won't hold");
        wheel("problem.details", "Не хватает ручки или носика", "A handle or a spout is missing");
        wheel("problem.too_much", "Глины больше, чем было", "More clay than there was");

        // Полка
        pottery("shelf_empty", "Полка пуста", "The shelf is empty");
        pottery("shelf_not_dry", "Сняли недосохшим (%s%%)", "Taken off before it dried (%s%%)");
        pottery("shelf_full", "На полке нет места", "No room on the shelf");
        pottery("shelf_open", "Под открытым небом: высохнет за %s сут., но может треснуть; дождь размочит",
                "Under the open sky: dries in %s days but may crack; rain will soak it");
        pottery("shelf_roof", "Под крышей: высохнет за %s сут.", "Under a roof: dries in %s days");
        pottery("burnish_nothing", "Лощить нечего: нужен сырец на полке", "Nothing to burnish: put greenware on the shelf");
        pottery("burnish_done_already", "Уже залощено", "Already burnished");
        pottery("burnish_too_dry", "Пересох — лощить поздно", "Too dry to burnish");
        pottery("burnish_too_wet", "Ещё сырой (%s%%) — лощат подсохший до кожетвёрдого",
                "Still wet (%s%%) — burnish it once it is leather-hard");
        pottery("burnished", "Залощили: в дыму горна станет чернолощёным", "Burnished: kiln smoke will turn it black");

        // Горн
        pottery("kiln_fuel_full", "Дров хватит", "Enough firewood");
        pottery("kiln_draft", "Тяга %s: горн тянет к %s°", "Draft %s: the kiln heads for %s°");
        pottery("kiln_already_fired", "Уже обожжено", "Already fired");
        pottery("kiln_cracked", "Треснувшее не обжигают — размочите в бочке", "Cracked pieces aren't fired — soak them in the barrel");
        pottery("kiln_wet", "Сначала высушите на полке", "Dry it on a shelf first");
        pottery("kiln_busy", "Горн горячий или в нём обожжённое — сначала выньте и остудите",
                "The kiln is hot or holds fired ware — take it out and let it cool first");
        pottery("kiln_full", "В горне нет места (%s)", "The kiln is full (%s)");
        pottery("kiln_loaded", "В горне: %s из %s. Дрова, огниво — и первую минуту не больше 350°",
                "In the kiln: %s of %s. Firewood, a flint — and keep it under 350° for the first minute");
        pottery("kiln_lit_already", "Уже горит", "Already burning");
        pottery("kiln_no_fuel", "Сначала дрова", "Firewood first");
        pottery("kiln_status_empty", "Жар %s°, дров %s%%. Обжигов: %s из %s", "Heat %s°, firewood %s%%. Firings: %s of %s");
        pottery("kiln_status", "Жар %s°, дров %s%%, обжиг %s%%. Тяга %s → %s° (держите 600–850°). ПКМ — окно горна",
                "Heat %s°, firewood %s%%, firing %s%%. Draft %s → %s° (hold 600–850°). Right-click opens the kiln");
        pottery("kiln_hot", "Горячо: %s°, обжиг %s%%! Вынутое горячим трескается. E ещё раз — вынуть всё равно",
                "Hot: %s°, firing %s%%! Ware taken out hot cracks. E again to take it anyway");
        pottery("kiln_cracked_out", "%s треснул в горне", "%s cracked in the kiln");
        pottery("kiln_broken", "Горн развалился: почините кирпичом (%s / %s)", "The kiln has fallen apart: mend it with bricks (%s / %s)");
        pottery("kiln_repaired", "Горн починен", "The kiln is mended");

        // Состояние станций по ПКМ (StationBlockEntity.report)
        add("hud.halgventmod.use.status", russian ? "Состояние: %s" : "Status: %s");
        pottery("shelf_where_open", "Под небом: сохнет %s сут., дождь размочит", "Under the sky: dries in %s days, rain soaks it");
        pottery("shelf_where_roof", "Под крышей: сохнет %s сут.", "Under a roof: dries in %s days");
        pottery("shelf_drying", "%s%%, ещё ~%s мин.", "%s%%, ~%s min. left");
        pottery("shelf_dry", "сухой — в горн", "dry — into the kiln");
        pottery("shelf_cracked", "треснул", "cracked");
        pottery("shelf_soaked", "размок", "soaked");
        pottery("draft", "тяга %s", "draft %s");
        pottery("kiln_report_loaded", "Загружено %s из %s. Дрова и огниво; первую минуту — не выше %s°",
                "Loaded %s of %s. Firewood and a flint; keep it under %s° for the first minute");
        pottery("kiln_report_temper", "Закал: %s° (не выше %s°), ещё ~%s мин. Дров %s%% → %s°",
                "Tempering: %s° (keep under %s°), ~%s min. left. Firewood %s%% → %s°");
        pottery("kiln_report_temper_hot", "Закал: %s° — выше %s°, будут трещины! Ещё ~%s мин. Дров %s%% → %s°",
                "Tempering: %s° — over %s°, it will crack! ~%s min. left. Firewood %s%% → %s°");
        pottery("kiln_report_firing", "Обжиг %s%%: %s° (держите %s–%s°), дров %s%% → %s°, заслонка %s, ещё ~%8$s мин.",
                "Firing %s%%: %s° (hold %s–%s°), firewood %s%% → %s°, damper %s, ~%8$s min. left");
        pottery("kiln_report_low", "Обжиг %s%%: %s° — холодно, нужно %s–%s°. Подкиньте дров (%s%% → %s°)",
                "Firing %s%%: %s° — too cold, need %s–%s°. Add firewood (%s%% → %s°)");
        pottery("kiln_report_over", "Обжиг %s%%: %s° — пережог! Держите не выше %4$s° (дров %5$s%% → %6$s°, заслонка %7$s)",
                "Firing %s%%: %s° — overfiring! Keep it under %4$s° (firewood %5$s%% → %6$s°, damper %7$s)");
        pottery("kiln_report_out", "Огонь погас: обжиг %s%%, %s°. Подкиньте дров и разожгите",
                "The fire is out: firing %s%%, %s°. Add firewood and light it");
        pottery("kiln_report_cooling", "Обожжено! Остывает: %s° — вынимайте ниже %s°", "Fired! Cooling: %s° — take it out below %s°");
        pottery("kiln_report_ready", "Обожжено и остыло (%s°): E — вынимать", "Fired and cool (%s°): E to take it out");
        leather("vat_report_done", "готово", "ready");
        leather("vat_report_idle", "не в этом растворе", "wrong bath");
        leather("vat_report_fire", "%s%%, нужен огонь под чаном", "%s%%, needs a fire under the vat");
        leather("vat_report_progress", "%s%%, ещё ~%s мин.", "%s%%, ~%s min. left");
        leather("rack_report_drying", "Сохнет: %s%%, ещё ~%s мин.", "Drying: %s%%, ~%s min. left");
        leather("rack_report_rain", "Дождь — не сохнет (%s%%)", "Rain — not drying (%s%%)");
        leather("rack_report_dry", "Высохла: E — снять; колотушка, жир или воск — отделка",
                "Dry: E to take it off; a mallet, fat or wax to finish it");
        leather("rack_report_finished", "Высохла и отделана: E — снять", "Dry and finished: E to take it off");
        cooking("hook_ready", "Готово (%s): E пустой рукой — снять", "Ready (%s): E with an empty hand to take it");

        // Очаг с глиняным горшком
        cooking("clay_pot_empty_first", "Сначала опорожните горшок", "Empty the pot first");
        cooking("clay_pot_cracked", "Горшок вываривался и треснул — остались черепки", "The pot wore out and cracked — only sherds left");
    }

    /**
     * Еда по таблице баланса: ванильные продукты под своими названиями, заготовки, эффекты в подсказке.
     * Новые предметы называются в ModItems.
     */
    private void foodTranslations() {
        if (russian) {
            add(net.minecraft.world.item.Items.PORKCHOP, "Свинина");
            add(net.minecraft.world.item.Items.BEEF, "Говядина");
            add(net.minecraft.world.item.Items.CHICKEN, "Курица");
            add(net.minecraft.world.item.Items.RABBIT, "Зайчатина");
            add(net.minecraft.world.item.Items.MUTTON, "Баранина");
            add(net.minecraft.world.item.Items.COOKED_PORKCHOP, "Жареная свинина");
            add(net.minecraft.world.item.Items.COOKED_BEEF, "Жареная говядина");
            add(net.minecraft.world.item.Items.COOKED_CHICKEN, "Жареная курица");
            add(net.minecraft.world.item.Items.COOKED_RABBIT, "Жареный заяц");
            add(net.minecraft.world.item.Items.COOKED_MUTTON, "Жареная баранина");
            add(net.minecraft.world.item.Items.SALMON, "Лосось");
            add(net.minecraft.world.item.Items.COOKED_SALMON, "Жареный лосось");
            add(net.minecraft.world.item.Items.POTATO, "Картофель");
            add(net.minecraft.world.item.Items.BAKED_POTATO, "Печёный картофель");
            add(net.minecraft.world.item.Items.CARROT, "Морковь");
            add(net.minecraft.world.item.Items.BEETROOT, "Свёкла");
            add(net.minecraft.world.item.Items.APPLE, "Яблоко");
        }
        add("item.halgventmod.raw_dish.named", russian ? "Заготовка: %s" : "Raw dish: %s");
        add("tooltip.halgventmod.food_effect.satiety", russian ? "Сытость: %s с" : "Satiety: %s s");
        add("tooltip.halgventmod.food_effect.well_fed", russian ? "Улучшенное насыщение: %s с" : "Well fed: %s s");
        add("tooltip.halgventmod.food_effect.raw", russian ? "Сырое: может отравить" : "Raw: may poison you");
        cooking("poisoned", "Отравление: мутит, еда идёт впрок вдвое хуже", "Food poisoning: you feel sick, food does half as much good");

        // Посадки: у грядок нет своих предметов, называем блоки
        add(com.nrdhvnstd.halgventmod.registry.ModBlocks.RYE.get(), russian ? "Рожь" : "Rye");
        add(com.nrdhvnstd.halgventmod.registry.ModBlocks.BARLEY_CROP.get(), russian ? "Ячмень" : "Barley");
        add(com.nrdhvnstd.halgventmod.registry.ModBlocks.OATS_CROP.get(), russian ? "Овёс" : "Oats");
        add(com.nrdhvnstd.halgventmod.registry.ModBlocks.ONION_CROP.get(), russian ? "Лук" : "Onions");
        add(com.nrdhvnstd.halgventmod.registry.ModBlocks.GARLIC_CROP.get(), russian ? "Чеснок" : "Garlic");
        add(com.nrdhvnstd.halgventmod.registry.ModBlocks.TURNIP_CROP.get(), russian ? "Репа" : "Turnips");
        add(com.nrdhvnstd.halgventmod.registry.ModBlocks.BEANS_CROP.get(), russian ? "Бобы" : "Beans");
        add(com.nrdhvnstd.halgventmod.registry.ModBlocks.CABBAGE_CROP.get(), russian ? "Капуста" : "Cabbage");
        add(com.nrdhvnstd.halgventmod.registry.ModBlocks.FLAX_CROP.get(), russian ? "Лён" : "Flax");
        add("entity.halgventmod.goose", russian ? "Гусь" : "Goose");
        add("message.halgventmod.farming.not_ripe", russian ? "Ещё не поспело" : "Not ripe yet");
    }

    /** Ткачество: чесалка, веретено, станок (weaving/*). */
    private void weavingTranslations() {
        weaving("comb_nothing", "Нечего чесать: нужен лён или шерсть в инвентаре", "Nothing to comb: you need flax or wool in your inventory");
        weaving("spin_nothing", "Нечего прясть: нужна кудель в инвентаре", "Nothing to spin: you need fibre in your inventory");
        weaving("weights_full", "Все %s грузил уже висят", "All %s weights are hung already");
        weaving("weight_hung", "Грузило подвешено: %s / %s", "Weight hung: %s / %s");
        weaving("need_weights", "Сначала подвесьте грузила: %s / %s — без них основу не натянуть",
                "Hang the weights first: %s / %s — the warp won't hold without them");
        weaving("need_warp", "Сначала натяните основу пряжей: %s / %s", "Set the warp with yarn first: %s / %s");
        weaving("warp_full", "Основа натянута. Пряжа для утка берётся из инвентаря — тките челноком",
                "The warp is set. Weft yarn is taken from your inventory — weave with the shuttle");
        weaving("warp_other", "Основа уже из другого волокна: нужна %s", "The warp is another fibre: you need %s");
        weaving("warp_added", "Основа: %s / %s", "Warp: %s / %s");
        weaving("warp_ready", "Основа натянута (%s / %s). Тките челноком", "The warp is set (%s / %s). Weave with the shuttle");
        weaving("need_weft", "Нужна %s в инвентаре на уток", "You need %s in your inventory for the weft");
        add("gui.halgventmod.weaving.title", russian ? "Ткацкий станок" : "Loom");
        add("gui.halgventmod.weaving.title_of", russian ? "Тканьё: %s" : "Weaving: %s");
        add("gui.halgventmod.weaving.progress", russian ? "Прокидки: %s / %s · изъяны: %s" : "Passes: %s / %s · flaws: %s");
        add("gui.halgventmod.weaving.hint_throw", russian ? "ЛКМ или Пробел — бросок, пока зев открыт"
                : "Click or Space to throw the shuttle while the shed is open");
        add("gui.halgventmod.weaving.hint_beat", russian ? "Зажмите и отпустите в зелёной зоне"
                : "Hold and release in the green zone");
        add("gui.halgventmod.weaving.no_weft", russian ? "Нет пряжи на уток: %s" : "No weft yarn: %s");
        add("gui.halgventmod.weaving.done", russian ? "Готово — снимите пустой рукой" : "Done — take it off by hand");
        add("gui.halgventmod.weaving.even", russian ? "Ровно" : "Even");
        add("gui.halgventmod.weaving.even_combo", russian ? "Ровно ×%s" : "Even ×%s");
        add("gui.halgventmod.weaving.loose", russian ? "Рыхло — в ткани просвет" : "Loose — a gap in the cloth");
        add("gui.halgventmod.weaving.tight", russian ? "Туго — уток перетянут, кромка сужается" : "Tight — the weft pulls the edges in");
        add("gui.halgventmod.weaving.snag", russian ? "Зев сомкнулся — челнок зацепил основу" : "The shed closed — the shuttle snagged the warp");
        weaving("woven", "Ткань готова — снимите её пустой рукой", "The cloth is done — take it off with an empty hand");
        weaving("take_cloth", "Сначала снимите готовую ткань пустой рукой", "Take the finished cloth off with an empty hand first");
        weaving("weaving", "Ткань ещё не готова: %s / %s", "The cloth isn't done yet: %s / %s");
        weaving("cloth_taken", "Снято: %s × %s (изъянов: %s)", "Taken off: %s × %s (flaws: %s)");
        weaving("cloth_taken_even", "Снято: %s × %s — ни одного изъяна, ткань вышла лучше",
                "Taken off: %s × %s — not a single flaw, the cloth came out better");
        weaving("report_weights", "Грузила: %s / %s. Подвесьте их, потом натяните основу пряжей",
                "Weights: %s / %s. Hang them, then set the warp with yarn");
        weaving("report_warp", "Основа: %s / %s мотков пряжи одного волокна", "Warp: %s / %s skeins of one fibre");
        weaving("report_weaving", "Тканьё: %s / %s прокидок, изъянов — %s. Уток — %s из инвентаря; E челноком — ткать",
                "Weaving: %s / %s passes, %s flaws. Weft — %s from your inventory; E with the shuttle to weave");
        weaving("report_done", "Готово: %s — снимите пустой рукой", "Done: %s — take it off with an empty hand");
    }

    private void weaving(String key, String ru, String en) {
        add("message.halgventmod.weaving." + key, russian ? ru : en);
    }

    /** Плотничество: колода для колки, пильные козлы, верстак плотника (carpentry/*). */
    private void carpentryTranslations() {
        // Колода для колки
        carpentry("block_busy", "На колоде уже стоит бревно", "There is a log on the block already");
        carpentry("not_firewood", "Это дерево на дрова не годится — не горит", "This wood won't do for firewood: it doesn't burn");
        carpentry("log_on_block", "Бревно на колоде — колите топором с полного замаха", "The log is on the block: split it with full axe swings");
        carpentry("need_log_block", "Сначала поставьте на колоду бревно", "Set a log on the block first");
        carpentry("chop_rushed", "Топор соскользнул — бейте с полного замаха", "The axe glanced off: strike with a full swing");
        carpentry("chop_progress", "Колете: %s / %s", "Splitting: %s / %s");
        carpentry("split", "Расколото: %s полена дров", "Split: %s pieces of firewood");
        carpentry("split_bark", "Расколото: %s полена дров, отошла кора", "Split: %s pieces of firewood, and the bark came off");
        carpentry("report_block_empty", "Колода пуста. E бревном — поставить, E топором — колоть", "The block is empty. E with a log sets it, E with an axe splits");
        carpentry("report_block", "На колоде: %s — %s / %s удара. E топором — колоть, пустой рукой — снять",
                "On the block: %s — %s / %s strikes. E with an axe splits, an empty hand takes it off");
        // Пильные козлы
        carpentry("horse_busy", "На козлах уже лежит бревно", "There is a log on the sawhorse already");
        carpentry("log_on_horse", "Бревно на козлах. E пилой — %s пропила по разметке", "The log is on the sawhorse. E with a saw: %s cuts along the lines");
        carpentry("need_log_horse", "Сначала положите на козлы бревно", "Lay a log on the sawhorse first");
        carpentry("sawn", "Распилено: досок — %s, кривых пропилов — %s (горбыль — на дрова)",
                "Sawn: %s planks, %s crooked cuts (the offcuts go for firewood)");
        carpentry("sawn_even", "Распилено ровно: досок — %s", "Sawn true: %s planks");
        carpentry("sawing", "Бревно уже в работе: %s / %s пропилов — допилите", "The log is being sawn: %s / %s cuts, finish it");
        carpentry("report_horse_empty", "Козлы пусты. E бревном — положить, E пилой — пилить", "The sawhorse is empty. E with a log lays it on, E with a saw saws");
        carpentry("report_horse", "На козлах: %s — пропилов %s / %s, кривых — %s. E пилой — пилить",
                "On the sawhorse: %s — %s / %s cuts, %s crooked. E with a saw saws");
        // Верстак плотника
        carpentry("pick_product", "Выберите изделие в окне верстака", "Pick a product in the bench window");
        carpentry("need_materials", "На верстаке не хватает материалов", "The bench is missing materials");
        carpentry("need_tool", "Нужен инструмент в инвентаре: %s", "You need a tool in your inventory: %s");
        carpentry("product", "Изделие: %s — %s", "Product: %s — %s");
        carpentry("made", "Сделано: %s", "Made: %s");
        carpentry("worked", "Работа: %s из 100", "Work: %s of 100");
        carpentry("worked_clean", "Работа: %s из 100 — изделие вышло лучше", "Work: %s of 100: the piece came out better");
        carpentry("worked_rough", "Работа: %s из 100 — изделие вышло хуже", "Work: %s of 100: the piece came out worse");
        gui("carpenter_bench.products", "Изделия", "Products");
        gui("carpenter_bench.on_bench", "На верстаке", "On the bench");
        gui("carpenter_bench.work", "Делать", "Make");
        gui("carpenter_bench.planks", "доски", "planks");
        add("tooltip.halgventmod.quality.durability", russian ? "Прочность ×%s" : "Durability ×%s");
        // Ванильная миска — деревянная, с верстака плотника (глиняная — «Миска» гончара)
        add(net.minecraft.world.item.Items.BOWL, russian ? "Деревянная миска" : "Wooden bowl");

        // Окно распила
        gui("sawing.title", "Пильные козлы", "Sawhorse");
        gui("sawing.title_of", "Распил: %s", "Ripping: %s");
        gui("sawing.progress", "Пропил %s / %s · кривых: %s · зажимало: %s", "Cut %s / %s · crooked: %s · jammed: %s");
        gui("sawing.hint", "ЛКМ или Пробел — нажать на толчке · мышь — куда вести пилу",
                "Left click or Space presses on the push · the mouse steers the saw");
        gui("sawing.done", "Распилено — доски у вас", "Sawn: the planks are yours");
        gui("sawing.even", "Ровный пропил", "A true cut");
        gui("sawing.crooked", "Пропил увело — доска уйдёт в горбыль", "The cut wandered: that plank is an offcut");
        gui("sawing.jam", "Пилу зажало — жмите только на толчке", "The saw jammed: press only on the push");
        gui("sawing.wrong", "Пила вязнет — это обратный ход", "The saw binds: that's the return stroke");
        gui("sawing.knot", "Сучок дёрнул пилу — в сучке точнее", "The knot jerked the saw: be precise in knots");
        gui("sawing.crack", "Трещина тянет пилу", "The crack pulls the saw");
        gui("sawing.tempo", "Ровно ×%s — темп растёт", "Steady ×%s: the pace picks up");
        gui("sawing.push", "толчок", "push");
        gui("sawing.pull", "назад", "back");
        gui("sawing.heat", "нагрев", "heat");
        gui("sawing.pace", "темп ×%s", "pace ×%s");

        // Работа на верстаке: этапы
        gui("bench_work.title", "Верстак плотника", "Carpenter's bench");
        gui("bench_work.title_of", "Изделие: %s", "Making: %s");
        gui("bench_work.stage", "Этап %s / %s: %s", "Step %s / %s: %s");
        gui("bench_work.stage_done", "Этап готов: %s из 100", "Step done: %s of 100");
        gui("bench_work.finishing", "Изделие готово…", "The piece is ready…");
        gui("bench_work.quality", "Выйдет: %s", "Will come out: %s");
        gui("bench_work.grade_0", "ужасно", "terrible");
        gui("bench_work.grade_1", "плохо", "poor");
        gui("bench_work.grade_2", "посредственно", "mediocre");
        gui("bench_work.grade_3", "добротно", "fine");
        gui("bench_work.grade_4", "отменно", "excellent");
        gui("bench_work.grade_5", "шедевр", "a masterwork");
        gui("bench_work.op.plane", "строгание", "planing");
        gui("bench_work.op.chisel", "выборка долотом", "chiselling");
        gui("bench_work.op.drill", "сверление", "drilling");
        gui("bench_work.op.whittle", "обстругивание ножом", "whittling");
        gui("bench_work.op.nail", "сборка", "assembly");
        // Строгание
        gui("bench_work.plane_status", "Неровность: %s · задиры: %s · перестрогано: %s",
                "Unevenness: %s · tear-outs: %s · overcut: %s");
        gui("bench_work.plane_hint_right", "Зажмите ЛКМ, ведите вправо по волокну · колесо — стружка: %s · Enter — хватит",
                "Hold left click, push right with the grain · wheel: shaving %s · Enter: enough");
        gui("bench_work.plane_hint_left", "Зажмите ЛКМ, ведите влево по волокну · колесо — стружка: %s · Enter — хватит",
                "Hold left click, push left with the grain · wheel: shaving %s · Enter: enough");
        gui("bench_work.depth_0", "тончайшая", "finest");
        gui("bench_work.depth_1", "тонкая", "thin");
        gui("bench_work.depth_2", "средняя", "medium");
        gui("bench_work.depth_3", "толстая", "thick");
        gui("bench_work.plane_against", "Задир — против волокна", "Tear-out: against the grain");
        gui("bench_work.plane_knot", "Сучок вырвало — тише на сучках", "The knot tore out: slow down over knots");
        // Долото
        gui("bench_work.chisel_status", "Выбрано: %s / %s · сколов: %s · прочих огрехов: %s",
                "Removed: %s / %s · chips: %s · other flaws: %s");
        gui("bench_work.chisel_hint", "ЛКМ — лёгкий удар, ПКМ — сильный: середину сильными, у края и по дну — лёгкими",
                "Left click taps, right click strikes hard: the middle hard, the edges and the floor lightly");
        gui("bench_work.chisel_chip", "Скол вдоль волокна — у края бейте легко", "A split along the grain: tap near the edge");
        gui("bench_work.chisel_punch", "Дно пробито — по тонкому дну легко", "Punched through: tap a thin floor lightly");
        gui("bench_work.chisel_gouge", "Мимо гнезда — задир на лице", "Off the mortise: a gouge on the face");
        // Сверление
        gui("bench_work.drill_status", "Дыра %s / %s · глубина %s%% · обороты %s/с", "Hole %s / %s · depth %s%% · %s turns/s");
        gui("bench_work.drill_hint", "Зажмите ЛКМ и крутите мышью по часовой вокруг метки, в зелёном",
                "Hold left click and circle the mouse clockwise around the mark, in the green");
        gui("bench_work.drill_hint_end", "Выход — тише! Медленные обороты, иначе вырвет с изнанки",
                "Breaking through: slow down, or it blows out the back");
        gui("bench_work.drill_wander", "Сверло увело — крутите медленнее", "The bit wandered: turn slower");
        gui("bench_work.drill_blowout", "Вырвало с изнанки", "Blown out at the back");
        gui("bench_work.drill_clean", "Чистая дыра", "A clean hole");
        gui("bench_work.drill_done", "Просверлено", "Drilled");
        // Нож
        gui("bench_work.whittle_status", "Отход от разметки: %s · задиры: %s · перерезано: %s",
                "Off the line: %s · tear-outs: %s · overcut: %s");
        gui("bench_work.whittle_hint_right", "Зажмите ЛКМ и ведите нож вправо по разметке, от себя · Enter — хватит",
                "Hold left click and draw the knife right along the line · Enter: enough");
        gui("bench_work.whittle_hint_left", "Зажмите ЛКМ и ведите нож влево по разметке, от себя · Enter — хватит",
                "Hold left click and draw the knife left along the line · Enter: enough");
        gui("bench_work.whittle_against", "Нож задрал — против волокна", "The knife tore: against the grain");
        gui("bench_work.whittle_slip", "Нож сорвался на сучке", "The knife slipped on a knot");
        // Сборка
        gui("bench_work.nail_status", "Гвоздь %s / %s · погнуто: %s · вмятин: %s", "Nail %s / %s · bent: %s · dents: %s");
        gui("bench_work.wedge_status", "Насадка %s / %s · погнуто: %s · вмятин: %s", "Fitting %s / %s · bent: %s · dents: %s");
        gui("bench_work.nail_hint", "Зажмите ЛКМ — замах, отпустите, когда гвоздь стоит прямо; сила — в зелёном",
                "Hold left click to swing, release while the nail stands straight; strength in the green");
        gui("bench_work.nail_bent", "Гвоздь погнулся — забейте новый", "The nail bent: drive a new one");
        gui("bench_work.nail_dent", "Забит, но молоток оставил вмятину", "Driven, but the hammer left a dent");
        gui("bench_work.nail_home", "Забит чисто", "Driven home cleanly");
        gui("bench_work.nail_weak", "Слабо — бейте крепче", "Too light: strike harder");
    }

    private void carpentry(String key, String ru, String en) {
        add("message.halgventmod.carpentry." + key, russian ? ru : en);
    }

    // Создание персонажа: окно, облик, предыстории, команда /character
    private void characterTranslations() {
        add("gui.halgventmod.creation.title", russian ? "Создание персонажа" : "Character Creation");
        add("gui.halgventmod.creation.step.appearance", russian ? "Облик" : "Appearance");
        add("gui.halgventmod.creation.step.backstory", russian ? "Предыстория" : "Backstory");
        add("gui.halgventmod.creation.step.spawn", russian ? "Точка появления" : "Starting Point");
        add("gui.halgventmod.creation.step.summary", russian ? "Итог" : "Summary");
        add("gui.halgventmod.creation.step.name", russian ? "Имя" : "Name");
        add("gui.halgventmod.creation.back", russian ? "Назад" : "Back");
        add("gui.halgventmod.creation.next", russian ? "Далее" : "Next");
        add("gui.halgventmod.creation.enter", russian ? "Войти в мир" : "Enter the World");
        add("gui.halgventmod.creation.random", russian ? "Случайный облик" : "Random Look");
        add("gui.halgventmod.creation.kit", russian ? "Комплект:" : "Starting kit:");
        add("gui.halgventmod.creation.backstory.hint", russian ? "Выберите, кем был ваш персонаж до начала пути. От этого зависит, с чем он начнёт." : "Choose who your character was before the journey. It decides what they start with.");
        add("gui.halgventmod.creation.backstory.none", russian ? "Предыстории не найдены: персонаж начнёт без комплекта." : "No backstories found: the character starts without a kit.");
        add("gui.halgventmod.creation.spawn.hint", russian ? "Выберите, где начнётся ваш путь." : "Choose where your journey begins.");
        add("gui.halgventmod.creation.spawn.none", russian ? "Места появления пока не назначены: вы начнёте там, где стоите." : "No starting points have been set yet: you will start where you stand.");
        add("gui.halgventmod.creation.summary.backstory", russian ? "Предыстория: %s" : "Backstory: %s");
        add("gui.halgventmod.creation.summary.no_backstory", russian ? "нет" : "none");
        add("gui.halgventmod.creation.summary.spawn", russian ? "Точка появления: %s" : "Starting point: %s");
        add("gui.halgventmod.creation.summary.spawn_here", russian ? "там, где стоите" : "where you stand");
        add("gui.halgventmod.creation.name.label", russian ? "Имя персонажа" : "Character name");
        add("gui.halgventmod.creation.name.hint", russian ? "Только кириллица; слова через пробел или дефис. После имени можно римское число из I, V и X — например, «Гюнтер III». От %s до %s знаков." : "Cyrillic letters only; words separated by spaces or hyphens. A Roman numeral of I, V and X may follow the name, e.g. «Гюнтер III». %s to %s characters.");
        add("gui.halgventmod.creation.name.edit", russian ? "Снаряжение и место у персонажа останутся прежними: меняются только облик и имя." : "Your gear and location stay the same: only the look and name change.");
        add("gui.halgventmod.creation.error.appearance", russian ? "Облик не принят: выберите его заново." : "The look was not accepted: choose it again.");
        add("gui.halgventmod.creation.error.backstory", russian ? "Такой предыстории больше нет: выберите другую." : "That backstory no longer exists: choose another.");
        add("gui.halgventmod.creation.error.spawn", russian ? "Такой точки появления больше нет: выберите другую." : "That starting point no longer exists: choose another.");
        add("gui.halgventmod.creation.error.name_length", russian ? "Имя должно быть от 2 до 32 знаков." : "The name must be 2 to 32 characters long.");
        add("gui.halgventmod.creation.error.name_characters", russian ? "В имени только кириллица, пробел и дефис между словами; римское число из I, V, X — после имени." : "Only Cyrillic letters, with spaces or hyphens between words; a Roman numeral of I, V, X may follow the name.");
        add("gui.halgventmod.point.spawn", russian ? "Точка появления" : "Starting point");
        add("gui.halgventmod.point.spawn.tooltip", russian ? "Новые персонажи смогут выбрать это место, чтобы начать игру здесь." : "New characters can pick this place to start the game here.");
        add("appearance.halgventmod.sex", russian ? "Пол" : "Sex");
        add("appearance.halgventmod.sex.male", russian ? "Мужской" : "Male");
        add("appearance.halgventmod.sex.female", russian ? "Женский" : "Female");
        add("appearance.halgventmod.age", russian ? "Возраст" : "Age");
        add("appearance.halgventmod.age.young", russian ? "Молодой" : "Young");
        add("appearance.halgventmod.age.mature", russian ? "Зрелый" : "Mature");
        add("appearance.halgventmod.age.old", russian ? "Пожилой" : "Old");
        add("appearance.halgventmod.build", russian ? "Телосложение" : "Build");
        add("appearance.halgventmod.build.sturdy", russian ? "Крепкое" : "Sturdy");
        add("appearance.halgventmod.build.slim", russian ? "Худощавое" : "Slim");
        add("appearance.halgventmod.skin", russian ? "Цвет кожи" : "Skin");
        add("appearance.halgventmod.skin.pale", russian ? "Бледная" : "Pale");
        add("appearance.halgventmod.skin.fair", russian ? "Светлая" : "Fair");
        add("appearance.halgventmod.skin.ruddy", russian ? "Румяная" : "Ruddy");
        add("appearance.halgventmod.skin.olive", russian ? "Смуглая" : "Olive");
        add("appearance.halgventmod.skin.tan", russian ? "Загорелая" : "Tanned");
        add("appearance.halgventmod.skin.dark", russian ? "Тёмная" : "Dark");
        add("appearance.halgventmod.eyes", russian ? "Глаза" : "Eyes");
        add("appearance.halgventmod.eyes.blue", russian ? "Голубые" : "Blue");
        add("appearance.halgventmod.eyes.grey", russian ? "Серые" : "Grey");
        add("appearance.halgventmod.eyes.green", russian ? "Зелёные" : "Green");
        add("appearance.halgventmod.eyes.hazel", russian ? "Ореховые" : "Hazel");
        add("appearance.halgventmod.eyes.brown", russian ? "Карие" : "Brown");
        add("appearance.halgventmod.eyes.dark", russian ? "Тёмные" : "Dark");
        add("appearance.halgventmod.hair", russian ? "Причёска" : "Hair");
        add("appearance.halgventmod.hair.bald", russian ? "Лысый" : "Bald");
        add("appearance.halgventmod.hair.short", russian ? "Короткая" : "Short");
        add("appearance.halgventmod.hair.bowl", russian ? "Под горшок" : "Bowl cut");
        add("appearance.halgventmod.hair.long", russian ? "Длинная" : "Long");
        add("appearance.halgventmod.hair.braid", russian ? "Коса" : "Braid");
        add("appearance.halgventmod.hair_color", russian ? "Цвет волос" : "Hair colour");
        add("appearance.halgventmod.hair_color.black", russian ? "Чёрные" : "Black");
        add("appearance.halgventmod.hair_color.dark_brown", russian ? "Тёмно-русые" : "Dark brown");
        add("appearance.halgventmod.hair_color.brown", russian ? "Русые" : "Brown");
        add("appearance.halgventmod.hair_color.red", russian ? "Рыжие" : "Red");
        add("appearance.halgventmod.hair_color.blond", russian ? "Светлые" : "Blond");
        add("appearance.halgventmod.hair_color.ash", russian ? "Пепельные" : "Ash");
        add("appearance.halgventmod.beard", russian ? "Борода" : "Beard");
        add("appearance.halgventmod.beard.none", russian ? "Нет" : "None");
        add("appearance.halgventmod.beard.stubble", russian ? "Щетина" : "Stubble");
        add("appearance.halgventmod.beard.mustache", russian ? "Усы" : "Moustache");
        add("appearance.halgventmod.beard.short", russian ? "Короткая" : "Short");
        add("appearance.halgventmod.beard.full", russian ? "Окладистая" : "Full");
        add("appearance.halgventmod.beard.long", russian ? "Длинная" : "Long");
        add("appearance.halgventmod.scar", russian ? "Шрам" : "Scar");
        add("appearance.halgventmod.scar.none", russian ? "Нет" : "None");
        add("appearance.halgventmod.scar.cheek", russian ? "На щеке" : "Cheek");
        add("appearance.halgventmod.scar.brow", russian ? "Через бровь" : "Across the brow");
        add("appearance.halgventmod.scar.lip", russian ? "На губе" : "Lip");
        add("appearance.halgventmod.scar.eye", russian ? "Через глаз" : "Across the eye");
        add("appearance.halgventmod.tattoo", russian ? "Татуировка" : "Tattoo");
        add("appearance.halgventmod.tattoo.none", russian ? "Нет" : "None");
        add("appearance.halgventmod.tattoo.cheek", russian ? "Узор на щеке" : "Cheek pattern");
        add("appearance.halgventmod.tattoo.brow", russian ? "Знак на лбу" : "Forehead sign");
        add("appearance.halgventmod.tattoo.chin", russian ? "Полосы на подбородке" : "Chin stripes");
        add("appearance.halgventmod.voice", russian ? "Голос" : "Voice");
        add("appearance.halgventmod.voice.male_1", russian ? "Мужской I" : "Male I");
        add("appearance.halgventmod.voice.male_2", russian ? "Мужской II" : "Male II");
        add("appearance.halgventmod.voice.male_3", russian ? "Мужской III" : "Male III");
        add("appearance.halgventmod.voice.female_1", russian ? "Женский I" : "Female I");
        add("appearance.halgventmod.voice.female_2", russian ? "Женский II" : "Female II");
        add("backstory.halgventmod.mercenary", russian ? "Наёмник" : "Mercenary");
        add("backstory.halgventmod.mercenary.desc", russian ? "Вольный солдат, идущий к своей мечте с мечом в руке." : "A free soldier walking toward his dream, sword in hand.");
        add("backstory.halgventmod.peasant", russian ? "Крестьянин" : "Peasant");
        add("backstory.halgventmod.peasant.desc", russian ? "Раб полей и мельниц, мужественно выдерживающий удары судьбы." : "A slave of fields and mills who bravely weathers the blows of fate.");
        add("backstory.halgventmod.craftsman", russian ? "Ремесленник" : "Craftsman");
        add("backstory.halgventmod.craftsman.desc", russian ? "На все руки мастер, гниющий за стенами города." : "A jack of all trades, rotting behind the city walls.");
        add("backstory.halgventmod.pilgrim", russian ? "Пилигрим" : "Pilgrim");
        add("backstory.halgventmod.pilgrim.desc", russian ? "Благоверующий странник, который не властен даже над своей судьбой." : "A devout wanderer with no power even over his own fate.");
        add("backstory.halgventmod.minstrel", russian ? "Менестрель" : "Minstrel");
        add("backstory.halgventmod.minstrel.desc", russian ? "Весёлый актёр и поэт, ублажающий богатых дам." : "A merry actor and poet who pleases wealthy ladies.");
        add("backstory.halgventmod.jester", russian ? "Шут" : "Jester");
        add("backstory.halgventmod.jester.desc", russian ? "Весельчак и добряк, ввергающий людей в трагедию одним словом." : "A kind-hearted joker who plunges people into tragedy with a single word.");
        add("backstory.halgventmod.deserter", russian ? "Дезертир" : "Deserter");
        add("backstory.halgventmod.deserter.desc", russian ? "Трус, бежавший с поля боя, спасая свою никчёмную жизнь." : "A coward who fled the battlefield to save his worthless life.");
        add("backstory.halgventmod.flagellant", russian ? "Флагеллянт" : "Flagellant");
        add("backstory.halgventmod.flagellant.desc", russian ? "Бесстрашный верун." : "A fearless zealot.");
        add("backstory.halgventmod.noble", russian ? "Дворянин" : "Noble");
        add("backstory.halgventmod.noble.desc", russian ? "Человек чести и славы посреди многолюдья смуты." : "A man of honour and glory amid the crowds of turmoil.");
        add("backstory.halgventmod.hunter", russian ? "Охотник" : "Hunter");
        add("backstory.halgventmod.hunter.desc", russian ? "Терпеливый и упёртый егерь, готовый сразить любого зверя." : "A patient, stubborn huntsman ready to bring down any beast.");
        add("backstory.halgventmod.fisher", russian ? "Рыбак" : "Fisherman");
        add("backstory.halgventmod.fisher.desc", russian ? "Простой рыбак, прожигающий свою жизнь перед неодолимой стихией." : "A simple fisherman burning his life away before the unconquerable elements.");
        add("backstory.halgventmod.savage", russian ? "Дикарь" : "Savage");
        add("backstory.halgventmod.savage.desc", russian ? "Господин своей вольной земли, древний нравами и обычаями." : "Lord of his own free land, ancient in manners and customs.");
        add("command.halgventmod.character.reset", russian ? "%s создаёт персонажа заново" : "%s is creating their character anew");
        add("command.halgventmod.character.edit", russian ? "%s выбирает облик и имя заново" : "%s is choosing their look and name anew");
    }

    private void gui(String key, String ru, String en) {
        add("gui.halgventmod." + key, russian ? ru : en);
    }

    private void recipe(String id, String nameRu, String howRu, String serveRu, String nameEn, String howEn, String serveEn) {
        gui("recipes." + id, nameRu, nameEn);
        gui("recipes." + id + ".how", howRu, howEn);
        gui("recipes." + id + ".serve", serveRu, serveEn);
    }

    private void brewingMessage(String key, String ru, String en) {
        add("message.halgventmod.brewing." + key, russian ? ru : en);
    }

    private void pottery(String key, String ru, String en) {
        add("message.halgventmod.pottery." + key, russian ? ru : en);
    }

    private void potteryTip(String key, String ru, String en) {
        add("tooltip.halgventmod.pottery." + key, russian ? ru : en);
    }

    private void wheel(String key, String ru, String en) {
        add("gui.halgventmod.wheel." + key, russian ? ru : en);
    }

    private void brewing(String key, String ru, String en) {
        add("message.halgventmod.brewing." + key, russian ? ru : en);
    }

    private void drunk(String key, String ru, String en) {
        add("message.halgventmod.drunk." + key, russian ? ru : en);
    }

    private void drink(String key, String ru, String en) {
        add("drink.halgventmod." + key, russian ? ru : en);
    }

    /** Качество снаряжения и типы урона (quality/*). Сами признаки качества — из ModTraits. */
    private void qualityTranslations() {
        add("tooltip.halgventmod.quality.weapon", russian ? "Урон ×%s, пробитие %s%% (%s)" : "Damage ×%s, penetration %s%% (%s)");
        add("tooltip.halgventmod.quality.food", russian ? "Сытость и насыщение ×%s" : "Nutrition and saturation ×%s");
        add("tooltip.halgventmod.quality.drink", russian ? "Хмель ×%s" : "Intoxication ×%s");
        add("tooltip.halgventmod.quality.rotgut", russian ? "Сивуха: мутит в %s%% случаев" : "Rotgut: makes you sick %s%% of the time");
        add("tooltip.halgventmod.quality.hangover_skip", russian ? "Похмелье: шанс обойтись без него %s%%" : "Hangover: %s%% chance to escape it");
        add("tooltip.halgventmod.quality.hangover_longer", russian ? "Похмелье дольше на %s%%" : "Hangover lasts %s%% longer");
        add("tooltip.halgventmod.quality.armor", russian ? "Защита ×%s, стойкость ×%s" : "Protection ×%s, resistance ×%s");
        add("damage_kind.halgventmod.chopping", russian ? "рубящий" : "chopping");
        add("damage_kind.halgventmod.piercing", russian ? "колющий" : "piercing");
        add("damage_kind.halgventmod.blunt", russian ? "дробящий" : "blunt");
        add("damage_kind.halgventmod.penetrating", russian ? "проникающий" : "penetrating");
        add("command.halgventmod.quality.set", russian ? "Качество задано: %s" : "Quality set: %s");
        add("command.halgventmod.quality.not_gear",
                russian ? "Возьмите в руку оружие или броню и укажите качество" : "Hold a weapon or armor and name a quality");
    }

    private void squad(String key, String ru, String en) {
        add("squad.halgventmod." + key, russian ? ru : en);
    }

    private void squadGui(String key, String ru, String en) {
        add("gui.halgventmod.squad." + key, russian ? ru : en);
    }
}