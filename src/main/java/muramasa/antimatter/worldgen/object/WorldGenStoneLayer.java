package muramasa.antimatter.worldgen.object;

import muramasa.antimatter.blocks.BlockStone;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.StoneLayerOre;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.Arrays;

public class WorldGenStoneLayer extends WorldGenBase<WorldGenStoneLayer> {

    private BlockState stoneState;
    private StoneLayerOre[] ores = new StoneLayerOre[0];

    public WorldGenStoneLayer(BlockState state, int... dims) {
        super("world_gen_stone_layer", WorldGenStoneLayer.class, dims);
        this.stoneState = state;
    }

    public WorldGenStoneLayer(Block block, int... dims) {
        this(block.getDefaultState(), dims);
    }

    public WorldGenStoneLayer(StoneType stoneType, int... dims) {
        this(stoneType.getState(), dims);
    }

    public WorldGenStoneLayer addOres(StoneLayerOre... ores) {
        if (stoneState.getBlock() instanceof BlockStone) {
            Arrays.stream(ores).forEach(o -> o.setState(((BlockStone) stoneState.getBlock()).getType()));
        }
        this.ores = ores;
        return this;
    }

    public BlockState getStoneState() {
        return stoneState;
    }

    public StoneLayerOre[] getOres() {
        return ores;
    }
}
