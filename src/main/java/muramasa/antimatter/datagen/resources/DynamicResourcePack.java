package muramasa.antimatter.datagen.resources;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import mcp.MethodsReturnNonnullByDefault;
import muramasa.antimatter.Ref;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.tags.ITag;
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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DynamicResourcePack implements IResourcePack {

    //To ensure that the resource pack is not duplicated when running e.g. singleplayer.

    protected static final ObjectSet<String> CLIENT_DOMAINS = new ObjectOpenHashSet<>();
    protected static final ObjectSet<String> SERVER_DOMAINS = new ObjectOpenHashSet<>();
    protected static final Map<ResourceLocation, String> ASSETS = new HashMap<>();
    protected static final Map<ResourceLocation, JsonObject> LANG = new HashMap<>();
    protected static final Map<ResourceLocation, JsonObject> DATA = new HashMap<>();

    private String name = null;

    static {
        CLIENT_DOMAINS.addAll(Sets.newHashSet(Ref.ID, Ref.SHARED_ID));
        SERVER_DOMAINS.addAll(Sets.newHashSet(Ref.ID, Ref.SHARED_ID, "minecraft", "forge"));
    }

    public DynamicResourcePack(String name, Collection<String> domains) {
        this.name = name;
        CLIENT_DOMAINS.addAll(domains);
        SERVER_DOMAINS.addAll(domains);
    }

    public static void clearServer() {
        DATA.clear();
    }

    public static void clearClient() {
        ASSETS.clear();
        LANG.clear();
    }

    public static void addState(ResourceLocation loc, IGeneratedBlockstate state) {
        synchronized (ASSETS) {
            ASSETS.put(getStateLoc(loc), state.toJson().toString());
        }
    }

    public static void addBlock(ResourceLocation loc, ModelBuilder<?> builder) {
        synchronized (ASSETS) {
            ASSETS.put(getModelLoc(loc), builder.toJson().toString());
        }
    }

    public static void addItem(ResourceLocation loc, ModelBuilder<?> builder) {
        synchronized (ASSETS) {
            ASSETS.put(getModelLoc(loc), builder.toJson().toString());
        }
    }

    public static void addLangLoc(String domain, String locale, String key, String value) {
        synchronized (LANG) {
            LANG.computeIfAbsent(getLangLoc(domain, locale), j -> new JsonObject()).addProperty(key, value);
        }
    }

    public static void addRecipe(IFinishedRecipe recipe) {
        DATA.put(getRecipeLog(recipe.getId()), recipe.serializeRecipe());
        if (recipe.serializeAdvancement() != null)
            DATA.put(getAdvancementLoc(Objects.requireNonNull(recipe.getAdvancementId())), recipe.serializeAdvancement());
    }

    public static void addLootEntry(ResourceLocation loc, LootTable table) {
        JsonObject obj = (JsonObject) LootTableManager.serialize(table);
        synchronized (DATA) {
            DATA.put(getLootLoc(loc), obj);
        }
    }

    public static void addAdvancement(ResourceLocation loc, JsonObject obj) {
        synchronized (DATA) {
            DATA.put(getAdvancementLoc(loc), obj);
        }
    }


    public static void addTag(String type, ResourceLocation loc, JsonObject obj) {
        synchronized (DATA) {
            JsonObject object = DATA.putIfAbsent(getTagLoc(type, loc), obj);
            if (object != null) {
                object.getAsJsonArray("values").addAll(obj.getAsJsonArray("values"));
            }
        }
    }

    public static void ensureTagAvailable(String id, ResourceLocation loc) {
        if (loc.getNamespace().contains("minecraft")) return;
        synchronized (DATA) {
            DATA.putIfAbsent(getTagLoc(id, loc), ITag.Builder.tag().serializeToJson());
        }
    }

    @Override
    public InputStream getResource(ResourcePackType type, ResourceLocation location) throws IOException {
        if (type == ResourcePackType.SERVER_DATA) {
            if (DATA.containsKey(location))
                return new ByteArrayInputStream(DATA.get(location).toString().getBytes(StandardCharsets.UTF_8));
            else throw new FileNotFoundException("Can't find " + location + " " + getName());
        } else {
            if (LANG.containsKey(location))
                return new ByteArrayInputStream(LANG.get(location).toString().getBytes(StandardCharsets.UTF_8));
            else if (ASSETS.containsKey(location))
                return new ByteArrayInputStream(ASSETS.get(location).getBytes(StandardCharsets.UTF_8));
            else throw new FileNotFoundException("Can't find " + location + " " + getName());
        }
    }

    @Override
    public InputStream getRootResource(String fileName) {
        throw new UnsupportedOperationException("Dynamic Resource Pack cannot have root resources");
    }

    @Override
    public boolean hasResource(ResourcePackType type, ResourceLocation location) {
        if (type == ResourcePackType.CLIENT_RESOURCES) {
            return ASSETS.containsKey(location) || LANG.containsKey(location);
        } else {
            return DATA.containsKey(location);
        }
    }

    @Override
    public Collection<ResourceLocation> getResources(ResourcePackType type, String namespace, String path, int maxDepth, Predicate<String> filter) {
        if (type == ResourcePackType.SERVER_DATA)
            return DATA.keySet().stream().filter(loc -> loc.getPath().startsWith(path) && filter.test(loc.getPath())).collect(Collectors.toList());
        else if (type == ResourcePackType.CLIENT_RESOURCES) {
            Stream<ResourceLocation> obj = LANG.keySet().stream().filter(loc -> loc.getPath().startsWith(path) && filter.test(loc.getPath()));
            Stream<ResourceLocation> obj2 = ASSETS.keySet().stream().filter(loc -> loc.getPath().startsWith(path) && filter.test(loc.getPath()));
            return Stream.concat(obj, obj2).collect(Collectors.toList());
        }
        return Collections.emptyList();//LANG.keySet().stream().filter(loc -> loc.getPath().startsWith(path) && filter.test(loc.getPath())).collect(Collectors.toList());
    }

    @Override
    public Set<String> getNamespaces(ResourcePackType type) {
        return type == ResourcePackType.SERVER_DATA ? SERVER_DOMAINS : CLIENT_DOMAINS;
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(IMetadataSectionSerializer<T> deserializer) {
        return null;
    }

    @Override
    public void close() {
        //NOOP
    }

    public static ResourceLocation getLootLoc(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), "loot_tables/blocks/" + id.getPath() + ".json");
    }

    public static ResourceLocation getStateLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "blockstates/", registryId.getPath(), ".json"));
    }

    public static ResourceLocation getModelLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "models/", registryId.getPath(), ".json"));
    }

    public static ResourceLocation getLangLoc(String domain, String locale) {
        return new ResourceLocation(domain, String.join("", "lang/", locale, ".json"));
    }

    public static ResourceLocation getRecipeLog(ResourceLocation recipeId) {
        return new ResourceLocation(recipeId.getNamespace(), String.join("", "recipes/", recipeId.getPath(), ".json"));
    }

    public static ResourceLocation getAdvancementLoc(ResourceLocation advancementId) {
        return new ResourceLocation(advancementId.getNamespace(), String.join("", "advancements/", advancementId.getPath(), ".json"));
    }

    public static ResourceLocation getTagLoc(String identifier, ResourceLocation tagId) {
        return new ResourceLocation(tagId.getNamespace(), String.join("", "tags/", identifier, "/", tagId.getPath(), ".json"));
    }
}
