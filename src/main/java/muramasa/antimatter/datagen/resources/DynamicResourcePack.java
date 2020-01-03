package muramasa.antimatter.datagen.resources;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.IGeneratedBlockstate;
import net.minecraftforge.client.model.generators.ModelBuilder;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Predicate;

public class DynamicResourcePack extends ResourcePack {

    protected Set<String> namespaces = new HashSet<>();
    protected Map<String, InputStream> registry = new HashMap<>();
    protected Map<String, JsonObject> lang = new HashMap<>();
    protected DynamicPackFinder packFinder;

    public DynamicResourcePack(DynamicPackFinder packFinder) {
        super(new File(packFinder.packId));
        this.packFinder = packFinder;
    }

    protected InputStream buildPackMeta() {
        JsonObject pack = new JsonObject(), data = new JsonObject();
        data.addProperty("description", packFinder.packId);
        data.addProperty("pack_format", 4);
        pack.add("pack", data);
        return new ReaderInputStream(new StringReader(pack.toString()), Charset.defaultCharset());
    }

    public void addState(String namespace, String id, IGeneratedBlockstate state) {
        namespaces.add(namespace);
        registry.put("assets/" + namespace + "/blockstates/" + id + ".json", new ReaderInputStream(new StringReader(state.toJson().toString()), Charset.defaultCharset()));
    }

    public void addModel(String namespace, String type, String id, ModelBuilder builder) {
        namespaces.add(namespace);
        registry.put("assets/" + namespace + "/models/" + type + "/" + id + ".json", new ReaderInputStream(new StringReader(builder.toJson().toString()), Charset.defaultCharset()));
    }

    public void addLang(String namespace, String region, String key, String value) {
        String mapKey = "assets/" + namespace + "/lang" + region + ".json";
        lang.computeIfAbsent(mapKey, k -> new JsonObject());
        lang.get(mapKey).addProperty(key, value);
    }

    @Override
    protected InputStream getInputStream(String resourcePath) throws IOException {
        if (resourcePath.equals("pack.mcmeta")) return buildPackMeta();
        InputStream stream = registry.get(resourcePath);
        if (stream == null) throw new ResourcePackFileNotFoundException(file, resourcePath);
        return stream;
    }

    @Override
    protected boolean resourceExists(String resourcePath) {
        if (resourcePath.contains("lang")) {
            System.out.println("x");
        }
        return registry.containsKey(resourcePath);
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter) {
        Set<ResourceLocation> locs = new HashSet<>();
        String path = type.getDirectoryName() + "/";

        return locs;
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type) {
        return namespaces;
    }

    @Override
    public String getName() {
        return packFinder.packId;
    }

    @Override
    public void close() throws IOException {
        //NOOP
    }
}
