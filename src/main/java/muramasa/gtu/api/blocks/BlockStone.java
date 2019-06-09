package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.api.registration.GregTechRegistry;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.client.render.StateMapperRedirect;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStone extends Block implements IGregTechObject, IModelOverride {

    private StoneType type;

    public BlockStone(StoneType type) {
        super(net.minecraft.block.material.Material.IRON);
        this.type = type;
        setUnlocalizedName("stone_" + getId());
        setRegistryName("stone_" + getId());
        setCreativeTab(Ref.TAB_BLOCKS);
        GregTechRegistry.register(BlockStone.class, this);
    }

    public StoneType getType() {
        return type;
    }

    @Override
    public String getId() {
        return type.getId();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_stone", "id=" + getId()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":block_stone", "id=" + getId())));
    }
}
