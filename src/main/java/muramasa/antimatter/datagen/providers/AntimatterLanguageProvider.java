package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import static muramasa.antimatter.material.MaterialType.*;
import static muramasa.antimatter.util.Utils.*;

public class AntimatterLanguageProvider extends LanguageProvider {

    private final String providerDomain, providerName, locale;

    public AntimatterLanguageProvider(String providerDomain, String providerName, String locale, DataGenerator gen) {
        super(gen, providerDomain, locale);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        processTranslations(providerDomain, locale);
    }

    protected void processTranslations(String domain, String locale) {
        if (!locale.startsWith("en")) return;
        AntimatterAPI.all(ItemBasic.class).stream().filter(i -> i.getDomain().equals(domain)).forEach(item -> {
            add(item, lowerUnderscoreToUpperSpaced(item.getId()));
        });
        AntimatterAPI.all(Material.class).stream().filter(m -> m.getDomain().equals(domain)).forEach(mat -> {
            add("material.".concat(mat.getId()), getLocalizedType(mat));
        });
        AntimatterAPI.all(StoneType.class).forEach(s -> {
            IMaterialTag.all(ORE, ORE_SMALL).stream().filter(m -> m.getDomain().equals(domain)).forEach(m -> {
                if (m.has(ORE)) {
                    add(ORE.get().get(m, s).asBlock(),
                            String.join("", getLocalizedType(m), " ", getLocalizedType(s), " ", getLocalizedType(ORE)));
                }
                if (m.has(ORE_SMALL)) {
                    add(ORE_SMALL.get().get(m, s).asBlock(),
                            String.join("", getLocalizedType(m), " ", getLocalizedType(s), " ", getLocalizedType(ORE_SMALL)));
                }
            });
        });
        AntimatterAPI.all(BlockStone.class).stream().filter(s -> s.getDomain().equals(domain)).forEach(s -> add(s, getLocalizedType(s)));
        AntimatterAPI.all(BlockStorage.class).stream()
                .filter(storage -> storage.getMaterial().getDomain().equals(domain)).forEach(block -> {
                    MaterialType<?> type = block.getType();
                    add(block, String.join("", getLocalizedType(block.getMaterial()), " ", getLocalizedType(type)));
        });
        AntimatterAPI.all(MaterialItem.class).stream()
                .filter(i -> i.getMaterial().getDomain().equals(domain)).forEach(item -> {
                    MaterialType<?> type = item.getType();
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
        add(Ref.TAB_BLOCKS.getTranslationKey(), "Antimatter Blocks");
        add(Ref.TAB_ITEMS.getTranslationKey(), "Antimatter Items");
        add(Ref.TAB_MACHINES.getTranslationKey(), "Antimatter Machines");
        add(Ref.TAB_MATERIALS.getTranslationKey(), "Antimatter Material Items");
        add(Ref.TAB_TOOLS.getTranslationKey(), "Antimatter Tools");
    }

    @Override
    public String getName() {
        return providerName;
    }

}
