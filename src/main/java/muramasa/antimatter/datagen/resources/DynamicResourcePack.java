package muramasa.antimatter.datagen.resources;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.IGeneratedBlockstate;
import net.minecraftforge.client.model.generators.ModelBuilder;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DynamicResourcePack implements IResourcePack {

    protected static ObjectOpenHashSet<String> DOMAINS = new ObjectOpenHashSet<>();
    protected static Object2ObjectOpenHashMap<ResourceLocation, String> REGISTRY = new Object2ObjectOpenHashMap<>();
    protected static Object2ObjectOpenHashMap<ResourceLocation, JsonObject> LANG = new Object2ObjectOpenHashMap<>();

    protected String name;

    public DynamicResourcePack(String name) {
        this.name = name;
    }

    public static void addState(ResourceLocation loc, IGeneratedBlockstate state) {
        DOMAINS.add(loc.getNamespace());
        REGISTRY.put(getStateLoc(loc), state.toJson().toString());
    }

    public static void addBlock(ResourceLocation loc, ModelBuilder<?> builder) {
        DOMAINS.add(loc.getNamespace());
        REGISTRY.put(getBlockLoc(loc), builder.toJson().toString());
    }

    public static void addItem(ResourceLocation loc, ModelBuilder<?> builder) {
        DOMAINS.add(loc.getNamespace());
        REGISTRY.put(getItemLoc(loc), builder.toJson().toString());
    }

    public static void addLang(ResourceLocation loc, String key, String value) {
        LANG.computeIfAbsent(getLangLoc(loc), k -> new JsonObject()).addProperty(key, value);
    }

    @Override
    public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
        if (type == ResourcePackType.SERVER_DATA) throw new UnsupportedOperationException("Dynamic Resource Pack only supports client resources");
        String str = REGISTRY.get(location);
        if (str == null) throw new FileNotFoundException("Can't find " + location + " " + getName());
        else {
            return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public InputStream getRootResourceStream(String fileName) throws IOException {
        throw new UnsupportedOperationException("Dynamic Resource Pack cannot have root resources");
    }

    @Override
    public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
        if (type == ResourcePackType.SERVER_DATA) return false;
        return REGISTRY.containsKey(location);
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespace, String path, int maxDepth, Predicate<String> filter) {
        if (type == ResourcePackType.SERVER_DATA) return Collections.emptyList();
        return REGISTRY.keySet().stream().filter(loc -> loc.getPath().startsWith(path) && filter.test(loc.getPath())).collect(Collectors.toList());
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type) {
        return type == ResourcePackType.CLIENT_RESOURCES ? DOMAINS : Collections.emptySet();
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {
        //NOOP
    }

    public static ResourceLocation getStateLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), "blockstates/" + registryId.getPath() + ".json");
    }

    public static ResourceLocation getBlockLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), "models/block/" + registryId.getPath() + ".json");
    }

    public static ResourceLocation getItemLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), "models/item/" + registryId.getPath() + ".json");
    }

    public static ResourceLocation getLangLoc(ResourceLocation langId) {
        return new ResourceLocation(langId.getNamespace(), "/lang" + langId.getPath() + ".json");
    }
}
