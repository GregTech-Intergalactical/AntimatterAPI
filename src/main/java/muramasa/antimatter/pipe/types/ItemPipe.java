package muramasa.antimatter.pipe.types;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTag;
import muramasa.antimatter.pipe.BlockItemPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.tile.pipe.TileEntityItemPipe;
import muramasa.antimatter.tile.pipe.TileEntityItemPipe.TileEntityCoveredItemPipe;
import net.minecraft.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public class ItemPipe<T extends ItemPipe<T>> extends PipeType<T> {

    protected int[] caps;

    public ItemPipe(String domain, Material material) {
        super(domain, material, t -> new TileEntityItemPipe<>(t, false), TileEntityCoveredItemPipe::new);
        material.flags(MaterialTag.ITEMPIPE);
    }

    @Override
    public String getType() {
        return "item";
    }

    @Override
    public String getTypeName() {
        return "item";
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockItemPipe(this, s)).collect(Collectors.toSet());
    }

    public int getCapacity(PipeSize size) {
        return caps[size.ordinal()];
    }

    public T caps(int baseCap) {
        this.caps = new int[]{baseCap, baseCap * 2, baseCap * 3, baseCap * 4, baseCap * 5, baseCap * 6};
        return (T) this;
    }

    public T caps(int... caps) {
        this.caps = caps;
        return (T) this;
    }
}
