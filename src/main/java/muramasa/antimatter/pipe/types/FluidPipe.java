package muramasa.antimatter.pipe.types;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTag;
import muramasa.antimatter.pipe.BlockFluidPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.tile.pipe.TileEntityFluidPipe;
import muramasa.antimatter.tile.pipe.TileEntityFluidPipe.TileEntityCoveredFluidPipe;
import net.minecraft.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public class FluidPipe<T extends FluidPipe<T>> extends PipeType<T> {

    protected int maxTemp;
    protected boolean gasProof;
    protected int[] caps, pressures;

    public FluidPipe(String domain, Material material, int maxTemp, boolean gasProof) {
        super(domain, material, TileEntityFluidPipe::new, TileEntityCoveredFluidPipe::new);
        this.maxTemp = maxTemp;
        this.gasProof = gasProof;
        material.flags(MaterialTag.FLUIDPIPE);
    }

    @Override
    public String getId() {
        return "fluid";
    }

    @Override
    public String getTypeName() {
        return "fluid";
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockFluidPipe(this, s)).collect(Collectors.toSet());
    }

    public int getTemperature() {
        return maxTemp;
    }

    public boolean isGasProof() {
        return gasProof;
    }

    public int getCapacity(PipeSize size) {
        return caps[size.ordinal()];
    }

    public int getPressure(PipeSize size) {
        return pressures[size.ordinal()];
    }
    //TODO!
    public T caps(int baseCap) {
        //this.caps = new int[]{baseCap / 6, baseCap / 6, baseCap / 3, baseCap, baseCap * 2, baseCap * 4};
        this.caps = new int[]{baseCap, baseCap, baseCap, baseCap, baseCap, baseCap};
        return (T) this;
    }

    public T caps(int... caps) {
        this.caps = caps;
        return (T) this;
    }

    public T pressures(int basePressure) {
        this.pressures = new int[]{basePressure, basePressure * 2, basePressure * 3, basePressure * 4, basePressure * 5, basePressure * 6};
        return (T) this;
    }

    public T pressures(int... pressures) {
        this.pressures = pressures;
        return (T) this;
    }
}
