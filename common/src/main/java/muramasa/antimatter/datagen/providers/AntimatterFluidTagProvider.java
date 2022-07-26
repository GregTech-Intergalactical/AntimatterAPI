package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.AntimatterRuntimeResourceGeneration;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.fluid.AntimatterMaterialFluid;
import muramasa.antimatter.material.Material;
import net.devtech.arrp.json.tags.JTag;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static muramasa.antimatter.util.TagUtils.getForgelikeFluidTag;

public class AntimatterFluidTagProvider extends AntimatterTagProvider<Fluid> implements IAntimatterProvider {

    private final boolean replace;

    public AntimatterFluidTagProvider(String providerDomain, String providerName, boolean replace) {
        super(Registry.FLUID, providerDomain, providerName, "fluids");
        this.replace = replace;
    }

    protected void processTags(String domain) {
        AntimatterAPI.all(AntimatterFluid.class, domain).forEach(f -> {
            tag(getForgelikeFluidTag(f.getId()))
                    .add(f.getFluid(), f.getFlowingFluid())
                    .replace(replace);
            if (f instanceof AntimatterMaterialFluid) {
                Material m = ((AntimatterMaterialFluid) f).getMaterial();
                tag(getForgelikeFluidTag(m.getId()))
                        .add(f.getFluid(), f.getFlowingFluid())
                        .replace(replace);
            }
        });
    }
}
