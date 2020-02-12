package muramasa.antimatter.datagen.providers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.blocks.BlockStone;
import muramasa.antimatter.blocks.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterLanguageProvider;
import muramasa.antimatter.items.ItemBasic;
import muramasa.antimatter.items.MaterialItem;
import muramasa.antimatter.materials.IMaterialTag;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static muramasa.antimatter.materials.MaterialType.*;
import static muramasa.antimatter.util.Utils.*;

public class AntimatterLanguageProvider implements IDataProvider, IAntimatterLanguageProvider {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator gen;
    private final String providerDomain, providerName, locale;
    private final List<IAntimatterLanguageProvider> additionalProviders;

    public AntimatterLanguageProvider(String providerDomain, String providerName, String locale, DataGenerator gen, IAntimatterLanguageProvider... additionalProviders) {
        this.gen = gen;
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.locale = locale;
        this.additionalProviders = Arrays.asList(additionalProviders);
    }

    @Override
    public void processTranslations() {
        AntimatterAPI.all(ItemBasic.class).stream().filter(i -> i.getDomain().equals(providerDomain)).forEach(item -> {
            add(item, lowerUnderscoreToUpperSpaced(item.getId()));
        });
        AntimatterAPI.all(Material.class).stream().filter(m -> m.getDomain().equals(providerDomain)).forEach(mat -> {
            add("material.".concat(mat.getId()), getLocalizedType(mat));
        });
        AntimatterAPI.all(StoneType.class).forEach(s -> {
            IMaterialTag.all(ORE, ORE_SMALL).stream().filter(m -> m.getDomain().equals(providerDomain)).forEach(m -> {
                if (m.has(ORE)) {
                    add(BlockOre.get(m, ORE, s).getBlock(),

                            String.join("", getLocalizedType(m), " ", getLocalizedType(s), " ", getLocalizedType(ORE)));
                }
                if (m.has(ORE_SMALL)) {
                    add(BlockOre.get(m, ORE_SMALL, s).getBlock(),
                            String.join("", getLocalizedType(m), " ", getLocalizedType(s), " ", getLocalizedType(ORE_SMALL)));
                }
            });
        });
        AntimatterAPI.all(BlockStone.class).stream().filter(s -> s.getDomain().equals(providerDomain)).forEach(s -> add(s, getLocalizedType(s)));
        AntimatterAPI.all(BlockStorage.class).stream()
                .filter(storage -> storage.getMaterial().getDomain().equals(providerDomain)).forEach(block -> {
                    MaterialType type = block.getType();
                    add(block, String.join("", getLocalizedType(block.getMaterial()), " ", getLocalizedType(type)));
        });
        AntimatterAPI.all(MaterialItem.class).stream()
                .filter(i -> i.getMaterial().getDomain().equals(providerDomain)).forEach(item -> {
                    MaterialType type = item.getType();
                    if (type == ROCK) {
                        add(item, String.join("", getLocalizedType(item.getMaterial()), " Bearing Rock"));
                    }
                    else if (type == CRUSHED) {
                        add(item, String.join("", "Crushed ", getLocalizedType(item.getMaterial()), " Ore"));
                    }
                    else if (type == CRUSHED_PURIFIED) {
                        add(item, String.join("", "Purified Crushed ", getLocalizedType(item.getMaterial()), " Ore"));
                    }
                    else if (type == CRUSHED_CENTRIFUGED) {
                        add(item, String.join("", "Centrifuged Crushed ", getLocalizedType(item.getMaterial()), " Ore"));
                    }
                    else {
                        String[] split = getLocalizedMaterialType(type);
                        if (split.length > 1) add(item, String.join("", split[0], " ", getLocalizedType(item.getMaterial()), " ", split[1]));
                        else add(item, String.join("", getLocalizedType(item.getMaterial()), " ", split[0]));
                    }
                });
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        processTranslations();
        for (IAntimatterLanguageProvider provider : additionalProviders) {
            provider.processTranslations();
        }
        if (!data.isEmpty()) {
            save(cache, data, this.gen.getOutputFolder().resolve("assets/" + providerDomain + "/lang/" + locale + ".json"));
        }
    }

    private void save(DirectoryCache cache, Object object, Path target) throws IOException {
        String data = GSON.toJson(object);
        data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data); // Escape unicode after the fact so that it's not double escaped by GSON
        String hash = IDataProvider.HASH_FUNCTION.hashUnencodedChars(data).toString();
        if (!Objects.equals(cache.getPreviousHash(target), hash) || !Files.exists(target)) {
            Files.createDirectories(target.getParent());
            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(target)) {
                bufferedwriter.write(data);
            }
        }
        cache.recordHash(target, hash);
    }

    @Override
    public String getName() {
        return providerName;
    }

}
