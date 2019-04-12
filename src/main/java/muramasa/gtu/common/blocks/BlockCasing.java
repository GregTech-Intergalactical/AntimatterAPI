package muramasa.gtu.common.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Casing;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.registration.IHasModelOverride;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.tileentities.multi.TileEntityCasing;
import muramasa.gtu.client.render.GTModelLoader;
import muramasa.gtu.client.render.StateMapperRedirect;
import muramasa.gtu.client.render.overrides.ItemOverrideCasing;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockCasing extends BlockBaked implements IHasModelOverride {

    private Casing type;

    public BlockCasing(Casing type) {
        super(TextureData.get().base(type.getTexture()), new ModelResourceLocation(Ref.MODID + ":layered"));
        setUnlocalizedName("casing_" + type.getName());
        setRegistryName("casing_" + type.getName());
        setHardness(1.0F);
        setResistance(10.0F);
        setCreativeTab(Ref.TAB_BLOCKS);
        setSoundType(SoundType.METAL);
        this.type = type;
    }

    public Casing getType() {
        return type;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCasing();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "wrench";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_casing", "casing_type=" + type.getName()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":block_casing", "casing_type=" + type.getName())));
        GTModelLoader.register("block_casing", this);
    }

    @Override
    public List<Texture> getTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        for (Casing type : Casing.getAll()) {
            textures.add(type.getTexture());
        }
        textures.addAll(Arrays.asList(Textures.LARGE_TURBINE));
        textures.addAll(Arrays.asList(Textures.LARGE_TURBINE_ACTIVE));
        return textures;
    }

    @Override
    public ItemOverrideList getOverride(IBakedModel baked) {
        return new ItemOverrideCasing();
    }
}
