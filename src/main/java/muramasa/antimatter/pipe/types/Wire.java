package muramasa.antimatter.pipe.types;

import muramasa.antimatter.tier.VoltageTier;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.pipe.BlockCable;
import net.minecraft.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public class Wire<T extends Wire<T>> extends Cable<T> {

    public Wire(String domain, Material material, int loss, VoltageTier tier) {
        super(domain, material, loss, tier);
    }

    @Override
    public String getId() {
        return "wire";
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockCable(this, s, false)).collect(Collectors.toSet());
    }
}
