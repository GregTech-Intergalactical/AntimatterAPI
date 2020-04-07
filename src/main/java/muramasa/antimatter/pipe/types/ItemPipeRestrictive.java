package muramasa.antimatter.pipe.types;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.pipe.BlockItemPipe;
import muramasa.antimatter.pipe.PipeSize;
import net.minecraft.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public class ItemPipeRestrictive<T extends ItemPipeRestrictive<T>> extends ItemPipe<T> {

    public ItemPipeRestrictive(String domain, Material material) {
        super(domain, material);
    }

    @Override
    public String getId() {
        return "item_restrictive";
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockItemPipe(this, s, true)).collect(Collectors.toSet());
    }

    @Override
    public int getStepSize(PipeSize size) {
        return super.getStepSize(size) * 1000;
    }
}
