package muramasa.antimatter.worldgen.vein;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.material.Material;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WorldGenVeinVariantBuilder {

    private final WorldGenVeinBuilder veinBuilder;
    @Nullable private Integer weight;
    // TODO: split chances into center and ring
    @Nullable private Float oreChance;
    @Nullable private Float smallOreChance;
    @Nullable private Float markerOreChance;
    @Nullable private Float surfaceStoneChance;
    private final ArrayList<WorldGenVeinVariantMaterial> materials;

    public WorldGenVeinVariantBuilder(WorldGenVeinBuilder veinBuilder) {
        this.veinBuilder = veinBuilder;
        this.materials = new ArrayList<>();
    }

    final public WorldGenVeinBuilder buildVariant() {
        if (this.weight == null) {
            throw new RuntimeException("weight is required");
        }
        if (this.materials.size() > 0 && this.oreChance == null && this.smallOreChance == null) {
            throw new RuntimeException("oreChance and/or smallOreChance is required when materials are specified");
        }
        if (this.materials.size() == 0 && this.oreChance != null && this.smallOreChance != null) {
            throw new RuntimeException("oreChance and smallOreChance is not allowed when no materials are specified");
        }

        List<WorldGenVeinVariant> veinVariants = WorldGenVeinVariant.getFlat(
                this.weight,
                this.oreChance != null ? this.oreChance : 0.0f,
                this.smallOreChance != null ? this.smallOreChance : 0.0f,
                this.markerOreChance != null ? this.markerOreChance : 0.0f,
                this.surfaceStoneChance != null ? this.surfaceStoneChance : 0.0f,
                this.materials
        );
        veinVariants.forEach(this.veinBuilder::addVeinVariant);
        return this.veinBuilder;
    }

    final public WorldGenVeinVariantBuilder withWeight(int weight) {
        this.weight = weight;
        return this;
    }

    final public WorldGenVeinVariantBuilder withChance(float oreChance, float smallOreChance, float markerOreChance, float surfaceStoneChance) {
        this.oreChance = oreChance;
        this.smallOreChance = smallOreChance;
        if (AntimatterConfig.WORLD.ORE_VEIN_SMALL_ORE_MARKERS) {
            this.markerOreChance = markerOreChance;
        }
        this.surfaceStoneChance = surfaceStoneChance;
        return this;
    }

    final public WorldGenVeinVariantBuilder withOreChance(float oreChance, float smallOreChance) {
        this.oreChance = oreChance;
        this.smallOreChance = smallOreChance;
        return this;
    }

    final public WorldGenVeinVariantBuilder withNormalChance(float oreChance) {
        this.oreChance = oreChance;
        return this;
    }

    final public WorldGenVeinVariantBuilder withSmallOreChance(float smallOreChance) {
        this.smallOreChance = smallOreChance;
        return this;
    }

    final public WorldGenVeinVariantBuilder withMarkerOreChance(float markerOreChance) {
        this.markerOreChance = markerOreChance;
        return this;
    }

    final public WorldGenVeinVariantBuilder withSurfaceStoneChance(float surfaceStoneChance) {
        this.surfaceStoneChance = surfaceStoneChance;
        return this;
    }

    final public WorldGenVeinVariantBuilder withThinChance() {
        return this.withChance((float) AntimatterConfig.WORLD.THIN_ORE_CHANCE, (float) AntimatterConfig.WORLD.THIN_SMALL_ORE_CHANCE, (float) AntimatterConfig.WORLD.THIN_MARKER_ORE_CHANCE, (float) AntimatterConfig.WORLD.THIN_SURFACE_STONE_CHANCE);
    }

    final public WorldGenVeinVariantBuilder withNormalChance() {
        return this.withChance((float) AntimatterConfig.WORLD.NORMAL_ORE_CHANCE, (float) AntimatterConfig.WORLD.NORMAL_SMALL_ORE_CHANCE, (float) AntimatterConfig.WORLD.NORMAL_MARKER_ORE_CHANCE, (float) AntimatterConfig.WORLD.NORMAL_SURFACE_STONE_CHANCE);
    }

    final public WorldGenVeinVariantBuilder withDenseChance() {
        return this.withChance((float) AntimatterConfig.WORLD.DENSE_ORE_CHANCE, (float) AntimatterConfig.WORLD.DENSE_SMALL_ORE_CHANCE, (float) AntimatterConfig.WORLD.DENSE_MARKER_ORE_CHANCE, (float) AntimatterConfig.WORLD.DENSE_SURFACE_STONE_CHANCE);
    }

    final public WorldGenVeinVariantMaterialBuilder withMaterial() {
        return new WorldGenVeinVariantMaterialBuilder(this);
    }

    final public WorldGenVeinVariantBuilder withMaterial(Material material, int weight, int minY, int maxY) {
        return this.withMaterial()
                .withMaterial(material)
                .withWeight(weight)
                .atHeight(minY, maxY)
                .buildMaterial();
    }

    final public WorldGenVeinVariantBuilder withMaterial(Material material, int weight) {
        return this.withMaterial(material, weight, Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    final public WorldGenVeinVariantBuilder withMaterial(Material material) {
        return this.withMaterial(material, 1, Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    final public WorldGenVeinVariantBuilder withMaterial(Material material, int minY, int maxY) {
        return this.withMaterial(material, 1, minY, maxY);
    }

    final void addVeinMaterial(WorldGenVeinVariantMaterial material) {
        this.materials.add(material);
    }

}
