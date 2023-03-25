package muramasa.antimatter.pipe.types;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.pipe.BlockCable;
import muramasa.antimatter.pipe.BlockHeatPipe;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.pipe.TileEntityHeatPipe;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public class HeatPipe<T extends HeatPipe<T>> extends PipeType<T> {

    public final int conductivity;

    public HeatPipe(String domain, Material material, int conductivity) {
        super(domain, material, TileEntityHeatPipe::new);
        this.conductivity = conductivity;
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockHeatPipe(this, s)).collect(Collectors.toSet());
    }

    @Override
    public String getType() {
        return "heat_pipe";
    }

    @Override
    public String getTypeName() {
        return "heat_pipe";
    }
}
