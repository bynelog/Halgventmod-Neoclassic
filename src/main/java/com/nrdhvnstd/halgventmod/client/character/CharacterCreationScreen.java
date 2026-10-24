package com.nrdhvnstd.halgventmod.client.character;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.Window;
import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.armor.ArmorInventory;
import com.nrdhvnstd.halgventmod.armor.ArmorSlot;
import com.nrdhvnstd.halgventmod.armor.ModArmor;
import com.nrdhvnstd.halgventmod.character.Appearance;
import com.nrdhvnstd.halgventmod.character.Backstory;
import com.nrdhvnstd.halgventmod.name.RpNames;
import com.nrdhvnstd.halgventmod.network.CharacterCreatePayload;
import com.nrdhvnstd.halgventmod.network.CharacterOpenPayload;
import com.nrdhvnstd.halgventmod.registry.ModAttachments;
import com.nrdhvnstd.halgventmod.sound.Voices;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Окно создания персонажа (геймдок 3.1), шаг за шагом: Облик → Предыстория → Точка появления → Итог → Имя.
 * Кто уже играл до создания персонажа (или кому администратор открыл правку) — только Облик и Имя.
 * Слева — панель шага, справа — фигурка в выбранном облике (и в комплекте предыстории после её выбора).
 * Закрыть окно нельзя: оно уходит, когда сервер принял персонажа (CharacterClient).
 * Оформление — как у окна «Персонаж»: тёмные панели с каймой, названия на свитках.
 * Не влезает в экран — масштаб интерфейса понижается на время окна, как у правки точки карты.
 */
public class CharacterCreationScreen extends Screen {
    private enum Step {
        APPEARANCE, BACKSTORY, SPAWN, SUMMARY, NAME;

        String key() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    // ===== Спрайты окна «Персонаж»: textures/gui/sprites/character/ =====
    private static final ResourceLocation PANEL = sprite("panel");
    private static final ResourceLocation TITLE = sprite("title");
    private static final ResourceLocation FIGURE_FRAME = sprite("figure_frame");
    private static final ResourceLocation CELL_SPRITE = sprite("cell");

    // ===== Разметка =====
    private static final int PANEL_MIN_WIDTH = 230;
    private static final int PANEL_MAX_WIDTH = 280;
    private static final int PANEL_MAX_HEIGHT = 240;
    private static final int MIN_PANEL_HEIGHT = 170;
    private static final int FIGURE_WIDTH = 110;
    private static final int GAP = 6;
    private static final int TOP = 30;          // верх панелей: над ними строка шагов и свиток
    private static final int BOTTOM = 30;       // под панелями — кнопки
    private static final int MARGIN = 8;        // от окна до края экрана
    private static final int MIN_WIDTH = 2 * MARGIN + PANEL_MIN_WIDTH + GAP + FIGURE_WIDTH;
    private static final int MIN_HEIGHT = TOP + MIN_PANEL_HEIGHT + BOTTOM;
    private static final int INNER = 8;         // внутри панели, за узорной каймой
    private static final int TITLE_H = 14;      // от верха панели до первой строки
    private static final int TITLE_RISE = 4;
    private static final int TITLE_HEIGHT = 11;
    private static final int TITLE_PADDING = 12;
    private static final int LINE = 10;
    private static final int ARROW = 14;        // кнопки «<» и «>» у части облика
    private static final int PART_ROW = 24;     // подпись части + переключатель
    private static final int LIST_ROW = 15;     // кнопка списка 14 + просвет
    private static final int COLUMN_GAP = 8;
    private static final int CELL = 18;
    private static final int KIT_GAP = 6;       // между одеждой и вещами в строке комплекта
    private static final int BUTTON_WIDTH = 90;
    private static final float FIGURE_SCALE = 0.3F; // от высоты рамки; в окне «Персонаж» 60 при ~200
    private static final float FIGURE_Y_OFFSET = 0.0625F;

    // ===== Цвета текста (ARGB), как в окне «Персонаж» =====
    private static final int COLOR_TITLE = 0xFF3B2A1A;
    private static final int COLOR_TEXT = 0xFFE8DCC0;
    private static final int COLOR_DIM = 0xFFB8A888;
    private static final int COLOR_SELECTED = 0xFFFFE8A0;
    private static final int COLOR_ERROR = 0xFFE07060;

    private final boolean full;
    private final List<Step> steps;
    private final List<Backstory> backstories;
    private final List<CharacterOpenPayload.Spawn> spawns;
    private final RandomSource random = RandomSource.create();
    private final Map<ResourceLocation, ArmorInventory> kitArmor = new HashMap<>(); // одежда предыстории на фигурке
    private final ArmorInventory noArmor = new ArmorInventory();                     // облик — без одежды поверх

    private Map<String, String> appearance;
    private int step;
    @Nullable
    private Backstory backstory;
    @Nullable
    private CharacterOpenPayload.Spawn spawn;
    private String name;
    @Nullable
    private Component error;
    private boolean waiting;             // отправили — ждём ответа сервера
    private boolean scaled;              // масштаб интерфейса понижен этим окном — при закрытии вернуть

    // Посчитано в init()
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private int figureX;
    private final Map<Button, Backstory> backstoryButtons = new HashMap<>();
    private final Map<Button, CharacterOpenPayload.Spawn> spawnButtons = new HashMap<>();
    @Nullable
    private Button nextButton;

    public CharacterCreationScreen(CharacterOpenPayload payload) {
        super(Component.translatable("gui.halgventmod.creation.title"));
        this.full = payload.full();
        this.steps = full ? List.of(Step.values()) : List.of(Step.APPEARANCE, Step.NAME);
        this.backstories = payload.backstories();
        this.spawns = payload.spawns();
        Map<String, String> given = Appearance.validated(payload.appearance());
        this.appearance = new LinkedHashMap<>(given != null ? given : Appearance.random(random));
        this.name = payload.name();
    }

    private Step current() {
        return steps.get(step);
    }

    private boolean last() {
        return step == steps.size() - 1;
    }

    // ===== Виджеты =====

    @Override
    protected void init() {
        fitScale();
        panelWidth = Mth.clamp(width - 2 * MARGIN - GAP - FIGURE_WIDTH, PANEL_MIN_WIDTH, PANEL_MAX_WIDTH);
        panelHeight = Mth.clamp(height - TOP - BOTTOM, MIN_PANEL_HEIGHT, PANEL_MAX_HEIGHT);
        int total = panelWidth + GAP + FIGURE_WIDTH;
        panelX = (width - total) / 2;
        panelY = TOP;
        figureX = panelX + panelWidth + GAP;

        backstoryButtons.clear();
        spawnButtons.clear();
        int x = contentX();
        int y = contentY();
        switch (current()) {
            case APPEARANCE -> initAppearance(x, y);
            case BACKSTORY -> initBackstories(x, y);
            case SPAWN -> initSpawns(x, y);
            case NAME -> initName(x, y);
            default -> {
            }
        }

        // Внизу: «Назад», посередине «Случайный облик» (на шаге облика), «Далее» или «Войти в мир»
        int buttonsY = panelY + panelHeight + 6;
        int center = panelX + total / 2;
        Button back = addRenderableWidget(Button.builder(Component.translatable("gui.halgventmod.creation.back"),
                        button -> go(-1))
                .bounds(center - BUTTON_WIDTH / 2 - 4 - BUTTON_WIDTH, buttonsY, BUTTON_WIDTH, 20).build());
        back.active = step > 0;
        if (current() == Step.APPEARANCE) {
            addRenderableWidget(Button.builder(Component.translatable("gui.halgventmod.creation.random"),
                            button -> appearance = new LinkedHashMap<>(Appearance.random(random)))
                    .bounds(center - BUTTON_WIDTH / 2, buttonsY, BUTTON_WIDTH, 20).build());
        }
        nextButton = addRenderableWidget(Button.builder(Component.translatable(last()
                                ? "gui.halgventmod.creation.enter" : "gui.halgventmod.creation.next"),
                        button -> go(1))
                .bounds(center + BUTTON_WIDTH / 2 + 4, buttonsY, BUTTON_WIDTH, 20).build());
        updateNext();
    }

    private int contentX() {
        return panelX + INNER;
    }

    private int contentY() {
        return panelY + TITLE_H;
    }

    private int contentWidth() {
        return panelWidth - 2 * INNER;
    }

    private int columnWidth() {
        return (contentWidth() - COLUMN_GAP) / 2;
    }

    /** Части облика в два столбца: подпись, под ней «<» вариант «>». */
    private void initAppearance(int x, int y) {
        Appearance.Part[] parts = Appearance.Part.values();
        int rows = (parts.length + 1) / 2;
        int columnWidth = columnWidth();
        for (int i = 0; i < parts.length; i++) {
            Appearance.Part part = parts[i];
            int cx = x + (i < rows ? 0 : columnWidth + COLUMN_GAP);
            int cy = y + (i % rows) * PART_ROW + LINE;
            addRenderableWidget(Button.builder(Component.literal("<"), button -> cycle(part, -1))
                    .bounds(cx, cy, ARROW, ARROW).build());
            addRenderableWidget(Button.builder(Component.literal(">"), button -> cycle(part, 1))
                    .bounds(cx + columnWidth - ARROW, cy, ARROW, ARROW).build());
        }
    }

    private void initBackstories(int x, int y) {
        int rows = (backstories.size() + 1) / 2;
        int columnWidth = columnWidth();
        for (int i = 0; i < backstories.size(); i++) {
            Backstory each = backstories.get(i);
            int cx = x + (i < rows ? 0 : columnWidth + COLUMN_GAP);
            int cy = y + (i % rows) * LIST_ROW;
            Button button = addRenderableWidget(Button.builder(each.title(), pressed -> {
                        backstory = each;
                        updateNext();
                    })
                    .bounds(cx, cy, columnWidth, LIST_ROW - 1).build());
            backstoryButtons.put(button, each);
        }
    }

    private void initSpawns(int x, int y) {
        int rows = (spawns.size() + 1) / 2;
        int columnWidth = columnWidth();
        for (int i = 0; i < spawns.size(); i++) {
            CharacterOpenPayload.Spawn each = spawns.get(i);
            int cx = x + (i < rows ? 0 : columnWidth + COLUMN_GAP);
            int cy = y + (i % rows) * LIST_ROW;
            Button button = addRenderableWidget(Button.builder(Component.literal(each.name()), pressed -> {
                        spawn = each;
                        updateNext();
                    })
                    .bounds(cx, cy, columnWidth, LIST_ROW - 1).build());
            spawnButtons.put(button, each);
        }
    }

    private void initName(int x, int y) {
        EditBox box = new EditBox(font, x, y + LINE + 2, contentWidth(), 18,
                Component.translatable("gui.halgventmod.creation.name.label"));
        box.setMaxLength(RpNames.MAX_LENGTH);
        box.setValue(name);
        box.setResponder(value -> {
            name = value;
            error = null;
            updateNext();
        });
        addRenderableWidget(box);
        setInitialFocus(box);
    }

    private void updateNext() {
        if (nextButton == null) {
            return;
        }
        nextButton.active = switch (current()) {
            case BACKSTORY -> backstory != null || backstories.isEmpty();
            case SPAWN -> spawn != null || spawns.isEmpty();
            case NAME -> !waiting && RpNames.characterNameError(RpNames.normalize(name)) == null;
            default -> true;
        };
    }

    // ===== Действия =====

    private void go(int direction) {
        if (direction > 0 && last()) {
            confirm();
            return;
        }
        step = Mth.clamp(step + direction, 0, steps.size() - 1);
        error = null;
        rebuildWidgets();
    }

    private void cycle(Appearance.Part part, int direction) {
        List<String> options = part.options();
        int index = options.indexOf(Appearance.get(appearance, part));
        appearance.put(part.id(), options.get(Math.floorMod(index + direction, options.size())));
        if (part == Appearance.Part.SEX) {
            appearance = new LinkedHashMap<>(Appearance.matchSex(appearance));
        }
        if (part == Appearance.Part.SEX || part == Appearance.Part.VOICE) {
            playVoice();
        }
    }

    /** Послушать голос: первый крик, что есть в наборе. */
    private void playVoice() {
        String set = Appearance.get(appearance, Appearance.Part.VOICE);
        for (Voices.Cry cry : Voices.Cry.values()) {
            Holder<SoundEvent> sound = Voices.sound(set, cry);
            if (sound != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound.value(), 1.0F));
                return;
            }
        }
    }

    private void confirm() {
        waiting = true;
        error = null;
        updateNext();
        PacketDistributor.sendToServer(new CharacterCreatePayload(Map.copyOf(appearance),
                backstory == null ? "" : backstory.id().toString(),
                spawn == null ? CharacterCreatePayload.NO_SPAWN : spawn.id(), name));
    }

    /** Сервер не принял: показать причину на нужном шаге. */
    void showError(String key) {
        waiting = false;
        Step target = switch (key) {
            case "appearance" -> Step.APPEARANCE;
            case "backstory" -> Step.BACKSTORY;
            case "spawn" -> Step.SPAWN;
            default -> Step.NAME;
        };
        if (steps.contains(target)) {
            step = steps.indexOf(target);
        }
        if (minecraft != null) {
            rebuildWidgets();
        }
        error = Component.translatable("gui.halgventmod.creation.error." + key);
    }

    // ===== Масштаб =====

    /** Наибольший масштаб интерфейса не крупнее выбранного, при котором окно целиком на экране (как PointEditScreen). */
    private void fitScale() {
        Window window = minecraft.getWindow();
        double scale = window.getGuiScale();
        double fit = Math.min(scale, Math.min(window.getWidth() / (double) MIN_WIDTH,
                window.getHeight() / (double) MIN_HEIGHT));
        fit = Math.max(1, Math.floor(fit * 100) / 100);
        if (fit < scale) {
            window.setGuiScale(fit);
            width = window.getGuiScaledWidth();
            height = window.getGuiScaledHeight();
            scaled = true;
        }
    }

    @Override
    public void removed() {
        if (scaled) {
            Window window = minecraft.getWindow();
            window.setGuiScale(window.calculateScale(minecraft.options.guiScale().get(), minecraft.isEnforceUnicode()));
            scaled = false;
        }
        super.removed();
    }

    // ===== Рисование =====

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        panel(graphics, panelX, panelY, panelWidth, panelHeight,
                Component.translatable("gui.halgventmod.creation.step." + current().key()));
        graphics.blitSprite(FIGURE_FRAME, figureX, panelY, FIGURE_WIDTH, panelHeight);
        LocalPlayer player = minecraft.player;
        if (player != null) {
            renderFigure(graphics, player, mouseX, mouseY);
        }
    }

    /** Фигурка игрока в выбираемом облике и одежде: на время рисования подменяем облик и броню. */
    private void renderFigure(GuiGraphics graphics, LocalPlayer player, int mouseX, int mouseY) {
        ArmorInventory own = player.getExistingDataOrNull(ModAttachments.ARMOR);
        AppearanceSkins.setPreview(appearance);
        player.setData(ModAttachments.ARMOR, previewArmor());
        try {
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, figureX + 1, panelY + 1,
                    figureX + FIGURE_WIDTH - 1, panelY + panelHeight - 1, Math.round(panelHeight * FIGURE_SCALE),
                    FIGURE_Y_OFFSET, mouseX, mouseY, player);
        } finally {
            if (own != null) {
                player.setData(ModAttachments.ARMOR, own);
            } else {
                player.removeData(ModAttachments.ARMOR.get());
            }
            AppearanceSkins.setPreview(null);
        }
    }

    /** На шаге облика — без одежды поверх исподнего; дальше — одежда выбранной предыстории. */
    private ArmorInventory previewArmor() {
        Backstory chosen = backstory;
        if (!full || current() == Step.APPEARANCE || chosen == null) {
            return noArmor;
        }
        return kitArmor.computeIfAbsent(chosen.id(), id -> {
            ArmorInventory armor = new ArmorInventory();
            for (ItemStack stack : chosen.armor()) {
                ArmorSlot slot = ModArmor.slotOf(stack);
                if (slot != null) {
                    armor.set(slot, stack.copy());
                }
            }
            return armor;
        });
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderSteps(graphics);
        int x = contentX();
        int y = contentY();
        ItemStack hovered = switch (current()) {
            case APPEARANCE -> {
                renderAppearance(graphics, x, y);
                yield ItemStack.EMPTY;
            }
            case BACKSTORY -> renderBackstory(graphics, x, y, mouseX, mouseY);
            case SPAWN -> {
                renderSpawn(graphics, x, y);
                yield ItemStack.EMPTY;
            }
            case SUMMARY -> renderSummary(graphics, x, y, mouseX, mouseY);
            case NAME -> {
                renderName(graphics, x, y);
                yield ItemStack.EMPTY;
            }
        };
        if (error != null) {
            List<FormattedCharSequence> lines = font.split(error, contentWidth());
            int errorY = panelY + panelHeight - INNER - lines.size() * LINE;
            for (FormattedCharSequence line : lines) {
                graphics.drawString(font, line, x, errorY, COLOR_ERROR, false);
                errorY += LINE;
            }
        }
        if (!hovered.isEmpty()) {
            graphics.renderTooltip(font, hovered, mouseX, mouseY);
        }
    }

    /** Над панелями: «1. Облик   2. Предыстория …» — пройденные светлые, текущий выделен, будущие тусклые. */
    private void renderSteps(GuiGraphics graphics) {
        Component[] labels = new Component[steps.size()];
        int total = 0;
        int spacing = font.width("   ");
        for (int i = 0; i < steps.size(); i++) {
            labels[i] = Component.literal((i + 1) + ". ")
                    .append(Component.translatable("gui.halgventmod.creation.step." + steps.get(i).key()));
            total += font.width(labels[i]) + (i > 0 ? spacing : 0);
        }
        int x = (width - total) / 2;
        for (int i = 0; i < labels.length; i++) {
            int color = i == step ? COLOR_SELECTED : i < step ? COLOR_TEXT : COLOR_DIM;
            graphics.drawString(font, labels[i], x, 10, color, true);
            x += font.width(labels[i]) + spacing;
        }
    }

    private void renderAppearance(GuiGraphics graphics, int x, int y) {
        Appearance.Part[] parts = Appearance.Part.values();
        int rows = (parts.length + 1) / 2;
        int columnWidth = columnWidth();
        for (int i = 0; i < parts.length; i++) {
            Appearance.Part part = parts[i];
            int cx = x + (i < rows ? 0 : columnWidth + COLUMN_GAP);
            int cy = y + (i % rows) * PART_ROW;
            graphics.drawString(font, Component.translatable(part.langKey()), cx, cy, COLOR_DIM, false);
            String value = fit(Component.translatable(part.optionKey(Appearance.get(appearance, part))).getString(),
                    columnWidth - 2 * ARROW - 4);
            graphics.drawString(font, value, cx + (columnWidth - font.width(value)) / 2, cy + LINE + 3, COLOR_TEXT, false);
        }
    }

    private ItemStack renderBackstory(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        outlineSelected(graphics, backstoryButtons, backstory);
        if (backstories.isEmpty()) {
            text(graphics, Component.translatable("gui.halgventmod.creation.backstory.none"), x, y, COLOR_DIM);
            return ItemStack.EMPTY;
        }
        int below = y + (backstories.size() + 1) / 2 * LIST_ROW + 4;
        if (backstory == null) {
            text(graphics, Component.translatable("gui.halgventmod.creation.backstory.hint"), x, below, COLOR_DIM);
            return ItemStack.EMPTY;
        }
        below = text(graphics, backstory.description(), x, below, COLOR_TEXT) + 2;
        return kit(graphics, backstory, x, below, mouseX, mouseY);
    }

    private void renderSpawn(GuiGraphics graphics, int x, int y) {
        outlineSelected(graphics, spawnButtons, spawn);
        if (spawns.isEmpty()) {
            text(graphics, Component.translatable("gui.halgventmod.creation.spawn.none"), x, y, COLOR_DIM);
            return;
        }
        int below = y + (spawns.size() + 1) / 2 * LIST_ROW + 4;
        if (spawn == null) {
            text(graphics, Component.translatable("gui.halgventmod.creation.spawn.hint"), x, below, COLOR_DIM);
        } else if (!spawn.description().isEmpty()) {
            text(graphics, Component.literal(spawn.description()), x, below, COLOR_TEXT);
        }
    }

    private ItemStack renderSummary(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        Component backstoryName = backstory != null ? backstory.title()
                : Component.translatable("gui.halgventmod.creation.summary.no_backstory");
        y = text(graphics, Component.translatable("gui.halgventmod.creation.summary.backstory", backstoryName),
                x, y, COLOR_TEXT);
        if (backstory != null) {
            y = text(graphics, backstory.description(), x, y, COLOR_DIM);
        }
        Component spawnName = spawn != null ? Component.literal(spawn.name())
                : Component.translatable("gui.halgventmod.creation.summary.spawn_here");
        y = text(graphics, Component.translatable("gui.halgventmod.creation.summary.spawn", spawnName),
                x, y + 4, COLOR_TEXT) + 4;
        return backstory != null ? kit(graphics, backstory, x, y, mouseX, mouseY) : ItemStack.EMPTY;
    }

    private void renderName(GuiGraphics graphics, int x, int y) {
        graphics.drawString(font, Component.translatable("gui.halgventmod.creation.name.label"), x, y, COLOR_DIM, false);
        int below = y + LINE + 2 + 18 + 4;
        below = text(graphics, Component.translatable("gui.halgventmod.creation.name.hint",
                RpNames.MIN_LENGTH, RpNames.MAX_LENGTH), x, below, COLOR_DIM);
        if (!full) {
            below = text(graphics, Component.translatable("gui.halgventmod.creation.name.edit"), x, below + 4, COLOR_DIM);
        }
        String normalized = RpNames.normalize(name);
        String problem = normalized.isEmpty() ? null : RpNames.characterNameError(normalized);
        if (problem != null && error == null) {
            text(graphics, Component.translatable("gui.halgventmod.creation.error." + problem), x, below + 4, COLOR_ERROR);
        }
    }

    /** «Комплект:» и в строку ячейки — сначала одежда, через просвет вещи. Возвращает вещь под курсором. */
    private ItemStack kit(GuiGraphics graphics, Backstory chosen, int x, int y, int mouseX, int mouseY) {
        graphics.drawString(font, Component.translatable("gui.halgventmod.creation.kit"), x, y, COLOR_DIM, false);
        int cellX = x;
        int cellY = y + LINE;
        int right = x + contentWidth();
        ItemStack hovered = ItemStack.EMPTY;
        for (List<ItemStack> group : List.of(chosen.armor(), chosen.items())) {
            for (ItemStack stack : group) {
                if (cellX + CELL > right) {
                    cellX = x;
                    cellY += CELL;
                }
                graphics.blitSprite(CELL_SPRITE, cellX, cellY, CELL, CELL);
                graphics.renderItem(stack, cellX + 1, cellY + 1);
                graphics.renderItemDecorations(font, stack, cellX + 1, cellY + 1);
                if (mouseX >= cellX && mouseX < cellX + CELL && mouseY >= cellY && mouseY < cellY + CELL) {
                    hovered = stack;
                }
                cellX += CELL;
            }
            cellX += KIT_GAP;
        }
        return hovered;
    }

    /** Золотая рамка вокруг выбранной кнопки списка. */
    private static <T> void outlineSelected(GuiGraphics graphics, Map<Button, T> buttons, @Nullable T selected) {
        buttons.forEach((button, value) -> {
            if (value == selected) {
                graphics.renderOutline(button.getX() - 1, button.getY() - 1, button.getWidth() + 2,
                        button.getHeight() + 2, COLOR_SELECTED);
            }
        });
    }

    /** Текст с переносом по ширине панели. Возвращает y под последней строкой. */
    private int text(GuiGraphics graphics, Component text, int x, int y, int color) {
        for (FormattedCharSequence line : font.split(text, contentWidth())) {
            graphics.drawString(font, line, x, y, color, false);
            y += LINE;
        }
        return y;
    }

    /** Тёмная панель с каймой и свиток с названием посередине её верхнего края. */
    private void panel(GuiGraphics graphics, int x, int y, int width, int height, Component title) {
        graphics.blitSprite(PANEL, x, y, width, height);
        int titleWidth = font.width(title) + 2 * TITLE_PADDING;
        int titleX = x + (width - titleWidth) / 2;
        int titleY = y - TITLE_RISE;
        graphics.blitSprite(TITLE, titleX, titleY, titleWidth, TITLE_HEIGHT);
        graphics.drawString(font, title, titleX + TITLE_PADDING, titleY + 2, COLOR_TITLE, false);
    }

    // Длинное название обрезается с многоточием
    private String fit(String text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return text;
        }
        return font.plainSubstrByWidth(text, maxWidth - font.width("…")) + "…";
    }

    private static ResourceLocation sprite(String name) {
        return ResourceLocation.fromNamespaceAndPath(HalgventMod.MODID, "character/" + name);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
