package muramasa.antimatter.datagen.providers;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.util.Utils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AntimatterAdvancementProvider implements DataProvider, IAntimatterProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Consumer<Consumer<Advancement>>> advancements;
    private final String providerDomain, providerName;

    @SafeVarargs
    public AntimatterAdvancementProvider(String providerDomain, String providerName, Consumer<Consumer<Advancement>>... advancements) {
        this.providerDomain = providerDomain;
        this.providerName = providerName;
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
            else {
                AntimatterDynamics.RUNTIME_DATA_PACK.addData(AntimatterDynamics.fix(a.getId(), "advancements", "json"), AntimatterDynamics.serialize(a.deconstruct()));
            }
        };
        advancements.forEach(a -> a.accept(consumer));
    }

    @Override
    public void run(@NotNull CachedOutput cache) {
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

    @NotNull
    @Override
    public String getName() {
        return providerName;
    }

    public static Advancement.Builder buildRootAdvancement(ItemLike provider, ResourceLocation backgroundPath, String title, String desc, FrameType type, boolean toast, boolean announce, boolean hide) {
        return Advancement.Builder.advancement().display(provider, Utils.translatable(title), Utils.translatable(desc), backgroundPath, type, toast, announce, hide).rewards(AdvancementRewards.Builder.experience(10));
    }

    public static Advancement.Builder buildAdvancement(Advancement parent, ItemLike provider, String title, String desc, FrameType type, boolean toast, boolean announce, boolean hide) {
        return Advancement.Builder.advancement().parent(parent).display(provider, Utils.translatable(title), Utils.translatable(desc), null, type, toast, announce, hide).rewards(AdvancementRewards.Builder.experience(10));
    }

    public static String getLoc(String domain, String id) {
        return String.join(":", domain, id);
    }

}
