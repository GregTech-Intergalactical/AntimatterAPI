package muramasa.antimatter.ore;

import muramasa.antimatter.block.BlockMaterialType;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.ISharedAntimatterObject;

public class BlockOreStone extends BlockMaterialType implements ISharedAntimatterObject {

    public BlockOreStone(String domain, Material material) {
        super(domain, material, AntimatterMaterialTypes.ORE_STONE, Properties.of(net.minecraft.world.level.material.Material.STONE).requiresCorrectToolForDrops());
        instancedTextures("stone");
    }

    @Override
    public boolean registerColorHandlers() {
        return false;
    }
}
