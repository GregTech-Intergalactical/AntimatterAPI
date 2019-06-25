package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.data.StoneType;
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

import static muramasa.gtu.api.properties.GTProperties.STONE;

public class BlockOre extends Block implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    protected Material material;

    public BlockOre(Material material) {
        super(net.minecraft.block.material.Material.ROCK);
        this.material = material;
        setUnlocalizedName("ore_" + getId());
        setRegistryName("ore_" + getId());
        setCreativeTab(Ref.TAB_BLOCKS);
        setDefaultState(getDefaultState().withProperty(STONE, 0));
        register();
    }

    protected void register() {
        GregTechAPI.register(BlockOre.class, this);
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

    public IBlockState get(StoneType type) {
        return getDefaultState().withProperty(STONE, type.getInternalId());
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(STONE, Ref.RNG.nextInt(StoneType.getLastInternalId()));
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
    public String getDisplayName(ItemStack stack) {
        return MaterialType.ORE.getDisplayName(material);
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
        for (StoneType s : StoneType.getAll()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), s.getInternalId(), new ModelResourceLocation(Ref.MODID + ":ore_" + getId(), "stone_type=" + s.getInternalId()));
        }
    }
}
