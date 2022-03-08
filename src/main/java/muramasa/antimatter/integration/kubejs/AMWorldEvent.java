package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.worldgen.vein.WorldGenVein;
import muramasa.antimatter.worldgen.vein.WorldGenVeinBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AMWorldEvent extends EventJS {

    public final List<WorldGenVein> VEINS = new ObjectArrayList<>();
    public boolean disableBuiltin = false;

    public final void vein(String id, int minY, int maxY, int weight, int density, int size, Material primary,
                              Material secondary, Material between, Material sporadic, String... dimensions) {
        if (dimensions == null || dimensions.length == 0) {
            dimensions = new String[]{"overworld"};
        }
        VEINS.addAll(new WorldGenVeinBuilder(id).asOreVein(minY, maxY, weight, density, size, primary, secondary, between, sporadic, Arrays.stream(dimensions).map(t -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(t))).toArray(ResourceKey[]::new)).buildVein());
    }

    public final void veinNamed(String id, int minY, int maxY, int weight, int density, int size, String p,
                                String s, String b, String sp, String... dimensions) {
        Material primary = Objects.requireNonNull(AntimatterAPI.get(Material.class, p), "Invalid primary material " + p);
        Material secondary = Objects.requireNonNull(AntimatterAPI.get(Material.class, s), "Invalid secondary material " + s);
        Material between = AntimatterAPI.get(Material.class, b);
        Material sporadic = AntimatterAPI.get(Material.class, sp);
        vein(id, minY, maxY, weight, density, size, primary, secondary, between, sporadic, dimensions);
    }

    public final void disableBuiltin() {
        this.disableBuiltin = true;
    }
}
