package muramasa.antimatter.pipe.types;

import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.pipe.BlockCable;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.tile.pipe.TileEntityCable;
import net.minecraft.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public class Cable<T extends Cable<T>> extends PipeType<T> {

    protected int loss;
    protected Tier tier;
    protected int[] amps;

    public Cable(String domain, Material material, int loss, Tier tier) {
        super(domain, material);
        this.loss = loss;
        this.tier = tier;
        setTile(() -> new TileEntityCable(this));
    }

    @Override
    public String getId() {
        return "cable";
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockCable(this, s, true)).collect(Collectors.toSet());
    }

    public int getLoss() {
        return loss;
    }

    public Tier getTier() {
        return tier;
    }

    public int getAmps(PipeSize size) {
        return amps[size.ordinal()];
    }

    public T amps(int baseAmps) {
        this.amps = new int[]{baseAmps, baseAmps * 2, baseAmps * 4, baseAmps * 8, baseAmps * 12, baseAmps * 16};
        return (T) this;
    }

    public T amps(int... amps) {
        this.amps = amps;
        return (T) this;
    }
}