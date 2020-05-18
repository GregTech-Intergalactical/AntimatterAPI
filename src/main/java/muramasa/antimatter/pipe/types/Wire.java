package muramasa.antimatter.pipe.types;

import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.pipe.BlockCable;
import net.minecraft.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public class Wire<T extends Wire<T>> extends Cable<T> {

    public Wire(String domain, Material material, int loss, Tier tier) {
        super(domain, material, loss, tier);
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockCable(this, s, false)).collect(Collectors.toSet());
    }
}
