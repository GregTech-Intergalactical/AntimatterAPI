package muramasa.antimatter.datagen.providers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class AntimatterAdvancementProvider implements IDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator gen;
    private final List<Consumer<Consumer<Advancement>>> advancements;
    private String providerDomain, providerName;

    public AntimatterAdvancementProvider(String providerDomain, String providerName, DataGenerator gen, Consumer<Consumer<Advancement>>... advancements) {
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.gen = gen;
        if (advancements.length == 0) throw new IllegalArgumentException("AntimatterAdvancementProvider requires at least one Advancement class.");
        this.advancements = Arrays.asList(advancements);
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        Path folder = this.gen.getOutputFolder();
        Set<ResourceLocation> locs = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            if (!locs.add(advancement.getId())) throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            else {
                Path path = getPath(folder, advancement);
                try {
                    IDataProvider.save(GSON, cache, advancement.copy().serialize(), path);
                } catch (IOException e) {
                    LOGGER.error("Couldn't save advancement {}", path, e);
                }
            }
        };
        advancements.forEach(a -> a.accept(consumer));
    }

    private Path getPath(Path path, Advancement advancement) {
        return path.resolve(String.join("", "data/", providerDomain, "/advancements/", advancement.getId().getPath(), ".json"));
    }

    @Override
    public String getName() {
        return providerName;
    }

    public static Advancement.Builder buildRootAdvancement(IItemProvider provider, ResourceLocation backgroundPath, String title, String desc, FrameType type, boolean toast, boolean announce, boolean hide) {
        return Advancement.Builder.builder().withDisplay(provider, new TranslationTextComponent(title), new TranslationTextComponent(desc), backgroundPath, type, toast, announce, hide);
    }

    public static Advancement.Builder buildAdvancement(IItemProvider provider, String title, String desc, FrameType type, boolean toast, boolean announce, boolean hide) {
        return Advancement.Builder.builder().withDisplay(provider, new TranslationTextComponent(title), new TranslationTextComponent(desc), null, type, toast, announce, hide);
    }

}
