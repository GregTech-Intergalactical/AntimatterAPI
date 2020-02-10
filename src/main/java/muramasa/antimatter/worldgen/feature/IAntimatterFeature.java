package muramasa.antimatter.worldgen.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.worldgen.object.WorldGenBase;

import java.util.List;

public interface IAntimatterFeature extends IAntimatterObject {

    Int2ObjectOpenHashMap<List<WorldGenBase<?>>> REGISTRY = new Int2ObjectOpenHashMap<>();

    void init();

    boolean enabled();

    default void register(Class<?> c, IAntimatterFeature feature) {
        AntimatterAPI.register(IAntimatterFeature.class, c.getName(), feature);
    }

    default Int2ObjectOpenHashMap<List<WorldGenBase<?>>> getRegistry() {
        return REGISTRY;
    }
}
