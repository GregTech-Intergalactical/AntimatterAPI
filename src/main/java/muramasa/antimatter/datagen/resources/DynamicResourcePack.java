package muramasa.antimatter.datagen.resources;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import mcp.MethodsReturnNonnullByDefault;
import muramasa.antimatter.Ref;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.IGeneratedBlockstate;
import net.minecraftforge.client.model.generators.ModelBuilder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DynamicResourcePack implements IResourcePack {

    protected static final ObjectSet<String> CLIENT_DOMAINS = new ObjectOpenHashSet<>();
    protected static final ObjectSet<String> SERVER_DOMAINS = new ObjectOpenHashSet<>();
    protected static final Object2ObjectMap<ResourceLocation, String> ASSETS = new Object2ObjectOpenHashMap<>();
    protected static final Object2ObjectMap<ResourceLocation, JsonObject> LANG = new Object2ObjectOpenHashMap<>();
    protected static final Object2ObjectMap<ResourceLocation, JsonObject> DATA = new Object2ObjectOpenHashMap<>();

    private final String name;

    static {
        CLIENT_DOMAINS.add(Ref.ID);
        SERVER_DOMAINS.addAll(Sets.newHashSet(Ref.ID, "minecraft", "forge"));
    }

    public DynamicResourcePack(String name, Collection<String> domains) {
        this.name = name;
        CLIENT_DOMAINS.addAll(domains);
        SERVER_DOMAINS.addAll(domains);
    }

    public static void addState(ResourceLocation loc, IGeneratedBlockstate state) {
        ASSETS.put(getStateLoc(loc), state.toJson().toString());
    }

    public static void addBlock(ResourceLocation loc, ModelBuilder<?> builder) {
        ASSETS.put(getBlockLoc(loc), builder.toJson().toString());
    }

    public static void addItem(ResourceLocation loc, ModelBuilder<?> builder) {
        ASSETS.put(getItemLoc(loc), builder.toJson().toString());
    }

    public static void addLangLoc(String domain, String locale, String key, String value) {
        JsonObject obj = new JsonObject();
        obj.addProperty(key, value);
        LANG.put(getLangLoc(domain, locale), obj);
    }

    public static void addAdvancement(ResourceLocation loc, JsonObject obj) {
        DATA.put(getAdvancementLoc(loc), obj);
    }

    // Must append 's' in the identifier
    public static void addTag(String identifier, ResourceLocation loc, JsonObject obj) {
        DATA.put(getTagLoc(identifier, loc), obj);
    }

    @Override
    public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
        if (type == ResourcePackType.SERVER_DATA) {
            if (DATA.get(location) != null) return new ByteArrayInputStream(DATA.get(location).toString().getBytes(StandardCharsets.UTF_8));
            else throw new FileNotFoundException("Can't find " + location + " " + getName());
        }
        if (ASSETS.get(location) == null) {
            if (DATA.get(location) != null) return new ByteArrayInputStream(DATA.get(location).toString().getBytes(StandardCharsets.UTF_8));
            throw new FileNotFoundException("Can't find " + location + " " + getName());
        }
        else return new ByteArrayInputStream(ASSETS.get(location).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public InputStream getRootResourceStream(String fileName) {
        throw new UnsupportedOperationException("Dynamic Resource Pack cannot have root resources");
    }

    @Override
    public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
        return ASSETS.containsKey(location) ? ASSETS.containsKey(location) : DATA.containsKey(location);
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespace, String path, int maxDepth, Predicate<String> filter) {
        if (type == ResourcePackType.SERVER_DATA) return DATA.keySet().stream().filter(loc -> loc.getPath().startsWith(path) && filter.test(loc.getPath())).collect(Collectors.toList());
        return ASSETS.keySet().stream().filter(loc -> loc.getPath().startsWith(path) && filter.test(loc.getPath())).collect(Collectors.toList());
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type) {
        return type == ResourcePackType.SERVER_DATA ? SERVER_DOMAINS : CLIENT_DOMAINS;
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) {
        return null;
    }

    @Override
    public void close() {
        //NOOP
    }

    //todo, pass string?
    public static ResourceLocation getStateLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "blockstates/", registryId.getPath(), ".json"));
    }

    public static ResourceLocation getBlockLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "models/", registryId.getPath(), ".json"));
    }

    public static ResourceLocation getItemLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "models/", registryId.getPath(), ".json"));
    }

    public static ResourceLocation getLangLoc(String domain, String locale) {
        return new ResourceLocation(domain, String.join("", "lang/", locale, ".json"));
    }

    public static ResourceLocation getAdvancementLoc(ResourceLocation advancementId) {
        return new ResourceLocation(advancementId.getNamespace(), String.join("", "advancements/", advancementId.getPath(), ".json"));
    }

    public static ResourceLocation getTagLoc(String identifier, ResourceLocation tagId) {
        return new ResourceLocation(tagId.getNamespace(), String.join("", "tags/", identifier, "/", tagId.getPath(), ".json"));
    }

}
