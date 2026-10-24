package com.nrdhvnstd.halgventmod.client.map;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.Window;
import com.nrdhvnstd.halgventmod.banner.BannerEntry;
import com.nrdhvnstd.halgventmod.banner.BannerRegistry;
import com.nrdhvnstd.halgventmod.client.banner.BannerDraw;
import com.nrdhvnstd.halgventmod.client.banner.BannerRegistryScreen;
import com.nrdhvnstd.halgventmod.network.PointEditPayload;
import com.nrdhvnstd.halgventmod.point.MapPoint;
import com.nrdhvnstd.halgventmod.point.MapPoints;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Окно точки карты у администрации (после правки границы на карте). Две колонки, помещается в 640 × 360:
 * слева — название и координаты, значок до открытия («?» или любой) и после, цвет флага, текст при наведении;
 * справа — галочка «С описанием», знамя из реестра (ряд последних внесённых, «—», «Весь реестр»), описание, сведения.
 * Галочка выключена — правая колонка неактивна, но её значения не стираются. Сохраняет и удаляет сервер (PointEditPayload).
 * Не влезает при крупном масштабе интерфейса — на время окна масштаб понижается до влезающего (fitScale).
 */
public class PointEditScreen extends Screen {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 340;
    private static final int PAD = 10;
    private static final int MARGIN = 4; // от окна до края экрана
    private static final int COLUMN = 286;
    private static final int COLUMN_GAP = WIDTH - 2 * PAD - 2 * COLUMN;

    // Левая колонка (y — от верха окна)
    private static final int NAME_WIDTH = 136;
    private static final int COORD_WIDTH = 46;
    private static final int FIELD_GAP = 4;
    private static final int ICON_CELL = 18;
    private static final int ICON_SIZE = 16;
    private static final int ICONS_PER_ROW = 13;
    private static final int HIDDEN_ICONS_TOP = 68;
    private static final int OPEN_ICONS_TOP = 118;
    private static final int DYE_TOP = 170;
    private static final int DYE_SWATCH = 11;
    private static final int DYE_STEP = 12;
    private static final int HOVER_TOP = 198;
    private static final int TEXT_HEIGHT_LEFT = 100;

    // Правая колонка
    private static final int BANNER_ROW_TOP = 58;
    private static final int BANNER_WIDTH = 20;    // 20 × 40: тексель в пиксель
    private static final int BANNER_CELL = 24;
    private static final int RECENT_BANNERS = 7;
    private static final int REGISTRY_BUTTON_WIDTH = 90;
    private static final int DESCRIPTION_TOP = 116;
    private static final int DESCRIPTION_HEIGHT = 80;
    private static final int INFO_TOP = 212;
    private static final int INFO_HEIGHT = 86;

    private static final int BUTTON_WIDTH = 100;

    private static final int COLOR_PANEL = 0xF0241C14;
    private static final int COLOR_FRAME = 0xFF6E6250;
    private static final int COLOR_TEXT = 0xFFE8DCC0;
    private static final int COLOR_DIM = 0xFFB8A888;
    private static final int COLOR_SELECTED = 0xFFFFE8A0;
    private static final int COLOR_HOVER = 0xFF6E6250;
    private static final int COLOR_CELL = 0x60000000;
    private static final int COLOR_DISABLED = 0xA0241C14; // поверх неактивной правой колонки

    // Значок и цвет последней сохранённой точки — для следующей новой
    private static MapIcon lastIcon = MapIcon.FLAG;
    private static DyeColor lastDye = DyeColor.RED;

    private final Screen parent;
    private final MapPoint point;        // черновик: id 0 — новая
    private final Runnable onDone;       // точку сохранили или удалили — карта закрывает правку границы
    private MapIcon icon;
    @Nullable
    private MapIcon hiddenIcon;          // null — «?»
    private DyeColor dye;
    private String banner;

    private int left;
    private int top;
    private EditBox nameBox;
    private EditBox xBox;
    private EditBox yBox;
    private EditBox zBox;
    private MultiLineEditBox hoverBox;
    private Checkbox describedBox;
    private Checkbox spawnBox;
    private MultiLineEditBox descriptionBox;
    private MultiLineEditBox infoBox;
    private Button registryButton;
    private boolean scaled;              // масштаб интерфейса понижен этим окном — при закрытии вернуть

    /**
     * draft — точка с уже заданной границей (id 0 — новая, иначе копия существующей).
     * onDone — после «Сохранить» или «Удалить»; «Отмена» просто возвращает на карту.
     */
    public PointEditScreen(Screen parent, MapPoint draft, Runnable onDone) {
        super(Component.translatable(draft.id == 0 ? "gui.halgventmod.point.new" : "gui.halgventmod.point.edit"));
        this.parent = parent;
        this.point = draft;
        this.onDone = onDone;
        this.icon = draft.icon.isEmpty() ? lastIcon : PointShapes.icon(draft);
        this.hiddenIcon = MapIcon.find(draft.hiddenIcon);
        this.dye = draft.dye.isEmpty() ? lastDye : PointShapes.dye(draft);
        this.banner = draft.banner;
    }

    @Override
    protected void init() {
        fitScale();
        left = (width - WIDTH) / 2;
        top = (height - HEIGHT) / 2;
        int x = left + PAD;
        int right = rightColumn();

        // Слева: название и координаты в одну строку
        nameBox = edit(nameBox, x, top + 34, NAME_WIDTH, MapPoint.NAME_MAX, point.name);
        setInitialFocus(nameBox);
        int coordX = x + NAME_WIDTH + 2 * FIELD_GAP;
        xBox = coordinate(xBox, coordX, point.x);
        yBox = coordinate(yBox, coordX + COORD_WIDTH + FIELD_GAP, point.y);
        zBox = coordinate(zBox, coordX + 2 * (COORD_WIDTH + FIELD_GAP), point.z);
        hoverBox = multiline(hoverBox, x, top + HOVER_TOP, TEXT_HEIGHT_LEFT, MapPoint.HOVER_TEXT_MAX, point.hoverText,
                "gui.halgventmod.point.hover_text");

        // Справа: галочка, знамя, описание, сведения
        boolean described = describedBox != null ? describedBox.selected() : point.described;
        describedBox = addRenderableWidget(Checkbox.builder(Component.translatable("gui.halgventmod.point.described"), font)
                .pos(right, top + 24)
                .selected(described)
                .onValueChange((box, value) -> updateDescribed())
                .build());
        boolean spawn = spawnBox != null ? spawnBox.selected() : point.spawn;
        spawnBox = addRenderableWidget(Checkbox.builder(Component.translatable("gui.halgventmod.point.spawn"), font)
                .pos(right + COLUMN / 2, top + 24)
                .selected(spawn)
                .build());
        spawnBox.setTooltip(Tooltip.create(Component.translatable("gui.halgventmod.point.spawn.tooltip")));
        registryButton = addRenderableWidget(Button.builder(Component.translatable("gui.halgventmod.point.all_banners"),
                        button -> minecraft.setScreen(BannerRegistryScreen.pick(this, id -> banner = id)))
                .bounds(right + COLUMN - REGISTRY_BUTTON_WIDTH, top + BANNER_ROW_TOP + 10, REGISTRY_BUTTON_WIDTH, 20).build());
        descriptionBox = multiline(descriptionBox, right, top + DESCRIPTION_TOP, DESCRIPTION_HEIGHT, MapPoint.DESCRIPTION_MAX,
                point.description, "gui.halgventmod.point.description");
        infoBox = multiline(infoBox, right, top + INFO_TOP, INFO_HEIGHT, MapPoint.INFO_MAX, point.info,
                "gui.halgventmod.point.info");

        int buttonsY = top + HEIGHT - 30;
        int buttonsX = left + (WIDTH - 3 * BUTTON_WIDTH - 2 * FIELD_GAP) / 2;
        addRenderableWidget(Button.builder(Component.translatable("gui.halgventmod.waypoint.save"), button -> save())
                .bounds(buttonsX, buttonsY, BUTTON_WIDTH, 20).build());
        Button delete = addRenderableWidget(Button.builder(Component.translatable("gui.halgventmod.waypoint.delete"),
                        button -> delete())
                .bounds(buttonsX + BUTTON_WIDTH + FIELD_GAP, buttonsY, BUTTON_WIDTH, 20).build());
        delete.active = point.id != 0;
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .bounds(buttonsX + 2 * (BUTTON_WIDTH + FIELD_GAP), buttonsY, BUTTON_WIDTH, 20).build());
        updateDescribed();
    }

    /**
     * Наибольший масштаб интерфейса не крупнее выбранного, при котором окно целиком на экране. Дробный:
     * целый часто падает вдвое (окно 1920 × 1017 при «4» — до «2»), и окно выходит мелким.
     * pose().scale не годится: в 1.21.1 scissor многострочных полей не учитывает масштаб.
     */
    private void fitScale() {
        Window window = minecraft.getWindow();
        double scale = window.getGuiScale();
        double fit = Math.min(scale, Math.min(window.getWidth() / (double) (WIDTH + 2 * MARGIN),
                window.getHeight() / (double) (HEIGHT + 2 * MARGIN)));
        fit = Math.max(1, Math.floor(fit * 100) / 100); // до сотых — чуть с запасом
        if (fit < scale) {
            window.setGuiScale(fit);
            width = window.getGuiScaledWidth();
            height = window.getGuiScaledHeight();
            scaled = true;
        }
    }

    // Окно закрыли или сменили на реестр знамён — масштаб из настроек, как в Minecraft.resizeDisplay
    @Override
    public void removed() {
        if (scaled) {
            Window window = minecraft.getWindow();
            window.setGuiScale(window.calculateScale(minecraft.options.guiScale().get(), minecraft.isEnforceUnicode()));
            scaled = false;
        }
        super.removed();
    }

    // Пересоздание окна (смена размера, возврат из реестра) — значения берутся из прежних полей
    private EditBox edit(@Nullable EditBox old, int x, int y, int width, int max, String value) {
        EditBox box = new EditBox(font, x, y, width, 18, Component.empty());
        box.setMaxLength(max);
        box.setValue(old != null ? old.getValue() : value);
        return addRenderableWidget(box);
    }

    private EditBox coordinate(@Nullable EditBox old, int x, int value) {
        EditBox box = edit(old, x, top + 34, COORD_WIDTH, 9, Integer.toString(value));
        box.setFilter(text -> text.matches("-?\\d*"));
        return box;
    }

    private MultiLineEditBox multiline(@Nullable MultiLineEditBox old, int x, int y, int height, int max, String value,
                                       String titleKey) {
        MultiLineEditBox box = new MultiLineEditBox(font, x, y, COLUMN, height, Component.empty(),
                Component.translatable(titleKey));
        box.setCharacterLimit(max);
        box.setValue(old != null ? old.getValue() : value);
        return addRenderableWidget(box);
    }

    private int rightColumn() {
        return left + PAD + COLUMN + COLUMN_GAP;
    }

    /** Галочка выключена — правая колонка неактивна (значения остаются). */
    private void updateDescribed() {
        boolean on = describedBox.selected();
        for (AbstractWidget widget : List.of(descriptionBox, infoBox, registryButton)) {
            widget.active = on;
            if (!on && getFocused() == widget) {
                setFocused(null);
            }
        }
    }

    private void save() {
        String name = nameBox.getValue().strip();
        if (name.isEmpty()) {
            name = icon.title().getString();
        }
        lastIcon = icon;
        lastDye = dye;
        point.name = name;
        point.y = parse(yBox, point.y);
        point.moveBy(parse(xBox, point.x) - point.x, parse(zBox, point.z) - point.z); // граница едет вместе с серединой
        point.icon = icon.id();
        point.hiddenIcon = hiddenIcon == null ? "" : hiddenIcon.id();
        point.dye = dye.getSerializedName();
        point.hoverText = hoverBox.getValue().strip();
        point.described = describedBox.selected();
        point.spawn = spawnBox.selected();
        point.banner = banner;
        point.description = descriptionBox.getValue().strip();
        point.info = infoBox.getValue().strip();
        PacketDistributor.sendToServer(new PointEditPayload(point.id == 0 ? MapPoints.Action.CREATE
                : MapPoints.Action.UPDATE, point));
        onDone.run();
        onClose();
    }

    private void delete() {
        if (point.id != 0) {
            PacketDistributor.sendToServer(new PointEditPayload(MapPoints.Action.DELETE, point));
        }
        onDone.run();
        onClose();
    }

    private static int parse(EditBox box, int fallback) {
        try {
            return Integer.parseInt(box.getValue());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    // ===== Сетки значков: до открытия — «?» и все значки, после — все значки =====

    /** Ячейка сетки под курсором: -1 — мимо. */
    private int cellAt(int gridTop, int count, double mouseX, double mouseY) {
        for (int i = 0; i < count; i++) {
            int cx = cellX(i);
            int cy = cellY(gridTop, i);
            if (mouseX >= cx && mouseX < cx + ICON_CELL && mouseY >= cy && mouseY < cy + ICON_CELL) {
                return i;
            }
        }
        return -1;
    }

    private int cellX(int index) {
        return left + PAD + index % ICONS_PER_ROW * ICON_CELL;
    }

    private int cellY(int gridTop, int index) {
        return top + gridTop + index / ICONS_PER_ROW * ICON_CELL;
    }

    private int dyeX(int index) {
        return left + PAD + 2 + index * DYE_STEP;
    }

    @Nullable
    private DyeColor dyeAt(double mouseX, double mouseY) {
        int y = top + DYE_TOP;
        for (DyeColor each : DyeColor.values()) {
            int x = dyeX(each.getId());
            if (mouseX >= x && mouseX < x + DYE_SWATCH && mouseY >= y && mouseY < y + DYE_SWATCH) {
                return each;
            }
        }
        return null;
    }

    // ===== Ряд знамён: «—» и последние внесённые =====

    private List<String> recentBanners() {
        List<String> ids = new ArrayList<>(BannerRegistry.clientEntries.keySet());
        List<String> result = new ArrayList<>();
        for (int i = ids.size() - 1; i >= 0 && result.size() < RECENT_BANNERS; i--) {
            result.add(ids.get(i));
        }
        return result;
    }

    /** Ячейка ряда знамён под курсором: 0 — «—», дальше знамёна; -1 — мимо. */
    private int bannerCellAt(int count, double mouseX, double mouseY) {
        int y = top + BANNER_ROW_TOP;
        for (int i = 0; i <= count; i++) {
            int x = rightColumn() + i * BANNER_CELL;
            if (mouseX >= x && mouseX < x + BANNER_CELL - 2 && mouseY >= y && mouseY < y + 2 * BANNER_WIDTH + 2) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MapIcon[] icons = MapIcon.values();
        int hidden = cellAt(HIDDEN_ICONS_TOP, icons.length + 1, mouseX, mouseY);
        if (hidden >= 0) {
            hiddenIcon = hidden == 0 ? null : icons[hidden - 1];
            return true;
        }
        int open = cellAt(OPEN_ICONS_TOP, icons.length, mouseX, mouseY);
        if (open >= 0) {
            icon = icons[open];
            return true;
        }
        DyeColor clickedDye = dyeAt(mouseX, mouseY);
        if (clickedDye != null) {
            dye = clickedDye;
            return true;
        }
        if (describedBox.selected()) {
            List<String> recent = recentBanners();
            int cell = bannerCellAt(recent.size(), mouseX, mouseY);
            if (cell >= 0) {
                banner = cell == 0 ? "" : recent.get(cell - 1);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // ===== Рисование =====

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.fill(left, top, left + WIDTH, top + HEIGHT, COLOR_PANEL);
        graphics.renderOutline(left, top, WIDTH, HEIGHT, COLOR_FRAME);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int x = left + PAD;
        int right = rightColumn();
        graphics.drawCenteredString(font, title, left + WIDTH / 2, top + 8, COLOR_TEXT);

        // Левая колонка
        int coordX = x + NAME_WIDTH + 2 * FIELD_GAP;
        graphics.drawString(font, Component.translatable("gui.halgventmod.waypoint.name"), x, top + 24, COLOR_DIM, false);
        String[] axes = {"X", "Y", "Z"};
        for (int i = 0; i < axes.length; i++) {
            graphics.drawString(font, axes[i], coordX + i * (COORD_WIDTH + FIELD_GAP), top + 24, COLOR_DIM, false);
        }

        MapIcon[] icons = MapIcon.values();
        Component hiddenTitle = hiddenIcon == null ? Component.translatable("gui.halgventmod.point.icon_unknown")
                : hiddenIcon.title();
        graphics.drawString(font, Component.translatable("gui.halgventmod.point.icon_hidden", hiddenTitle),
                x, top + HIDDEN_ICONS_TOP - 10, COLOR_DIM, false);
        int hoveredHidden = cellAt(HIDDEN_ICONS_TOP, icons.length + 1, mouseX, mouseY);
        for (int i = 0; i <= icons.length; i++) {
            MapIcon each = i == 0 ? null : icons[i - 1];
            int cx = cellX(i);
            int cy = cellY(HIDDEN_ICONS_TOP, i);
            cell(graphics, cx, cy, each == hiddenIcon, i == hoveredHidden);
            if (each == null) {
                PointShapes.drawUnknown(graphics, font, cx + ICON_CELL / 2.0F, cy + ICON_CELL / 2.0F, ICON_SIZE - 2, 255);
            } else {
                MapIcon.draw(graphics, each.cell(dye), cx + 1, cy + 1, ICON_SIZE);
            }
        }

        graphics.drawString(font, Component.translatable("gui.halgventmod.point.icon_open", icon.title()),
                x, top + OPEN_ICONS_TOP - 10, COLOR_DIM, false);
        int hoveredOpen = cellAt(OPEN_ICONS_TOP, icons.length, mouseX, mouseY);
        for (int i = 0; i < icons.length; i++) {
            int cx = cellX(i);
            int cy = cellY(OPEN_ICONS_TOP, i);
            cell(graphics, cx, cy, icons[i] == icon, i == hoveredOpen);
            MapIcon.draw(graphics, icons[i].cell(dye), cx + 1, cy + 1, ICON_SIZE);
        }

        graphics.drawString(font, Component.translatable("gui.halgventmod.waypoint.flag_color",
                Component.translatable("color.minecraft." + dye.getName())), x, top + DYE_TOP - 10, COLOR_DIM, false);
        for (DyeColor each : DyeColor.values()) {
            int sx = dyeX(each.getId());
            int sy = top + DYE_TOP;
            graphics.fill(sx - 1, sy - 1, sx + DYE_SWATCH + 1, sy + DYE_SWATCH + 1, each == dye ? 0xFFFFFFFF : 0xFF000000);
            graphics.fill(sx, sy, sx + DYE_SWATCH, sy + DYE_SWATCH, 0xFF000000 | each.getTextureDiffuseColor());
        }
        graphics.drawString(font, Component.translatable("gui.halgventmod.point.hover_text"), x, top + HOVER_TOP - 10,
                COLOR_DIM, false);

        // Правая колонка
        BannerEntry chosen = BannerRegistry.client(banner);
        Component bannerName = banner.isEmpty() ? Component.translatable("gui.halgventmod.point.no_banner")
                : chosen != null ? Component.literal(chosen.name() + " (" + banner + ")")
                : Component.translatable("gui.halgventmod.point.missing_banner", banner);
        graphics.drawString(font, Component.translatable("gui.halgventmod.point.banner", bannerName),
                right, top + BANNER_ROW_TOP - 10, COLOR_DIM, false);
        List<String> recent = recentBanners();
        int hoveredBanner = describedBox.selected() ? bannerCellAt(recent.size(), mouseX, mouseY) : -1;
        for (int i = 0; i <= recent.size(); i++) {
            String id = i == 0 ? "" : recent.get(i - 1);
            int bx = right + i * BANNER_CELL;
            int by = top + BANNER_ROW_TOP;
            int frame = id.equals(banner) ? COLOR_SELECTED : i == hoveredBanner ? COLOR_HOVER : 0;
            if (frame != 0) {
                graphics.fill(bx, by, bx + BANNER_CELL - 2, by + 2 * BANNER_WIDTH + 2, frame);
            }
            graphics.fill(bx + 1, by + 1, bx + BANNER_CELL - 3, by + 2 * BANNER_WIDTH + 1, COLOR_CELL);
            if (i == 0) {
                graphics.drawCenteredString(font, "—", bx + (BANNER_CELL - 2) / 2, by + BANNER_WIDTH - 4, COLOR_TEXT);
            } else {
                BannerDraw.draw(graphics, BannerRegistry.client(id), bx + 1, by + 1, BANNER_WIDTH);
            }
        }
        graphics.drawString(font, Component.translatable("gui.halgventmod.point.description"), right,
                top + DESCRIPTION_TOP - 10, COLOR_DIM, false);
        graphics.drawString(font, Component.translatable("gui.halgventmod.point.info"), right, top + INFO_TOP - 10,
                COLOR_DIM, false);
        if (!describedBox.selected()) {
            int from = top + BANNER_ROW_TOP - 12;
            graphics.fill(right - 2, from, right + COLUMN + 2, top + INFO_TOP + INFO_HEIGHT + 2, COLOR_DISABLED);
        }

        // Подсказки к значкам и знамёнам
        if (hoveredHidden >= 0) {
            graphics.renderTooltip(font, hoveredHidden == 0 ? Component.translatable("gui.halgventmod.point.icon_unknown")
                    : icons[hoveredHidden - 1].title(), mouseX, mouseY);
        } else if (hoveredOpen >= 0) {
            graphics.renderTooltip(font, icons[hoveredOpen].title(), mouseX, mouseY);
        } else if (hoveredBanner > 0) {
            BannerEntry entry = BannerRegistry.client(recent.get(hoveredBanner - 1));
            if (entry != null) {
                graphics.renderTooltip(font, Component.literal(entry.name()), mouseX, mouseY);
            }
        }
    }

    private static void cell(GuiGraphics graphics, int x, int y, boolean selected, boolean hovered) {
        int frame = selected ? COLOR_SELECTED : hovered ? COLOR_HOVER : 0;
        if (frame != 0) {
            graphics.fill(x, y, x + ICON_CELL, y + ICON_CELL, frame);
        }
        graphics.fill(x + 1, y + 1, x + ICON_CELL - 1, y + ICON_CELL - 1, COLOR_CELL);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Enter сохраняет только из однострочных полей: в многострочных это перевод строки
        if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && getFocused() instanceof EditBox) {
            save();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
