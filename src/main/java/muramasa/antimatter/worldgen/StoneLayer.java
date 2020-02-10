package muramasa.antimatter.worldgen;

import muramasa.antimatter.ore.StoneType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.Arrays;

public class StoneLayer {

    private BlockState stoneState;
    private StoneLayerOre[] ores = new StoneLayerOre[0];

    public StoneLayer(Block block) {
        this(block.getDefaultState());
    }

    public StoneLayer(BlockState state) {
        this.stoneState = state;
    }

    public StoneLayer(StoneType stoneType, StoneLayerOre... ores) {
        this.stoneState = stoneType.getState();
        this.ores = ores;
        Arrays.stream(ores).forEach(o -> o.setState(stoneType));
    }

    public BlockState getStoneState() {
        return stoneState;
    }

    public StoneLayerOre[] getOres() {
        return ores;
    }
}
