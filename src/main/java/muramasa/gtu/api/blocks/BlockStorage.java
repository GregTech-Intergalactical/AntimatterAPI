package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.IModelOverride;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static muramasa.gtu.api.properties.GTProperties.ORE_STONE;
import static muramasa.gtu.api.properties.GTProperties.STORAGE_TYPE;

public class BlockStorage extends Block implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    private Material material;

    public BlockStorage(Material material) {
        super(net.minecraft.block.material.Material.IRON);
        this.material = material;
        setUnlocalizedName("storage_" + getId());
        setRegistryName("storage_" + getId());
        setCreativeTab(Ref.TAB_BLOCKS);
        setDefaultState(getDefaultState().withProperty(STORAGE_TYPE, 0));
        GregTechAPI.register(BlockStorage.class, this);
    }

    @Override
    public String getId() {
        return material.getId();
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(STORAGE_TYPE).build();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(STORAGE_TYPE, Ref.RNG.nextInt(2));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STORAGE_TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STORAGE_TYPE, meta);
    }

    public IBlockState get(MaterialType type) {
        return getDefaultState().withProperty(ORE_STONE, type == MaterialType.BLOCK ? 0 : 1);
    }

    //TODO
    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return 1.0f + (getHarvestLevel(blockState) * 1.0f);
    }

    //TODO
    @Override
    public int getHarvestLevel(IBlockState state) {
        return 1;
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        return MaterialType.BLOCK.getDisplayName(material);
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return state.getValue(STORAGE_TYPE) == 1;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int i) {
        return i == 0 ? getMaterial().getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? ((BlockStorage) block).getMaterial().getRGB() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":storage_" + getId(), "storage_type=0"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 1, new ModelResourceLocation(Ref.MODID + ":storage_" + getId(), "storage_type=1"));
    }
}
