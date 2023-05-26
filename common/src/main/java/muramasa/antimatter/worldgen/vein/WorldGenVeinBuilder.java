package muramasa.antimatter.worldgen.vein;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class WorldGenVeinBuilder {

  @Nullable
  private final String id;
  @Nullable
  private Integer layer;
  @Nullable
  private Integer weight;
  @Nullable
  private Integer minY;
  @Nullable
  private Integer maxY;
  @Nullable
  private Integer density;
  @Nullable
  private Integer minSize;
  @Nullable
  private Integer maxSize;
  @Nullable
  private Float heightScale;
  @Nullable
  private BlockState fill;
  private final ArrayList<WorldGenVeinVariant> variants;
  private final ArrayList<ResourceKey<Level>> dimensions;

  public WorldGenVeinBuilder(String id) {
    this.id = id;
    this.variants = new ArrayList<>();
    this.dimensions = new ArrayList<>();
  }

  public final List<WorldGenVein> buildVein() {
    if (this.id == null) {
      throw new RuntimeException("id is required");
    }
    if (this.layer == null) {
      throw new RuntimeException("layer is required");
    }
    if (this.weight == null) {
      throw new RuntimeException("weight is required");
    }
    if (this.minY == null || this.maxY == null) {
      throw new RuntimeException("minY and maxY are required");
    }
    if (this.density == null) {
      throw new RuntimeException("density is required");
    }
    if (this.minSize == null || this.maxSize == null) {
      throw new RuntimeException("minSize and maxSize are required");
    }
    if (this.dimensions.size() == 0) {
      throw new RuntimeException("at least 1 dimension is required");
    }

    return WorldGenVein.getFlat(this.buildVeinFromJson());
  }

  private WorldGenVein buildVeinFromJson(){
      WorldGenVein vein = new WorldGenVein(
              this.id,
              this.layer,
              this.weight,
              this.minY,
              this.maxY,
              this.density,
              this.minSize,
              this.maxSize,
              this.heightScale != null ? this.heightScale : 1.0f,
              this.fill,
              this.variants,
              this.dimensions);
      AntimatterWorldGenerator.writeJson(vein.toJson(), this.id, "veins");
      return AntimatterWorldGenerator.readJson(WorldGenVein.class, vein, WorldGenVein::fromJson, "veins");
  }
  public final WorldGenVeinBuilder onLayer(int layer) {
    this.layer = layer;
    return this;
  }

  public final WorldGenVeinBuilder withWeight(int weight) {
    this.weight = weight;
    return this;
  }

    public final WorldGenVeinBuilder withDensity(int density) {
        this.density = density;
        return this;
    }

  public final WorldGenVeinBuilder atHeight(int minY, int maxY) {
    this.minY = minY;
    this.maxY = maxY;
    return this;
  }

  public final WorldGenVeinBuilder withSize(int minSize, int maxSize, float heightScale) {
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.heightScale = heightScale;
    return this;
  }

  public final WorldGenVeinBuilder withSize(int minSize, int maxSize) {
    return this.withSize(minSize, maxSize, 1.0f);
  }

  public final WorldGenVeinBuilder withSize(int size) {
    return this.withSize(size, size, 1.0f);
  }

  public final WorldGenVeinBuilder withFill(BlockState fill) {
    this.fill = fill;
    return this;
  }

  public final WorldGenVeinBuilder withFill(StoneType stoneType) {
    return this.withFill(stoneType.getState());
  }

  public final WorldGenVeinBuilder withFill(Material material) {
    return this.withFill(AntimatterMaterialTypes.ORE_STONE.get().get(material).asState());
  }

  public final WorldGenVeinBuilder inDimension(ResourceKey<Level> dimension) {
    this.dimensions.add(dimension);
    return this;
  }

  public final WorldGenVeinVariantBuilder withVariant() {
    return new WorldGenVeinVariantBuilder(this);
  }

  public final WorldGenVeinVariantBuilder withVariant(int weight) {
    return this
        .withVariant()
        .withWeight(weight);
  }

  @SafeVarargs
  public final WorldGenVeinBuilder asOreVein(int minY, int maxY, int weight, int density, int size, Material primary,
      Material secondary, Material between, Material sporadic, ResourceKey<Level>... dimensions) {
    this.asVein(weight, minY, maxY, density, dimensions).withSize(size, size * 2, 0.75f)
        .withVariant(AntimatterConfig.WORLD.NORMAL_VEIN_WEIGHT)
        .withNormalChance()
        .withMaterial(primary, AntimatterConfig.WORLD.PRIMARY_MATERIAL_WEIGHT)
        .withMaterial(secondary, AntimatterConfig.WORLD.SECONDARY_MATERIAL_WEIGHT)
        .withMaterial(between, AntimatterConfig.WORLD.BETWEEN_MATERIAL_WEIGHT)
        .withMaterial(sporadic, AntimatterConfig.WORLD.SPORADIC_MATERIAL_WEIGHT)
        .buildVariant()
        .withVariant(AntimatterConfig.WORLD.THIN_VEIN_WEIGHT)
        .withThinChance()
        .withMaterial(primary, AntimatterConfig.WORLD.PRIMARY_MATERIAL_WEIGHT)
        .withMaterial(secondary, AntimatterConfig.WORLD.SECONDARY_MATERIAL_WEIGHT)
        .withMaterial(between, AntimatterConfig.WORLD.BETWEEN_MATERIAL_WEIGHT)
        .withMaterial(sporadic, AntimatterConfig.WORLD.SPORADIC_MATERIAL_WEIGHT)
        .buildVariant()
        .withVariant(AntimatterConfig.WORLD.DENSE_VEIN_WEIGHT)
        .withDenseChance()
        .withMaterial(primary, AntimatterConfig.WORLD.PRIMARY_MATERIAL_WEIGHT)
        .withMaterial(secondary, AntimatterConfig.WORLD.SECONDARY_MATERIAL_WEIGHT)
        .withMaterial(between, AntimatterConfig.WORLD.BETWEEN_MATERIAL_WEIGHT)
        .withMaterial(sporadic, AntimatterConfig.WORLD.SPORADIC_MATERIAL_WEIGHT)
        .buildVariant();
    return this;
  }

  @SafeVarargs
  public final WorldGenVeinBuilder asStoneVein(int weight, int minY, int maxY, StoneType stoneType,
      ResourceKey<Level>... dimensions) {
    for (ResourceKey<Level> dimension : dimensions) {
      this.inDimension(dimension);
    }
    return this
        .onLayer(WorldGenVein.STONE_VEIN_LAYER)
        .withWeight(weight)
        .atHeight(minY, maxY)
        .withDensity(1)
        .withFill(stoneType);
  }

  public final WorldGenVeinBuilder asStoneVein(int weight, int minY, int maxY, StoneType stoneType,
                                               List<ResourceKey<Level>> dimensions) {
    for (ResourceKey<Level> dimension : dimensions) {
      this.inDimension(dimension);
    }
    return this
            .onLayer(WorldGenVein.STONE_VEIN_LAYER)
            .withWeight(weight)
            .atHeight(minY, maxY)
            .withFill(stoneType);
  }

  @SafeVarargs
  public final WorldGenVeinBuilder asVein(int weight, int minY, int maxY, int density,
      ResourceKey<Level>... dimensions) {
    for (ResourceKey<Level> dimension : dimensions) {
      this.inDimension(dimension);
    }
    return this
        .onLayer(WorldGenVein.ORE_VEIN_LAYER)
        .withWeight(weight)
        .withDensity(density)
        .atHeight(minY, maxY);
  }

  @SafeVarargs
  public final WorldGenVeinBuilder asSmallStoneVein(int weight, int minY, int maxY, StoneType stoneType,
      ResourceKey<Level>... dimensions) {
    return this
        .asStoneVein(weight, minY, maxY, stoneType, dimensions)
        .withSize(16, 32, 1.0f);
  }

  @SafeVarargs
  public final WorldGenVeinBuilder asMediumStoneVein(int weight, int minY, int maxY, StoneType stoneType,
      ResourceKey<Level>... dimensions) {
    return this
        .asStoneVein(weight, minY, maxY, stoneType, dimensions)
        .withSize(32, 96, 0.5f);
  }

  @SafeVarargs
  public final WorldGenVeinBuilder asLargeStoneVein(int weight, int minY, int maxY, StoneType stoneType,
      ResourceKey<Level>... dimensions) {
    return this
        .asStoneVein(weight, minY, maxY, stoneType, dimensions)
        .withSize(48, 115, 0.25f);
  }

  @SafeVarargs
  public final WorldGenVeinBuilder asStoneOre(int weight, int minY, int maxY, Material material,
      ResourceKey<Level>... dimensions) {
    for (ResourceKey<Level> dimension : dimensions) {
      this.inDimension(dimension);
    }
    return this
        .onLayer(WorldGenVein.STONE_ORE_VEIN_LAYER)
        .withWeight(weight)
        .atHeight(minY, maxY)
        .withDensity(1)
        .withFill(material);
  }

  @SafeVarargs
  public final WorldGenVeinBuilder asMediumStoneOreVein(int weight, int minY, int maxY, Material material,
      ResourceKey<Level>... dimensions) {
    return this
        .asStoneOre(weight, minY, maxY, material, dimensions)
        .withSize(32, 64, 1.0f);
  }

  final void addVeinVariant(WorldGenVeinVariant variant) {
    this.variants.add(variant);
  }

}
