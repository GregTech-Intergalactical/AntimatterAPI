package muramasa.antimatter.datagen;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

public interface IAntimatterLanguageProvider {

    Map<String, String> data = new TreeMap<>();

    void processTranslations();

    default void addBlock(Supplier<? extends Block> key, String name) {
        add(key.get(), name);
    }

    default void add(Block key, String name) {
        add(key.getTranslationKey(), name);
    }

    default void addItem(Supplier<? extends Item> key, String name) {
        add(key.get(), name);
    }

    default void add(Item key, String name) { add(key.getTranslationKey(), name); }

    default void addItemStack(Supplier<ItemStack> key, String name) {
        add(key.get(), name);
    }

    default void add(ItemStack key, String name) {
        add(key.getTranslationKey(), name);
    }

    default void addEnchantment(Supplier<? extends Enchantment> key, String name) {
        add(key.get(), name);
    }

    default void add(Enchantment key, String name) {
        add(key.getName(), name);
    }

    default void addBiome(Supplier<? extends Biome> key, String name) {
        add(key.get(), name);
    }

    default void add(Biome key, String name) { add(key.getTranslationKey(), name); }

    default void addEffect(Supplier<? extends Effect> key, String name) {
        add(key.get(), name);
    }

    default void add(Effect key, String name) {
        add(key.getName(), name);
    }

    default void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
        add(key.get(), name);
    }

    default void add(EntityType<?> key, String name) {
        add(key.getTranslationKey(), name);
    }

    default void add(String key, String value) {
        if (data.put(key, value) != null) throw new IllegalStateException("Duplicate translation key " + key);
    }

}
