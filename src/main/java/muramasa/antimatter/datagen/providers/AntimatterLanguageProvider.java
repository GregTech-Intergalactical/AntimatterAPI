package muramasa.antimatter.datagen.providers;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

import static muramasa.antimatter.material.MaterialType.*;
import static muramasa.antimatter.util.Utils.*;

public class AntimatterLanguageProvider implements IDataProvider, IAntimatterProvider {

    private final String providerDomain, providerName, locale;
    private final Object2ObjectMap<String, String> data = new Object2ObjectRBTreeMap<>();
    private final DataGenerator gen;

    public AntimatterLanguageProvider(String providerDomain, String providerName, String locale, DataGenerator gen) {
        this.gen = gen;
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.locale = locale;
    }

    @Override
    public void run() {
        addTranslations();
        data.forEach((k, v) -> DynamicResourcePack.addLangLoc(providerDomain, locale, k, v));
    }

    @Override
    public Dist getSide() {
        return Dist.CLIENT;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        addTranslations();
        if (!data.isEmpty()) save(cache, data, this.gen.getOutputFolder().resolve(String.join("", "assets/", providerDomain, "/lang/", locale, ".json")));
    }

    // Forge implementation
    @SuppressWarnings("all")
    private void save(DirectoryCache cache, Object object, Path target) throws IOException {
        String data = Ref.GSON.toJson(object);
        data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data); // Escape unicode after the fact so that it's not double escaped by GSON
        String hash = HASH_FUNCTION.hashUnencodedChars(data).toString();
        if (!Objects.equals(cache.getPreviousHash(target), hash) || !Files.exists(target)) {
            Files.createDirectories(target.getParent());
            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(target)) {
                bufferedwriter.write(data);
            }
        }
        cache.recordHash(target, hash);
    }

    protected void addTranslations() {
        processTranslations(providerDomain, locale);
        if (providerDomain.equals(Ref.ID)) processAntimatterTranslations();
    }

    protected void processTranslations(String domain, String locale) {
        if (!locale.startsWith("en")) return;
        AntimatterAPI.all(ItemBasic.class, domain).forEach(i -> add(i, lowerUnderscoreToUpperSpaced(i.getId())));
        AntimatterAPI.all(Material.class, domain).forEach(m -> add("material.".concat(m.getId()), getLocalizedType(m)));
        AntimatterAPI.all(BlockOre.class, domain, o -> {
            if (o.getOreType() == ORE) add(o, String.join("", getLocalizedType(o.getMaterial()), " ", getLocalizedType(o.getStoneType()), " Ore"));
            else add(o, String.join("", "Small ", getLocalizedType(o.getMaterial()), " ", getLocalizedType(o.getStoneType()), " Ore"));
        });

        AntimatterAPI.all(BlockStone.class, domain).forEach(s -> add(s, getLocalizedType(s)));
        AntimatterAPI.all(BlockStorage.class, domain).forEach(block -> add(block, String.join("", getLocalizedType(block.getMaterial()), " ", getLocalizedType(block.getType()))));
        AntimatterAPI.all(MaterialItem.class, domain).forEach(item -> {
            MaterialType<?> type = item.getType();
            if (type == ROCK) add(item, String.join("", getLocalizedType(item.getMaterial()), " Bearing Rock"));
            else if (type == CRUSHED) add(item, String.join("", "Crushed ", getLocalizedType(item.getMaterial()), " Ore"));
            else if (type == CRUSHED_PURIFIED) add(item, String.join("", "Purified Crushed ", getLocalizedType(item.getMaterial()), " Ore"));
            else if (type == CRUSHED_CENTRIFUGED) add(item, String.join("", "Centrifuged Crushed ", getLocalizedType(item.getMaterial()), " Ore"));
            else {
                String[] split = getLocalizedMaterialType(type);
                if (split.length > 1) add(item, String.join("", split[0], " ", getLocalizedType(item.getMaterial()), " ", split[1]));
                else add(item, String.join("", split[0], " ", getLocalizedType(item.getMaterial())));
            }
        });
    }

    private void processAntimatterTranslations() {
        add(Ref.TAB_BLOCKS, "Antimatter Blocks");
        add(Ref.TAB_ITEMS, "Antimatter Items");
        add(Ref.TAB_MACHINES, "Antimatter Machines");
        add(Ref.TAB_MATERIALS, "Antimatter Material Items");
        add(Ref.TAB_TOOLS, "Antimatter Tools");
    }

    @Override
    public String getName() {
        return providerName;
    }

    public void addBlock(Supplier<? extends Block> key, String name) {
        add(key.get(), name);
    }

    public void add(Block key, String name) {
        add(key.getTranslationKey(), name);
    }

    public void addItem(Supplier<? extends Item> key, String name) {
        add(key.get(), name);
    }

    public void add(Item key, String name) {
        add(key.getTranslationKey(), name);
    }

    public void addItemStack(Supplier<ItemStack> key, String name) {
        add(key.get(), name);
    }

    public void add(ItemStack key, String name) {
        add(key.getTranslationKey(), name);
    }

    public void addEnchantment(Supplier<? extends Enchantment> key, String name) {
        add(key.get(), name);
    }

    public void add(Enchantment key, String name) {
        add(key.getName(), name);
    }

    public void addBiome(Supplier<? extends Biome> key, String name) {
        add(key.get(), name);
    }

    public void add(Biome key, String name) {
        add(key.getTranslationKey(), name);
    }

    public void addEffect(Supplier<? extends Effect> key, String name) {
        add(key.get(), name);
    }

    public void add(Effect key, String name) {
        add(key.getName(), name);
    }

    public void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
        add(key.get(), name);
    }

    public void add(EntityType<?> key, String name) {
        add(key.getTranslationKey(), name);
    }

    public void addItemGroup(Supplier<? extends ItemGroup> key, String name) {
        add(key.get(), name);
    }

    public void add(ItemGroup key, String name) {
        add(key.getTranslationKey(), name);
    }

    public void add(String key, String value) {
        if (data.put(key, value) != null) {
            throw new IllegalStateException("Duplicate translation key " + key);
        }
    }

}
