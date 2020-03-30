package muramasa.antimatter.ore;

import muramasa.antimatter.block.BlockMaterialType;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import net.minecraft.block.Block;

public class BlockOreStone extends BlockMaterialType {

    public BlockOreStone(String domain, Material material) {
        super(domain, material, MaterialType.ORE_STONE, Block.Properties.create(net.minecraft.block.material.Material.ROCK));
        instancedTextures("stone");
    }

    @Override
    public boolean registerColorHandlers() {
        return false;
    }
}
