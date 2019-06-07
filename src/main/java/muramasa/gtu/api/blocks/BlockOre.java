package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.registration.*;
import muramasa.gtu.client.render.StateMapperRedirect;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockOre extends Block implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    private static PropertyInteger STONE = PropertyInteger.create("stone_type", 0, StoneType.getLastInternalId());
//    private static PropertyInteger SET = PropertyInteger.create("material_set", 0, MaterialSet.values().length);

    private Material material;

    public BlockOre(Material material) {
        super(net.minecraft.block.material.Material.ROCK);
        this.material = material;
        setUnlocalizedName("ore_" + getId());
        setRegistryName("ore_" + getId());
        setCreativeTab(Ref.TAB_BLOCKS);
        setDefaultState(getDefaultState().withProperty(STONE, StoneType.STONE.getInternalId()));
        GregTechRegistry.register(BlockOre.class, this);
    }

    @Override
    public String getId() {
        return material.getId();
    }

    public Material getMaterial() {
        return material;
    }

    public IBlockState getOreState(Material material, StoneType type) {
        return getDefaultState().withProperty(STONE, type.getInternalId());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(STONE).build();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STONE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STONE, meta);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
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
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(STONE, StoneType.GRANITE_RED.getInternalId());
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        return Prefix.Ore.getDisplayName(material);
    }

    @Override
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        return i == 1 ? getMaterial().getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 1 ? ((BlockOre) block).getMaterial().getRGB() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        for (StoneType type : StoneType.getAll()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getInternalId(), new ModelResourceLocation(Ref.MODID + ":block_ore", "stone_type=" + type.getInternalId()));
        }
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_ore")));
    }
}
