package com.nrdhvnstd.halgventmod.character;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Предыстория (геймдок 3.1): кем персонаж был до начала игры. Определяет стартовый комплект —
 * одежду и броню (надевается) и вещи в хотбар. Данные датапака, см. Backstories.
 * Название и описание — переводы backstory.<пространство>.<id> и backstory.<пространство>.<id>.desc.
 */
public record Backstory(ResourceLocation id, int order, List<ItemStack> armor, List<ItemStack> items) {
    public static final int MAX_STACKS = 64;

    public static final StreamCodec<RegistryFriendlyByteBuf, Backstory> STREAM_CODEC = StreamCodec.of(
            (buf, backstory) -> backstory.write(buf), Backstory::read);

    public String langKey() {
        return "backstory." + id.getNamespace() + "." + id.getPath();
    }

    public Component title() {
        return Component.translatableWithFallback(langKey(), id.getPath());
    }

    public Component description() {
        return Component.translatableWithFallback(langKey() + ".desc", "");
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeVarInt(order);
        writeStacks(buf, armor);
        writeStacks(buf, items);
    }

    private static Backstory read(RegistryFriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        int order = buf.readVarInt();
        List<ItemStack> armor = readStacks(buf);
        List<ItemStack> items = readStacks(buf);
        return new Backstory(id, order, armor, items);
    }

    private static void writeStacks(RegistryFriendlyByteBuf buf, List<ItemStack> stacks) {
        int count = Math.min(stacks.size(), MAX_STACKS);
        buf.writeVarInt(count);
        for (int i = 0; i < count; i++) {
            ItemStack.STREAM_CODEC.encode(buf, stacks.get(i));
        }
    }

    private static List<ItemStack> readStacks(RegistryFriendlyByteBuf buf) {
        int count = Math.min(buf.readVarInt(), MAX_STACKS);
        List<ItemStack> stacks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            stacks.add(ItemStack.STREAM_CODEC.decode(buf));
        }
        return List.copyOf(stacks);
    }
}
