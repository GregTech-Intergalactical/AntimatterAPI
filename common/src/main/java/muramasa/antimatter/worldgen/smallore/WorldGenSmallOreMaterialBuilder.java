package muramasa.antimatter.worldgen.smallore;

import muramasa.antimatter.material.Material;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenSmallOreMaterialBuilder {
    @Nullable
    private Material material;
    @Nullable
    private Integer weight;
    @Nullable
    private Integer maxY;
    @Nullable
    private Integer minY;
    List<ResourceLocation> dimensions = new ArrayList<>(), biomes = new ArrayList<>();
    boolean dimensionBlacklist = false, biomeBlacklist = true;

    public WorldGenSmallOreMaterialBuilder() {
    }

    final public List<WorldGenSmallOreMaterial> buildMaterial() {
        if (this.weight == null) {
            throw new RuntimeException("weight is required");
        }
        if (this.material == null) {
            throw new RuntimeException("material is required");
        }
        if (this.dimensions.isEmpty()) {
            this.dimensions.add(new ResourceLocation("overworld"));
        }
        WorldGenSmallOreMaterial smallOreMaterial = new WorldGenSmallOreMaterial(
                this.material,
                this.minY != null ? this.minY : Integer.MIN_VALUE,
                this.maxY != null ? this.maxY : Integer.MAX_VALUE,
                weight,
                this.dimensions,
                this.biomes,
                this.biomeBlacklist
        );
        return List.of(smallOreMaterial);
    }

    final public WorldGenSmallOreMaterialBuilder withMaterial(Material material) {
        this.material = material;
        return this;
    }

    final public WorldGenSmallOreMaterialBuilder withWeight(int weight) {
        this.weight = weight;
        return this;
    }

    final public WorldGenSmallOreMaterialBuilder atHeight(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
        return this;
    }

    final public WorldGenSmallOreMaterialBuilder withBiomes(ResourceLocation... biomes) {
        Collections.addAll(this.biomes, biomes);
        return this;
    }

    final public WorldGenSmallOreMaterialBuilder withDimensions(ResourceLocation... dimensions) {
        Collections.addAll(this.dimensions, dimensions);
        return this;
    }

    final public WorldGenSmallOreMaterialBuilder setBiomeBlacklist(boolean blacklist) {
        this.biomeBlacklist = blacklist;
        return this;
    }
}
