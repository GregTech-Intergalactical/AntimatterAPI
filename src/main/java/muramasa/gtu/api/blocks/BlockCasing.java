package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelOverride;
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

public class BlockCasing extends BlockBaked implements IGregTechObject, IModelOverride {

    private static int lastInternalId = 0;

    private String id;
    private int internalId;

    public BlockCasing(String id) {
        super();
        this.id = id;
        this.internalId = lastInternalId++;
        setData(TextureData.get().base(getTexture()).overlay(getTexture()));
        setModel(LAYERED);
        setUnlocalizedName("casing_" + getId());
        setRegistryName("casing_" + getId());
        setHardness(1.0F);
        setResistance(10.0F);
        setCreativeTab(Ref.TAB_BLOCKS);
        setSoundType(SoundType.METAL);
        GregTechAPI.register(BlockCasing.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public int getInternalId() {
        return internalId;
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

    public Texture getTexture() {
        return new Texture("blocks/casing/" + id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<Texture> getTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        GregTechAPI.all(BlockCasing.class).forEach(c -> textures.add(c.getTexture()));
        textures.addAll(Arrays.asList(Textures.LARGE_TURBINE));
        textures.addAll(Arrays.asList(Textures.LARGE_TURBINE_ACTIVE));
        return textures;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemOverrideList getOverride(IBakedModel baked) {
        return new ItemOverrideCasing();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_casing", "id=" + getId()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":block_casing", "id=" + getId())));
        GTModelLoader.register("block_casing", this);
    }
}