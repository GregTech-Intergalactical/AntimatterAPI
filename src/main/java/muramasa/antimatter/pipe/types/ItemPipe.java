package muramasa.antimatter.pipe.types;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.pipe.BlockItemPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.tile.pipe.TileEntityItemPipe;
import net.minecraft.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public class ItemPipe<T extends ItemPipe<T>> extends PipeType<T> {

    protected int[] slots, steps;

    public ItemPipe(String domain, Material material) {
        super(domain, material);
        setTile(() -> new TileEntityItemPipe(this));
    }

    @Override
    public String getId() {
        return "item";
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockItemPipe(this, s, false)).collect(Collectors.toSet());
    }

    public int getSlotSize(PipeSize size) {
        return slots[size.ordinal()];
    }

    public int getStepSize(PipeSize size) {
        return steps[size.ordinal()];
    }

    public T slots(int baseSlots) {
        slots = new int[]{baseSlots, baseSlots, baseSlots, baseSlots, baseSlots * 2, baseSlots * 4};
        return (T) this;
    }

    public T slots(int[] slots) {
        this.slots = slots;
        return (T) this;
    }

    public T steps(int baseSteps) {
        steps = new int[]{32768 / baseSteps, 32768 / baseSteps, 32768 / baseSteps, 32768 / baseSteps, 16384 / baseSteps, 8192 / baseSteps};
        return (T) this;
    }

    public T steps(int[] steps) {
        this.steps = steps;
        return (T) this;
    }
}
