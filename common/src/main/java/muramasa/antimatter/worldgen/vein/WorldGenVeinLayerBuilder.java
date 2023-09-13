package muramasa.antimatter.worldgen.vein;

import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.vein.old.WorldGenVein;
import muramasa.antimatter.worldgen.vein.old.WorldGenVeinVariant;
import muramasa.antimatter.worldgen.vein.old.WorldGenVeinVariantBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldGenVeinLayerBuilder {

  @Nullable
  private final String id;
  @Nullable
  private Integer weight;
  @Nullable
  private Integer minY;
  @Nullable
  private Integer maxY;
  @Nullable
  private Integer density;
  @Nullable
  private Integer size;
  @Nullable
  private Material primary, secondary, between, sporadic;
  private final ArrayList<ResourceKey<Level>> dimensions;

  public WorldGenVeinLayerBuilder(String id) {
    this.id = id;
    this.dimensions = new ArrayList<>();
  }

  public final WorldGenVeinLayer buildVein() {
    if (this.id == null) {
      throw new RuntimeException("id is required");
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
    if (this.size == null) {
      throw new RuntimeException("size is required");
    }
    if (this.dimensions.size() == 0) {
      throw new RuntimeException("at least 1 dimension is required");
    }
    if (this.primary == null || this.secondary == null || this.between == null || this.sporadic == null){
        throw new RuntimeException("materials must not be null!");
    }

    return this.buildVeinFromJson();
  }

  private WorldGenVeinLayer buildVeinFromJson(){
      WorldGenVeinLayer vein = new WorldGenVeinLayer(
              this.id,
              this.minY,
              this.maxY,
              this.weight,
              this.density,
              this.size,
              this.primary,
              this.secondary,
              this.between,
              this.sporadic,
              this.dimensions);
      AntimatterWorldGenerator.writeJson(vein.toJson(), this.id, "vein_layers");
      return AntimatterWorldGenerator.readJson(WorldGenVeinLayer.class, vein, WorldGenVeinLayer::fromJson, "vein_layers");
  }
    public final WorldGenVeinLayerBuilder withWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public final WorldGenVeinLayerBuilder withDensity(int density) {
        this.density = density;
        return this;
    }

    public final WorldGenVeinLayerBuilder atHeight(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
        return this;
    }

    public final WorldGenVeinLayerBuilder withSize(int size) {
        this.size = size;
        return this;
    }

    public final WorldGenVeinLayerBuilder withMaterials(Material... materials) {
        this.primary = materials.length > 0 ? materials[0] : null;
        this.secondary = materials.length > 1 ? materials[1] : primary;
        this.between = materials.length > 2 ? materials[2] : secondary;
        this.sporadic = materials.length > 3 ? materials[3] : between;
        return this;
    }

    public final WorldGenVeinLayerBuilder inDimension(ResourceKey<Level> dimension) {
        this.dimensions.add(dimension);
        return this;
    }

    @SafeVarargs
    public final WorldGenVeinLayerBuilder inDimensions(ResourceKey<Level>... dimension) {
        this.dimensions.addAll(Arrays.asList(dimension));
        return this;
    }

    @SafeVarargs
    public final WorldGenVeinLayerBuilder asOreVein(int minY, int maxY, int weight, int density, int size, Material primary,
                                                    Material secondary, Material between, Material sporadic, ResourceKey<Level>... dimensions) {
        return this.atHeight(minY, maxY).withWeight(weight).withDensity(density).withSize(size).withMaterials(primary, secondary, between, sporadic).inDimensions(dimensions);
    }

}
