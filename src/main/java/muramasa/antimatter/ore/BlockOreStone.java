package muramasa.antimatter.ore;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockMaterialType;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import net.minecraft.block.Block;

public class BlockOreStone extends BlockMaterialType {

    public BlockOreStone(Material material) {
        super(material, MaterialType.ORE_STONE, Block.Properties.create(net.minecraft.block.material.Material.ROCK));
        instancedTextures("stone");
        setRegistryName(MaterialType.ORE_STONE.getId() + "_" + material.getId());
        AntimatterAPI.register(BlockOreStone.class, this);
    }

    @Override
    public boolean registerColorHandlers() {
        return false;
    }
}
