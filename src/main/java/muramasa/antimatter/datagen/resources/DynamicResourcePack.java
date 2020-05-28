package muramasa.antimatter.datagen.resources;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.IGeneratedBlockstate;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

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

    protected static final ObjectOpenHashSet<String> DOMAINS = new ObjectOpenHashSet<>();
    protected static final Object2ObjectOpenHashMap<ResourceLocation, String> REGISTRY = new Object2ObjectOpenHashMap<>();
    protected static final Object2ObjectOpenHashMap<ResourceLocation, JsonObject> OBJS = new Object2ObjectOpenHashMap<>();

    private final String name;

    public DynamicResourcePack(String name) {
        this.name = name;
    }

    public static void addState(ResourceLocation loc, IGeneratedBlockstate state) {
        REGISTRY.put(getStateLoc(loc), state.toJson().toString());
    }

    public static void addBlock(ResourceLocation loc, ModelBuilder<?> builder) {
        REGISTRY.put(getBlockLoc(loc), builder.toJson().toString());
    }

    public static void addItem(ResourceLocation loc, ModelBuilder<?> builder) {
        REGISTRY.put(getItemLoc(loc), builder.toJson().toString());
    }

    public static void addLoc(String domain, String locale, String key, String value) {
        OBJS.computeIfAbsent(getLangLoc(domain, locale), k -> new JsonObject()).addProperty(key, value);
    }

    public static void addBlockTag(ResourceLocation loc, JsonObject obj) {
        OBJS.put(getBlockTagLoc(loc), obj);
    }

    public static void addItemTag(ResourceLocation loc, JsonObject obj) {
        OBJS.put(getItemTagLoc(loc), obj);
    }

    @Override
    public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
        if (type == ResourcePackType.SERVER_DATA) {
            // throw new UnsupportedOperationException("Dynamic Resource Pack only supports client resources");
            JsonObject obj = OBJS.get(location);
            if (obj != null) {
                return new ByteArrayInputStream(obj.toString().getBytes(StandardCharsets.UTF_8));
            }
            else throw new FileNotFoundException("Can't find " + location + " " + getName());
        }
        String str = REGISTRY.get(location);
        if (str == null) throw new FileNotFoundException("Can't find " + location + " " + getName());
        JsonObject obj = OBJS.get(location);
        if (obj != null) {
            return new ByteArrayInputStream(obj.toString().getBytes(StandardCharsets.UTF_8));
        }
        else {
            return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public InputStream getRootResourceStream(String fileName) {
        throw new UnsupportedOperationException("Dynamic Resource Pack cannot have root resources");
    }

    @Override
    public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
        if (type == ResourcePackType.SERVER_DATA) return OBJS.containsKey(location);
        if (!REGISTRY.containsKey(location)) return OBJS.containsKey(location);
        return true;
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespace, String path, int maxDepth, Predicate<String> filter) {
        if (type == ResourcePackType.SERVER_DATA) return OBJS.keySet().stream().filter(loc -> loc.getPath().startsWith(path) && filter.test(loc.getPath())).collect(Collectors.toList());
        return REGISTRY.keySet().stream().filter(loc -> loc.getPath().startsWith(path) && filter.test(loc.getPath())).collect(Collectors.toList());
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type) {
        if (type == ResourcePackType.SERVER_DATA) {
            final ObjectOpenHashSet<String> SERVER_DOMAINS = DOMAINS;
            SERVER_DOMAINS.add("forge");
            SERVER_DOMAINS.add("minecraft");
            return SERVER_DOMAINS;
        }
        return DOMAINS;
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

    public static ResourceLocation getBlockTagLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "tags/blocks/", registryId.getPath(), ".json"));
    }

    public static ResourceLocation getItemTagLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "tags/items/", registryId.getPath(), ".json"));
    }

}
