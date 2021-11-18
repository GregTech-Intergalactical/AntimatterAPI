package muramasa.antimatter.integration.kubejs;

import dev.latvian.kubejs.event.EventJS;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.object.WorldGenOreSmall;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import muramasa.antimatter.worldgen.object.WorldGenVeinLayer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class AMWorldEvent extends EventJS {
    public List<WorldGenStoneLayer> addStoneLayer(String stoneType, int weight, String world) {
        return WorldGenStoneLayer.add(AntimatterAPI.get(StoneType.class, stoneType), weight, RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(world)));
    }

    public WorldGenVeinLayer addOreLayer(String id, int minY, int maxY, int weight, int density, int size, String primary, String secondary, String between, String sporadic, String... dimensions) {
        List<String> list = Arrays.asList(dimensions);
        RegistryKey<World>[] array = list.stream().map(s -> RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(s))).toArray(RegistryKey[]::new);
        return new WorldGenVeinLayer(id, minY, maxY, weight, density, size, Material.get(primary), Material.get(secondary), Material.get(between), Material.get(sporadic), array);
    }

    public WorldGenOreSmall addSmallOreLayer(String id, int minY, int maxY, int amount, String material, String... dims) {
        List<String> list = Arrays.asList(dims);
        RegistryKey<World>[] array = list.stream().map(s -> RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(s))).toArray(RegistryKey[]::new);
        return new WorldGenOreSmall(id, minY, maxY, amount, Material.get(material), array);
    }

    public WorldGenOreSmall addSmallOreLayer(int minY, int maxY, int amount, String material, String... dims) {
        return addSmallOreLayer(material, minY, maxY, amount, material, dims);
    }
}
