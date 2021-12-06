package muramasa.antimatter.integration.kubejs;

import dev.latvian.kubejs.event.EventJS;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.object.WorldGenOreSmall;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import muramasa.antimatter.worldgen.object.WorldGenVeinLayer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class AMWorldEvent extends EventJS {
    public List<WorldGenStoneLayer> addStoneLayer(String stoneType, int weight, String world) {
        return WorldGenStoneLayer.add(AntimatterAPI.get(StoneType.class, stoneType), weight, ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(world)));
    }

    public WorldGenVeinLayer addOreLayer(String id, int minY, int maxY, int weight, int density, int size, String primary, String secondary, String between, String sporadic, String... dimensions) {
        List<String> list = Arrays.asList(dimensions);
        ResourceKey<Level>[] array = list.stream().map(s -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(s))).toArray(ResourceKey[]::new);
        return new WorldGenVeinLayer(id, minY, maxY, weight, density, size, Material.get(primary), Material.get(secondary), Material.get(between), Material.get(sporadic), array);
    }

    public WorldGenOreSmall addSmallOreLayer(String id, int minY, int maxY, int amount, String material, String... dims) {
        List<String> list = Arrays.asList(dims);
        ResourceKey<Level>[] array = list.stream().map(s -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(s))).toArray(ResourceKey[]::new);
        return new WorldGenOreSmall(id, minY, maxY, amount, Material.get(material), array);
    }

    public WorldGenOreSmall addSmallOreLayer(int minY, int maxY, int amount, String material, String... dims) {
        return addSmallOreLayer(material, minY, maxY, amount, material, dims);
    }
}
