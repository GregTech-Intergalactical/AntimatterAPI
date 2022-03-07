package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.worldgen.vein.WorldGenVein;
import muramasa.antimatter.worldgen.vein.WorldGenVeinBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class AMWorldEvent extends EventJS {

    public final List<WorldGenVein> VEINS = new ObjectArrayList<>();

    public final void addVein(String id, int minY, int maxY, int weight, int density, int size, Material primary,
                              Material secondary, Material between, Material sporadic, String... dimensions) {
        VEINS.addAll(new WorldGenVeinBuilder(id).asOreVein(minY, maxY, weight, density, size, primary, secondary, between, sporadic, Arrays.stream(dimensions).map(t -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(t))).toArray(ResourceKey[]::new)).buildVein());
    }
}
