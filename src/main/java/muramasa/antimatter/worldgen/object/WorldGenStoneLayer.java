package muramasa.antimatter.worldgen.object;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.StoneLayerOre;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldGenStoneLayer extends WorldGenBase<WorldGenStoneLayer> {

    private static Int2ObjectOpenHashMap<List<StoneLayerOre>> COLLISION_MAP = new Int2ObjectOpenHashMap<>();

    private StoneType stoneType;
    private BlockState stoneState;
    private StoneLayerOre[] ores = new StoneLayerOre[0];

    protected WorldGenStoneLayer(BlockState state, int... dims) {
        super("world_gen_stone_layer", WorldGenStoneLayer.class, dims);
        this.stoneState = state;
    }

    protected WorldGenStoneLayer(Block block, int... dims) {
        this(block.getDefaultState(), dims);
    }

    protected WorldGenStoneLayer(StoneType stoneType, int... dims) {
        this(stoneType.getState(), dims);
        this.stoneType = stoneType;
    }

    public static List<WorldGenStoneLayer> add(Block block, int weight, int... dims) {
        return IntStream.of(weight).mapToObj(i -> new WorldGenStoneLayer(block.getDefaultState(), dims)).collect(Collectors.toList());
    }

    public static List<WorldGenStoneLayer> add(BlockState state, int weight, int... dims) {
        return IntStream.of(weight).mapToObj(i -> new WorldGenStoneLayer(state, dims)).collect(Collectors.toList());
    }

    public static List<WorldGenStoneLayer> add(StoneType stoneType, int weight, int... dims) {
        return IntStream.of(weight).mapToObj(i -> new WorldGenStoneLayer(stoneType, dims)).collect(Collectors.toList());
    }

    public WorldGenStoneLayer addOres(StoneLayerOre... ores) {
        if (stoneState.getBlock() instanceof BlockStone) {
            Arrays.stream(ores).forEach(o -> o.setStatesByStoneType(((BlockStone) stoneState.getBlock()).getType()));
        }
        this.ores = ores;
        return this;
    }

    @Nullable
    public StoneType getStoneType() {
        return stoneType;
    }

    public BlockState getStoneState() {
        return stoneState;
    }

    public StoneLayerOre[] getOres() {
        return ores;
    }

    public static void addCollision(BlockState top, BlockState bottom, StoneLayerOre... oresToAdd) {
        COLLISION_MAP.computeIfAbsent(Objects.hash(top, bottom), k -> new ArrayList<>()).addAll(Arrays.asList(oresToAdd));
    }

    public static List<StoneLayerOre> getCollision(StoneType middle, BlockState top, BlockState bottom) {
        if (middle == null) return Collections.emptyList();
        List<StoneLayerOre> list = COLLISION_MAP.get(Objects.hash(top, bottom));
        if (list == null) return Collections.emptyList();
        for (StoneLayerOre ore : list) {
            ore.setStatesByStoneType(middle);
        }
        return list;
    }
}
