package muramasa.antimatter.ore;

import muramasa.antimatter.Data;
import muramasa.antimatter.block.BlockMaterialType;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import net.minecraft.world.level.block.Block;

public class BlockOreStone extends BlockMaterialType implements ISharedAntimatterObject {

    public BlockOreStone(String domain, Material material) {
        super(domain, material, Data.ORE_STONE, Block.Properties.of(net.minecraft.world.level.material.Material.STONE).requiresCorrectToolForDrops());
        instancedTextures("stone");
    }

    @Override
    public boolean registerColorHandlers() {
        return false;
    }
}
