package muramasa.gtu.api.blocks;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import net.minecraft.item.ItemStack;

public class BlockOreSmall extends BlockOre {

    public BlockOreSmall(Material material) {
        super(material);
    }

    @Override
    protected void register() {
        GregTechAPI.register(BlockOreSmall.class, this);
    }

    @Override
    public String getId() {
        return "small_".concat(super.getId());
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        return MaterialType.ORE_SMALL.getDisplayName(material);
    }
}
