package muramasa.antimatter.pipe.types;

import lombok.Getter;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.pipe.BlockCable;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.blockentity.pipe.BlockEntityCable;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public class Cable<T extends Cable<T>> extends PipeType<T> {

    @Getter
    protected double loss;
    @Getter
    protected Tier tier;
    protected int[] amps;

    public Cable(String domain, Material material, double loss, Tier tier) {
        super(domain, material, BlockEntityCable::new);
        this.loss = loss;
        this.tier = tier;
        material.flags(MaterialTags.CABLE);
    }

    public Cable(String domain, Material material, int loss, Tier tier) {
        this(domain, material, (double) loss, tier);
    }

    @Override
    public String getType() {
        return "cable";
    }

    @Override
    public String getTypeName() {
        return "energy";
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockCable(this, s, true)).collect(Collectors.toSet());
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

    public T loss(double loss){
        this.loss = loss;
        return (T) this;
    }
}