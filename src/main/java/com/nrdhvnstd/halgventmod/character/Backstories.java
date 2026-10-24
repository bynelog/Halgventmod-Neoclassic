package com.nrdhvnstd.halgventmod.character;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.nrdhvnstd.halgventmod.HalgventMod;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

/**
 * Предыстории — данные датапака: data/<пространство>/halgvent_backstory/<id>.json. Сервер читает их при запуске
 * и /reload; клиенту они приходят в окне создания персонажа (CharacterOpenPayload).
 * <pre>
 * {
 *   "order": 1,                                          // место в списке
 *   "armor": [{"id": "halgventmod:cale"}, ...],          // надевается, каждая вещь — в свою ячейку брони
 *   "items": [{"id": "halgventmod:rye_bread", "count": 2}, ...] // в хотбар по порядку, что не влезло — в инвентарь
 * }
 * </pre>
 * Вещь — как ItemStack в датапаке (id, count, components) и необязательный "wear": доля износа от 0 до 1.
 * Ошибка в вещи (например, такого предмета ещё нет) — предупреждение в лог, вещь пропускается.
 */
@EventBusSubscriber(modid = HalgventMod.MODID)
public final class Backstories {
    public static final String DIR = "halgvent_backstory";
    private static final Gson GSON = new Gson();

    private static List<Backstory> all = List.of();

    private Backstories() {}

    /** Все предыстории по порядку. */
    public static List<Backstory> all() {
        return all;
    }

    @Nullable
    public static Backstory get(@Nullable ResourceLocation id) {
        for (Backstory backstory : all) {
            if (backstory.id().equals(id)) {
                return backstory;
            }
        }
        return null;
    }

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new Loader(event.getRegistryAccess()));
    }

    private static final class Loader extends SimpleJsonResourceReloadListener {
        private final RegistryAccess registries;

        Loader(RegistryAccess registries) {
            super(GSON, DIR);
            this.registries = registries;
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
            RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
            List<Backstory> loaded = new ArrayList<>();
            files.forEach((id, json) -> {
                if (!json.isJsonObject()) {
                    HalgventMod.LOGGER.warn("Предыстория {}: ожидался объект", id);
                    return;
                }
                JsonObject object = json.getAsJsonObject();
                int order = GsonHelper.getAsInt(object, "order", 1000);
                loaded.add(new Backstory(id, order, stacks(id, object, "armor", ops), stacks(id, object, "items", ops)));
            });
            loaded.sort(Comparator.comparingInt(Backstory::order).thenComparing(Backstory::id));
            all = List.copyOf(loaded);
            HalgventMod.LOGGER.info("Предысторий: {}", all.size());
        }

        private static List<ItemStack> stacks(ResourceLocation id, JsonObject object, String key, RegistryOps<JsonElement> ops) {
            List<ItemStack> stacks = new ArrayList<>();
            JsonArray array = GsonHelper.getAsJsonArray(object, key, new JsonArray());
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    HalgventMod.LOGGER.warn("Предыстория {}: в «{}» ожидался объект вещи", id, key);
                    continue;
                }
                JsonObject entry = element.getAsJsonObject().deepCopy();
                JsonElement wear = entry.remove("wear");
                ItemStack stack = ItemStack.CODEC.parse(ops, entry)
                        .resultOrPartial(error -> HalgventMod.LOGGER.warn("Предыстория {}, «{}»: {}", id, key, error))
                        .orElse(ItemStack.EMPTY);
                if (stack.isEmpty()) {
                    continue;
                }
                if (wear != null && wear.isJsonPrimitive() && wear.getAsJsonPrimitive().isNumber() && stack.isDamageableItem()) {
                    float share = Math.clamp(wear.getAsFloat(), 0.0F, 1.0F);
                    stack.setDamageValue(Math.min(stack.getMaxDamage() - 1, Math.round(stack.getMaxDamage() * share)));
                }
                stacks.add(stack);
            }
            return stacks;
        }
    }
}
