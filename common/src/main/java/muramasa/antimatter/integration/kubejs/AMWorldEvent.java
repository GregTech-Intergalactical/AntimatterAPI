package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.StoneLayerOre;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayerBuilder;
import muramasa.antimatter.worldgen.vein.WorldGenVeinLayer;
import muramasa.antimatter.worldgen.vein.WorldGenVeinLayerBuilder;
import muramasa.antimatter.worldgen.vein.old.WorldGenVein;
import muramasa.antimatter.worldgen.vein.old.WorldGenVeinBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AMWorldEvent extends EventJS {

    public final List<WorldGenVeinLayer> VEINS = new ObjectArrayList<>();
    public final List<WorldGenStoneLayer> STONE_LAYERS = new ObjectArrayList<>();

    public final Int2ObjectOpenHashMap<List<StoneLayerOre>> COLLISION_MAP = new Int2ObjectOpenHashMap<>();
    public boolean disableBuiltin = false;

    public final void vein(String id, int minY, int maxY, int weight, int density, int size, Material primary,
                              Material secondary, Material between, Material sporadic, String... dimensions) {
        if (dimensions == null || dimensions.length == 0) {
            dimensions = new String[]{"overworld"};
        }
        VEINS.add(new WorldGenVeinLayerBuilder(id).asOreVein(minY, maxY, weight, density, size, primary, secondary, between, sporadic, Arrays.stream(dimensions).map(t -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(t))).toArray(ResourceKey[]::new)).buildVein());
    }

    /*public final void smallStoneLayer(String id, int weight, int minHeight, int maxHeight, String stoneType, String... dimensionKeys){
        stoneLayer(id, weight, minHeight, maxHeight, 16, 32, 1.0f, stoneType, dimensionKeys);
    }

    public final void mediumStoneLayer(String id, int weight, int minHeight, int maxHeight, String stoneType, String... dimensionKeys){
        stoneLayer(id, weight, minHeight, maxHeight, 32, 96, 0.5f, stoneType, dimensionKeys);
    }

    public final void largeStoneLayer(String id, int weight, int minHeight, int maxHeight, String stoneType, String... dimensionKeys){
        stoneLayer(id, weight, minHeight, maxHeight, 48, 115, 0.25f, stoneType, dimensionKeys);
    }*/



    public final void stoneLayer(String id, String stoneType, int weight, int minHeight, int maxHeight, String... dimensionKeys){
        if (dimensionKeys == null || dimensionKeys.length == 0) {
            dimensionKeys = new String[]{"overworld"};
        }
        List<ResourceKey<Level>> dimension = Arrays.stream(dimensionKeys).map((dimensionKey) -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimensionKey))).toList();
        StoneType type = Objects.requireNonNull(AntimatterAPI.get(StoneType.class, stoneType));

        STONE_LAYERS.addAll(new WorldGenStoneLayerBuilder(id).withStone(type).withWeight(weight).atHeight(minHeight, maxHeight).inDimensions(dimension).buildVein());
        /*VEINS.addAll(new WorldGenVeinBuilder(id)
                .asStoneVein(weight, minHeight, maxHeight, type, dimension)
                .withSize(minSize,  maxSize, heightScale)
                .buildVein());*/
    }

    public final void disableBuiltin() {
        this.disableBuiltin = true;
    }

    public void addCollision(BlockState top, BlockState bottom, StoneLayerOre... oresToAdd) {
        COLLISION_MAP.computeIfAbsent(Objects.hash(top, bottom), k -> new ObjectArrayList<>()).addAll(Arrays.asList(oresToAdd));
    }
}
