package muramasa.antimatter.worldgen.feature;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;

import java.util.List;

public abstract class AntimatterFeature<F extends IFeatureConfig> extends Feature<F> implements ISharedAntimatterObject {

    Object2ObjectMap<ResourceLocation,List<WorldGenBase<?>>> REGISTRY = new Object2ObjectOpenHashMap<>();

    public AntimatterFeature(Codec<F> codec, Class<?> c) {
        super(codec);
        AntimatterAPI.register(AntimatterFeature.class, c.getName(), getDomain(), this);
        this.setRegistryName(new ResourceLocation(getDomain(),getId()));
    }


    public abstract boolean enabled();

    public void onDataOverride(JsonObject json) {
        getRegistry().values().forEach(list -> list.forEach(base -> base.onDataOverride(json)));
    }

    public abstract void init();

    public Object2ObjectMap<ResourceLocation,List<WorldGenBase<?>>> getRegistry() {
        return REGISTRY;
    }

    public abstract void build(BiomeGenerationSettingsBuilder event);
}
