package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.client.render.StateMapperRedirect;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockCoil extends Block implements IGregTechObject, IModelOverride {

    private String id;
    private int heatCapacity;

    public BlockCoil(String id, int heatCapacity) {
        super(net.minecraft.block.material.Material.IRON);
        this.id = id;
        this.heatCapacity = heatCapacity;
        setUnlocalizedName("coil_".concat(getId()));
        setRegistryName("coil_".concat(getId()));
        setHardness(1.0F);
        setResistance(10.0F);
        setCreativeTab(Ref.TAB_BLOCKS);
        setSoundType(SoundType.METAL);
        GregTechAPI.register(BlockCoil.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public int getHeatCapacity() {
        return heatCapacity;
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "wrench";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_coil", "id=" + getId()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":block_coil", "id=" + getId())));
    }
}
