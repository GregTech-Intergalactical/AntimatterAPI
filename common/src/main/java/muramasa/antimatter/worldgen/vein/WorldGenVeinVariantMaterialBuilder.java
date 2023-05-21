package muramasa.antimatter.worldgen.vein;

import muramasa.antimatter.material.Material;

import javax.annotation.Nullable;
import java.util.List;

public class WorldGenVeinVariantMaterialBuilder {

    private final WorldGenVeinVariantBuilder veinVariantBuilder;
    @Nullable private Material material;
    @Nullable private Integer weight;
    @Nullable private Integer maxY;
    @Nullable private Integer minY;

    public WorldGenVeinVariantMaterialBuilder(WorldGenVeinVariantBuilder veinVariantBuilder) {
        this.veinVariantBuilder = veinVariantBuilder;
    }

    final public WorldGenVeinVariantBuilder buildMaterial() {
        if (this.weight == null) {
            throw new RuntimeException("weight is required");
        }
        if (this.material == null) {
            throw new RuntimeException("material is required");
        }
        this.veinVariantBuilder.addVeinMaterial(new WorldGenVeinVariantMaterial(
                this.weight,
                this.material,
                this.maxY != null ? this.maxY : Integer.MAX_VALUE,
                this.minY != null ? this.minY : Integer.MIN_VALUE
        ));
        return this.veinVariantBuilder;
    }

    final public WorldGenVeinVariantMaterialBuilder withMaterial(Material material) {
        this.material = material;
        return this;
    }

    final public WorldGenVeinVariantMaterialBuilder withWeight(int weight) {
        this.weight = weight;
        return this;
    }

    final public WorldGenVeinVariantMaterialBuilder atHeight(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
        return this;
    }

}
