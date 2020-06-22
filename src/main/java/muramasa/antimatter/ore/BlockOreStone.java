package muramasa.antimatter.ore;

import muramasa.antimatter.Data;
import muramasa.antimatter.block.BlockMaterialType;
import muramasa.antimatter.material.Material;
import net.minecraft.block.Block;

public class BlockOreStone extends BlockMaterialType {

    public BlockOreStone(String domain, Material material) {
        super(domain, material, Data.ORE_STONE, Block.Properties.create(net.minecraft.block.material.Material.ROCK));
        instancedTextures("stone");
    }

    @Override
    public boolean registerColorHandlers() {
        return false;
    }
}
