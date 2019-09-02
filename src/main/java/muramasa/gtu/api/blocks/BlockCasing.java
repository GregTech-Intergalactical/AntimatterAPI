package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.texture.Texture;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockCasing extends BlockBaked {

    private String type;

    public BlockCasing(String type) {
        super(net.minecraft.block.material.Material.IRON);
        this.type = type;
        TEXTURES.add(new Texture("blocks/casing/" + type));
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setHardness(1.0F);
        setResistance(10.0F);
        setCreativeTab(Ref.TAB_BLOCKS);
        setSoundType(SoundType.METAL);
        GregTechAPI.register(BlockCasing.class, this);
    }

    @Override
    public String getId() {
        return "casing_" + type;
    }

    public String getType() {
        return type;
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "wrench";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":" + getId(), "normal"));
    }
}