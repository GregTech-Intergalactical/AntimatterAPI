package muramasa.antimatter.datagen.providers;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AntimatterAdvancementProvider implements IDataProvider, IAntimatterProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator gen;
    private final List<Consumer<Consumer<Advancement>>> advancements;
    private final String providerDomain, providerName;

    @SafeVarargs
    public AntimatterAdvancementProvider(String providerDomain, String providerName, DataGenerator gen, Consumer<Consumer<Advancement>>... advancements) {
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.gen = gen;
        if (advancements.length == 0)
            throw new IllegalArgumentException("AntimatterAdvancementProvider requires at least one Advancement class.");
        this.advancements = Arrays.asList(advancements);
    }

    @Override
    public void run() {

    }

    @Override
    public void onCompletion() {
        Set<ResourceLocation> locs = new ObjectOpenHashSet<>();
        Consumer<Advancement> consumer = a -> {
            if (!locs.add(a.getId())) throw new IllegalStateException("Duplicate advancement " + a.getId());
            else DynamicResourcePack.addAdvancement(a.getId(), a.copy().serialize());
        };
        advancements.forEach(a -> a.accept(consumer));
    }

    @Override
    public void act(@Nonnull DirectoryCache cache) {
        /*Path folder = this.gen.getOutputFolder();
        Set<ResourceLocation> locs = new ObjectOpenHashSet<>();
        Consumer<Advancement> consumer = a -> {
            if (!locs.add(a.getId())) throw new IllegalStateException("Duplicate advancement " + a.getId());
            else {
                Path path = getPath(folder, a);
                try {
                    IDataProvider.save(Ref.GSON, cache, a.copy().serialize(), path);
                } catch (IOException e) {
                    LOGGER.error("Couldn't save advancement {}", path, e);
                }
            }
        };
        advancements.forEach(a -> a.accept(consumer));*/
    }

    private Path getPath(Path path, Advancement advancement) {
        return path.resolve(String.join("", "data/", providerDomain, "/advancements/", advancement.getId().getPath(), ".json"));
    }

    @Nonnull
    @Override
    public String getName() {
        return providerName;
    }

    public static Advancement.Builder buildRootAdvancement(IItemProvider provider, ResourceLocation backgroundPath, String title, String desc, FrameType type, boolean toast, boolean announce, boolean hide) {
        return Advancement.Builder.builder().withDisplay(provider, new TranslationTextComponent(title), new TranslationTextComponent(desc), backgroundPath, type, toast, announce, hide).withRewards(AdvancementRewards.Builder.experience(10));
    }

    public static Advancement.Builder buildAdvancement(Advancement parent, IItemProvider provider, String title, String desc, FrameType type, boolean toast, boolean announce, boolean hide) {
        return Advancement.Builder.builder().withParent(parent).withDisplay(provider, new TranslationTextComponent(title), new TranslationTextComponent(desc), null, type, toast, announce, hide).withRewards(AdvancementRewards.Builder.experience(10));
    }

    public static String getLoc(String domain, String id) {
        return String.join(":", domain, id);
    }

}
