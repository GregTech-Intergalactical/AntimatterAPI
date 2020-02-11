package muramasa.antimatter.worldgen.feature;

import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public abstract class AntimatterFeature<F extends IFeatureConfig> extends Feature<F> implements IAntimatterObject {

    Int2ObjectOpenHashMap<List<WorldGenBase<?>>> REGISTRY = new Int2ObjectOpenHashMap<>();

    public AntimatterFeature(Function<Dynamic<?>, ? extends F> configFactory, @Nullable Class<?> c) {
        super(configFactory);
        if (c != null) AntimatterAPI.register(AntimatterFeature.class, c.getName(), this);
    }

    public abstract boolean enabled();

    public void onDataOverride(JsonObject json) {
        getRegistry().values().forEach(list -> list.forEach(base -> base.onDataOverride(json)));
    }

    public abstract void init();

    public Int2ObjectOpenHashMap<List<WorldGenBase<?>>> getRegistry() {
        return REGISTRY;
    }
}
