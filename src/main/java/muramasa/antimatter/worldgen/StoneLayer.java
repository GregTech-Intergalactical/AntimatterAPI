package muramasa.antimatter.worldgen;

import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.block.BlockState;

import java.util.Arrays;

public class StoneLayer {

    private StoneType type;
    private BlockState[] ores;

    public StoneLayer(StoneType type, Material... materials) {
        this.type = type;
        ores = Arrays.stream(materials).map(m -> BlockOre.get(m, MaterialType.ORE, type)).toArray(BlockState[]::new);
    }

    public StoneType getType() {
        return type;
    }

    public BlockState[] getOres() {
        return ores;
    }
}
