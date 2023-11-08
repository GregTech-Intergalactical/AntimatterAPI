package muramasa.antimatter.pipe.types;

import lombok.Getter;
import muramasa.antimatter.blockentity.pipe.BlockEntityFluidPipe;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.pipe.BlockFluidPipe;
import muramasa.antimatter.pipe.PipeSize;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FluidPipe<T extends FluidPipe<T>> extends PipeType<T> {

    @Getter
    protected int maxTemperature;
    @Getter
    protected boolean gasProof;
    @Getter
    protected boolean acidProof = false;
    protected int[] caps, pressures;

    public FluidPipe(String domain, Material material, int maxTemperature, boolean gasProof) {
        super(domain, material, BlockEntityFluidPipe::new);
        this.maxTemperature = maxTemperature;
        this.gasProof = gasProof;
        material.flags(MaterialTags.FLUIDPIPE);
        sizes(PipeSize.TINY, PipeSize.SMALL, PipeSize.NORMAL, PipeSize.LARGE, PipeSize.HUGE, PipeSize.QUADRUPLE, PipeSize.NONUPLE);
    }

    @Override
    public String getTypeName() {
        return "fluid_pipe";
    }

    @Override
    public String getType() {
        return "fluid_pipe";
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockFluidPipe(this, s)).collect(Collectors.toSet());
    }


    public int getPressure(PipeSize size) {
        if (size == PipeSize.NONUPLE) return getPressure(PipeSize.SMALL);
        if (size == PipeSize.QUADRUPLE) return getPressure(PipeSize.NORMAL);
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
        basePressure /= 20;
        this.pressures = new int[]{basePressure, basePressure * 2, basePressure * 3, basePressure * 4, basePressure * 5, basePressure * 6};
        return (T) this;
    }

    public T pressures(int... pressures) {
        this.pressures = Arrays.stream(pressures).map(t -> t / 20).toArray();
        return (T) this;
    }

    public T acidProof(boolean acidProof){
        this.acidProof = acidProof;
        return (T) this;
    }

}
