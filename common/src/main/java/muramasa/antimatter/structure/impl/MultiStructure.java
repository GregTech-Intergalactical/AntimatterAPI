package muramasa.antimatter.structure.impl;

import it.unimi.dsi.fastutil.longs.LongList;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureResult;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiStructure extends Structure {
    List<Structure> validStructures;
    public MultiStructure(List<Structure> validStructures){
        this.validStructures = validStructures;
    }
    @Override
    public StructureResult evaluate(@NotNull TileEntityBasicMultiMachine<?> tile) {
        for (Structure validStructure : validStructures) {
            StructureResult result = validStructure.evaluate(tile);
            if (result.evaluate()) return result;
        }
        return new StructureResult(this);
    }

    @Override
    public LongList allPositions(TileEntityBasicMultiMachine<?> tile) {
        for (Structure validStructure : validStructures) {
            StructureResult result = validStructure.evaluate(tile);
            if (result.evaluate()) return validStructure.allPositions(tile);
        }
        return validStructures.get(validStructures.size() - 1).allPositions(tile);
    }

    @Override
    public int3 size() {
        return null;
    }

    @Override
    public int2 offset() {
        return null;
    }
}
