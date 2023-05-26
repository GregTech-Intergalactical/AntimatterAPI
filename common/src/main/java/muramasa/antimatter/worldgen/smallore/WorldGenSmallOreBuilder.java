package muramasa.antimatter.worldgen.smallore;

import muramasa.antimatter.material.Material;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldGenSmallOreBuilder {
    @Nullable
    private Material material;
    @Nullable
    private Integer amountPerChunk;
    @Nullable
    private Integer maxY;
    @Nullable
    private Integer minY;
    @Nullable String id;
    List<ResourceLocation> dimensions = new ArrayList<>();
    List<String> biomes = new ArrayList<>();
    boolean dimensionBlacklist = false, biomeBlacklist = true;

    public WorldGenSmallOreBuilder() {
    }

    final public WorldGenSmallOre buildMaterial() {
        if (this.amountPerChunk == null) {
            throw new RuntimeException("weight is required");
        }
        if (this.material == null) {
            throw new RuntimeException("material is required");
        }
        if (this.dimensions.isEmpty()) {
            this.dimensions.add(new ResourceLocation("overworld"));
        }
        return new WorldGenSmallOre(
                id != null ? id : material.getId(),
                this.material,
                this.minY != null ? this.minY : -64,
                this.maxY != null ? this.maxY : 320,
                amountPerChunk,
                this.dimensions,
                this.biomes,
                this.biomeBlacklist
        );
    }

    final public WorldGenSmallOreBuilder withMaterial(Material material) {
        this.material = material;
        return this;
    }

    final public WorldGenSmallOreBuilder withAmountPerChunk(int amountPerChunk) {
        this.amountPerChunk = amountPerChunk;
        return this;
    }

    final public WorldGenSmallOreBuilder atHeight(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
        return this;
    }

    final public WorldGenSmallOreBuilder withCustomId(String id){
        this.id = id;
        return this;
    }

    final public WorldGenSmallOreBuilder withBiomes(String... biomes) {
        Collections.addAll(this.biomes, biomes);
        return this;
    }

    final public WorldGenSmallOreBuilder withDimensions(ResourceLocation... dimensions) {
        Collections.addAll(this.dimensions, dimensions);
        return this;
    }

    final public WorldGenSmallOreBuilder setBiomeBlacklist(boolean blacklist) {
        this.biomeBlacklist = blacklist;
        return this;
    }
}
