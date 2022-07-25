package muramasa.antimatter.datagen.providers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.datagen.AntimatterRuntimeResourceGeneration;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.devtech.arrp.json.blockstate.JMultipart;
import net.devtech.arrp.util.UnsafeByteArrayOutputStream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AntimatterAdvancementProvider implements DataProvider, IAntimatterProvider {

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
            else {
                AntimatterRuntimeResourceGeneration.DYNAMIC_RESOURCE_PACK.addData(fix(a.getId(), "advancements", "json"), serialize(a.deconstruct()));
                //DynamicResourcePack.addAdvancement(a.getId(), a.deconstruct().serializeToJson());
            }
        };
        advancements.forEach(a -> a.accept(consumer));
    }

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Advancement.Builder.class, (JsonSerializer<Advancement.Builder>) (src, typeOfSrc, context) -> src.serializeToJson())
            .create();
    private static byte[] serialize(Object object) {
        UnsafeByteArrayOutputStream ubaos = new UnsafeByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(ubaos);
        GSON.toJson(object, writer);
        try {
            writer.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return ubaos.getBytes();
    }

    private static ResourceLocation fix(ResourceLocation identifier, String prefix, String append) {
        return new ResourceLocation(identifier.getNamespace(), prefix + '/' + identifier.getPath() + '.' + append);
    }

    @Override
    public void run(@Nonnull HashCache cache) {
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

    public static Advancement.Builder buildRootAdvancement(ItemLike provider, ResourceLocation backgroundPath, String title, String desc, FrameType type, boolean toast, boolean announce, boolean hide) {
        return Advancement.Builder.advancement().display(provider, new TranslatableComponent(title), new TranslatableComponent(desc), backgroundPath, type, toast, announce, hide).rewards(AdvancementRewards.Builder.experience(10));
    }

    public static Advancement.Builder buildAdvancement(Advancement parent, ItemLike provider, String title, String desc, FrameType type, boolean toast, boolean announce, boolean hide) {
        return Advancement.Builder.advancement().parent(parent).display(provider, new TranslatableComponent(title), new TranslatableComponent(desc), null, type, toast, announce, hide).rewards(AdvancementRewards.Builder.experience(10));
    }

    public static String getLoc(String domain, String id) {
        return String.join(":", domain, id);
    }

}
