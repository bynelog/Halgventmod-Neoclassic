package com.nrdhvnstd.halgventmod.client.character;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.nrdhvnstd.halgventmod.HalgventMod;
import com.nrdhvnstd.halgventmod.character.Appearance;
import com.nrdhvnstd.halgventmod.character.CharacterData;
import com.nrdhvnstd.halgventmod.registry.ModAttachments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

/**
 * Скин игрока из облика (геймдок 3.1: скин лаунчера не используется). Скин 64×64 собирается из слоёв
 * textures/entity/appearance/ снизу вверх; серые слои красятся цветом кожи, глаз или волос:
 * <pre>
 * body/&lt;пол&gt;      — тело (цвет кожи)        scar/&lt;шрам&gt;       — шрам
 * face/&lt;пол&gt;      — белки глаз, рот, нос     tattoo/&lt;татуировка&gt; — татуировка
 * eyes            — радужка (цвет глаз)     clothes/&lt;пол&gt;      — исподнее: рубаха и брэ
 * brows           — брови (цвет волос)      beard/&lt;борода&gt;     — борода (цвет волос)
 * age/&lt;возраст&gt;  — морщины                hair/&lt;причёска&gt;    — волосы (цвет волос)
 * </pre>
 * Нет файла — слоя нет (так и задуманы «none» и «bald»). Слой не 64×64 — пропускается с предупреждением.
 * Собранные скины кэшируются по облику; кэш сбрасывается при перезагрузке ресурсов и выходе из мира.
 */
@EventBusSubscriber(modid = HalgventMod.MODID, value = Dist.CLIENT)
public final class AppearanceSkins {
    private static final int SIZE = 64;
    private static final String ROOT = "textures/entity/appearance/";
    private static final int NO_TINT = -1;

    private static final Map<String, PlayerSkin> CACHE = new HashMap<>();
    private static int nextId;

    // Окно создания персонажа: облик, который сейчас выбирают, — на время рисования фигурки
    @Nullable
    private static Map<String, String> preview;

    private AppearanceSkins() {}

    public static void setPreview(@Nullable Map<String, String> appearance) {
        preview = appearance;
    }

    /** Скин игрока по облику; null — собрать пока нельзя (не поток отрисовки), пусть будет ванильный. */
    @Nullable
    public static PlayerSkin skinOf(AbstractClientPlayer player) {
        Map<String, String> appearance;
        if (preview != null && player == Minecraft.getInstance().player) {
            appearance = preview;
        } else {
            CharacterData data = player.getExistingDataOrNull(ModAttachments.CHARACTER);
            appearance = data == null || data.appearance().isEmpty() ? Appearance.DEFAULT : data.appearance();
        }
        String key = Appearance.key(appearance);
        PlayerSkin skin = CACHE.get(key);
        if (skin == null && RenderSystem.isOnRenderThread()) {
            skin = build(appearance);
            CACHE.put(key, skin);
        }
        return skin;
    }

    private static PlayerSkin build(Map<String, String> appearance) {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceManager manager = minecraft.getResourceManager();
        String sex = Appearance.get(appearance, Appearance.Part.SEX);
        int hair = Appearance.hairColor(appearance);

        NativeImage image = new NativeImage(SIZE, SIZE, true);
        draw(image, manager, "body/" + sex, Appearance.skinColor(appearance));
        draw(image, manager, "face/" + sex, NO_TINT);
        draw(image, manager, "eyes", Appearance.eyeColor(appearance));
        draw(image, manager, "brows", hair);
        draw(image, manager, "age/" + Appearance.get(appearance, Appearance.Part.AGE), NO_TINT);
        draw(image, manager, "scar/" + Appearance.get(appearance, Appearance.Part.SCAR), NO_TINT);
        draw(image, manager, "tattoo/" + Appearance.get(appearance, Appearance.Part.TATTOO), NO_TINT);
        draw(image, manager, "clothes/" + sex, NO_TINT);
        draw(image, manager, "beard/" + Appearance.get(appearance, Appearance.Part.BEARD), hair);
        draw(image, manager, "hair/" + Appearance.get(appearance, Appearance.Part.HAIR), hair);

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(HalgventMod.MODID, "appearance/skin_" + nextId++);
        minecraft.getTextureManager().register(id, new DynamicTexture(image));
        boolean slim = "slim".equals(Appearance.get(appearance, Appearance.Part.BUILD));
        return new PlayerSkin(id, null, null, null, slim ? PlayerSkin.Model.SLIM : PlayerSkin.Model.WIDE, false);
    }

    /** Наложить слой поверх (с учётом прозрачности); tint — RGB, на который умножается серый слой. */
    private static void draw(NativeImage target, ResourceManager manager, String layer, int tint) {
        NativeImage image = read(manager, layer);
        if (image == null) {
            return;
        }
        try {
            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) {
                    int src = image.getPixelRGBA(x, y); // ABGR
                    int alpha = src >>> 24;
                    if (alpha == 0) {
                        continue;
                    }
                    int r = src & 0xFF;
                    int g = src >> 8 & 0xFF;
                    int b = src >> 16 & 0xFF;
                    if (tint != NO_TINT) {
                        r = r * (tint >> 16 & 0xFF) / 255;
                        g = g * (tint >> 8 & 0xFF) / 255;
                        b = b * (tint & 0xFF) / 255;
                    }
                    int dst = target.getPixelRGBA(x, y);
                    int dstAlpha = dst >>> 24;
                    if (alpha < 255 && dstAlpha > 0) {
                        // «Поверх»: доля нижнего — его непрозрачность, умноженная на просвет верхнего
                        int under = dstAlpha * (255 - alpha) / 255;
                        int outAlpha = alpha + under;
                        r = (r * alpha + (dst & 0xFF) * under) / outAlpha;
                        g = (g * alpha + (dst >> 8 & 0xFF) * under) / outAlpha;
                        b = (b * alpha + (dst >> 16 & 0xFF) * under) / outAlpha;
                        alpha = outAlpha;
                    }
                    target.setPixelRGBA(x, y, alpha << 24 | b << 16 | g << 8 | r);
                }
            }
        } finally {
            image.close();
        }
    }

    @Nullable
    private static NativeImage read(ResourceManager manager, String layer) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(HalgventMod.MODID, ROOT + layer + ".png");
        Optional<Resource> resource = manager.getResource(id);
        if (resource.isEmpty()) {
            return null;
        }
        try (InputStream in = resource.get().open()) {
            NativeImage image = NativeImage.read(in);
            if (image.getWidth() != SIZE || image.getHeight() != SIZE) {
                HalgventMod.LOGGER.warn("Слой облика {}: нужен размер {}×{}", id, SIZE, SIZE);
                image.close();
                return null;
            }
            return image;
        } catch (IOException e) {
            HalgventMod.LOGGER.warn("Слой облика {} не прочитан: {}", id, e.toString());
            return null;
        }
    }

    // ===== Сброс кэша =====

    public static void clear() {
        for (PlayerSkin skin : CACHE.values()) {
            Minecraft.getInstance().getTextureManager().release(skin.texture()); // закрывает и картинку
        }
        CACHE.clear();
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> clear());
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clear();
    }
}
