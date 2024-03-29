package muramasa.antimatter.worldgen.vanillaore;

import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTypeBlock;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldGenVanillaOreBuilder {
    @Nullable
    private Material material;
    @Nullable
    private Material secondary;
    @Nullable
    private MaterialTypeBlock<?> materialType;
    @Nullable
    private MaterialTypeBlock<?> secondaryType;
    @Nullable
    private Integer weight;
    @Nullable
    private Integer probability;
    @Nullable
    private Integer maxY;
    @Nullable
    private Integer minY;
    @Nullable
    private Integer size;
    @Nullable
    private Integer plateau;
    @Nullable
    private Float secondaryChance;
    @Nullable
    private Float discardOnExposureChance;
    @Nullable String id;
    List<ResourceLocation> dimensions = new ArrayList<>();
    List<String> biomes = new ArrayList<>();
    boolean biomeBlacklist = true, triangle = false, spawnOnOceanFloor = false;

    public WorldGenVanillaOreBuilder() {
    }

    final public WorldGenVanillaOre buildMaterial() {
        if (this.size == null) {
            throw new RuntimeException("size is required");
        }
        if (this.material == null) {
            throw new RuntimeException("material is required");
        }
        if (this.dimensions.isEmpty()) {
            this.dimensions.add(new ResourceLocation("overworld"));
        }
        MaterialTypeBlock<?> materialTypeBlock = this.materialType == null ? AntimatterMaterialTypes.ORE : materialType;
        WorldGenVanillaOre vanillaOre =  new WorldGenVanillaOre(
                id != null ? id : material.getId(),
                this.material,
                this.secondary == null ? Material.NULL : this.secondary,
                materialTypeBlock,
                this.secondaryType == null ? materialTypeBlock : this.secondaryType,
                this.secondaryChance == null ? 0.0f : this.secondaryChance,
                this.discardOnExposureChance == null ? 0.0f : this.discardOnExposureChance,
                this.minY != null ? this.minY : Integer.MIN_VALUE,
                this.maxY != null ? this.maxY : Integer.MAX_VALUE,
                weight == null ? 1 : weight,
                size,
                probability == null ? 1 : probability,
                triangle,
                this.plateau == null ? 0 : this.plateau,
                this.spawnOnOceanFloor,
                this.dimensions,
                this.biomes,
                this.biomeBlacklist
        );
        AntimatterWorldGenerator.writeJson(vanillaOre.toJson(), vanillaOre.getId(), "vanilla_ore");
        return AntimatterWorldGenerator.readJson(WorldGenVanillaOre.class, vanillaOre, WorldGenVanillaOre::fromJson, "vanilla_ore");
    }

    final public WorldGenVanillaOreBuilder withMaterial(Material material) {
        this.material = material;
        return this;
    }

    final public WorldGenVanillaOreBuilder withSecondaryMaterial(Material secondary, float secondaryChance){
        this.secondary = secondary;
        this.secondaryChance = secondaryChance;
        return this;
    }

    final public WorldGenVanillaOreBuilder withSecondaryType(MaterialTypeBlock<?> materialType){
        this.secondaryType = materialType;
        return this;
    }

    final public WorldGenVanillaOreBuilder withMaterialType(MaterialTypeBlock<?> materialType){
        this.materialType = materialType;
        return this;
    }

    final public WorldGenVanillaOreBuilder withWeight(int weight) {
        this.weight = weight;
        return this;
    }

    final public WorldGenVanillaOreBuilder withSize(int size){
        this.size = size;
        return this;
    }

    final public WorldGenVanillaOreBuilder withDiscardOnExposureChance(float discardOnExposureChance){
        this.discardOnExposureChance = discardOnExposureChance;
        return this;
    }

    final public WorldGenVanillaOreBuilder atHeight(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
        return this;
    }

    final public WorldGenVanillaOreBuilder withProbability(int probability){
        this.probability = probability;
        return this;
    }

    final public WorldGenVanillaOreBuilder withCustomId(String id){
        this.id = id;
        return this;
    }

    final public WorldGenVanillaOreBuilder withBiomes(String... biomes) {
        Collections.addAll(this.biomes, biomes);
        return this;
    }

    final public WorldGenVanillaOreBuilder withDimensions(ResourceLocation... dimensions) {
        Collections.addAll(this.dimensions, dimensions);
        return this;
    }

    final public WorldGenVanillaOreBuilder setBiomeBlacklist(boolean blacklist) {
        this.biomeBlacklist = blacklist;
        return this;
    }

    final public WorldGenVanillaOreBuilder setHasTriangleHeight(boolean triangle){
        this.triangle = triangle;
        return this;
    }

    final public WorldGenVanillaOreBuilder setHasTriangleHeight(boolean triangle, int plateau){
        this.triangle = triangle;
        this.plateau = plateau;
        return this;
    }

    final public WorldGenVanillaOreBuilder setSpawnOnOceanFloor(boolean spawnOnOceanFloor){
        this.spawnOnOceanFloor = spawnOnOceanFloor;
        return this;
    }
}
