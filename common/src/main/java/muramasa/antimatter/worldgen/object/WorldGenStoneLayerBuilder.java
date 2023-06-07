package muramasa.antimatter.worldgen.object;

import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.StoneLayerOre;
import muramasa.antimatter.worldgen.vein.old.WorldGenVein;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldGenStoneLayerBuilder {
    @Nullable
    private final String id;
    @Nullable
    private StoneType stoneType;
    @Nullable
    private BlockState stoneState;
    @Nullable
    private Integer weight;
    @Nullable
    private Integer minY;
    @Nullable
    private Integer maxY;

    private final ArrayList<ResourceKey<Level>> dimensions;
    private StoneLayerOre[] ores = new StoneLayerOre[0];

    public WorldGenStoneLayerBuilder(String id) {
        this.id = id;
        this.dimensions = new ArrayList<>();
    }

    public final WorldGenStoneLayerBuilder withWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public final WorldGenStoneLayerBuilder withStone(StoneType type) {
        this.stoneType = type;
        this.stoneState = type.getState();
        return this;
    }

    public final WorldGenStoneLayerBuilder withStone(BlockState state) {
        this.stoneState = state;
        return this;
    }

    public final WorldGenStoneLayerBuilder atHeight(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
        return this;
    }

    public final WorldGenStoneLayerBuilder inDimension(ResourceKey<Level> dimension) {
        this.dimensions.add(dimension);
        return this;
    }

    public final WorldGenStoneLayerBuilder inDimensions(List<ResourceKey<Level>> dimensions) {
        this.dimensions.addAll(dimensions);
        return this;
    }

    public WorldGenStoneLayerBuilder addOres(StoneLayerOre... ores) {
        if (stoneType == null){
            throw new IllegalStateException("Stone type must not be null before adding ores!");
        }
        Arrays.stream(ores).forEach(o -> o.setStatesByStoneType(stoneType));
        this.ores = ores;
        return this;
    }

    public final List<WorldGenStoneLayer> buildVein() {
        if (this.id == null) {
            throw new RuntimeException("id is required");
        }
        if (this.stoneState == null && this.stoneType == null) {
            throw new RuntimeException("either stone state or stone type is required");
        }
        if (this.weight == null) {
            throw new RuntimeException("weight is required");
        }
        if (this.dimensions.size() == 0) {
            this.dimensions.add(Level.OVERWORLD);
        }

        return WorldGenStoneLayer.getFlat(this.buildVeinFromJson());
    }

    private WorldGenStoneLayer buildVeinFromJson(){
        WorldGenStoneLayer vein = new WorldGenStoneLayer(
                this.id,
                this.stoneType,
                this.stoneState,
                this.weight,
                this.minY == null ? Integer.MIN_VALUE : this.minY,
                this.maxY == null ? Integer.MAX_VALUE : this.maxY,
                this.dimensions);
        AntimatterWorldGenerator.writeJson(vein.toJson(), this.id, "stone_layers");
        return AntimatterWorldGenerator.readJson(WorldGenStoneLayer.class, vein, WorldGenStoneLayer::fromJson, "stone_layers");
    }
}
