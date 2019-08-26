package muramasa.gtu.api.ore;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.tileentities.TileEntityMaterial;
import muramasa.gtu.api.tileentities.TileEntityRock;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.client.render.StateMapperRedirect;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static muramasa.gtu.api.properties.GTProperties.*;

public class BlockRock extends Block implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    private StoneType stoneType;

    public BlockRock(StoneType stoneType) {
        super(net.minecraft.block.material.Material.ROCK);
        this.stoneType = stoneType;
        setSoundType(stoneType.getSoundType());
        setHardness(0.4f);
        setResistance(0.2f);
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_BLOCKS);
        GregTechAPI.register(BlockRock.class, this);
    }

    @Override
    public String getId() {
        return "rock_" + stoneType.getId();
    }

    public StoneType getStoneType() {
        return stoneType;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(ROCK_MODEL).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityRock) {
            TileEntityRock rock = (TileEntityRock) tile;
            exState = exState.withProperty(ROCK_MODEL, (int) rock.getModel());
        }
        return exState;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityRock();
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return stoneType.getSoundType();
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.3, 0, 0.3, 0.7, 0.125, 0.7);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }
    
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (fromPos.up().equals(pos)) {
            if (world.getBlockState(fromPos).getBlockFaceShape(world, fromPos, EnumFacing.UP) != BlockFaceShape.SOLID) {
                world.destroyBlock(pos, true);
            }
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (stoneType == StoneType.STONE) {
            items.add(new ItemStack(this));
        }
    }

    /** TileEntity Drops Start **/
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMaterial) {
            TileEntityMaterial ore = (TileEntityMaterial) tile;
            if (ore.getMaterial() == Materials.NULL) {
                int chance = Ref.RNG.nextInt(4);
                drops.add(Materials.Stone.getDustTiny(chance == 0 ? 1 : chance));
            }
            else drops.add(ore.getMaterial().getRock(1));
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);
        world.setBlockToAir(pos);
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {       
        if (GuiScreen.isShiftKeyDown()) return false; 
        harvestBlock(world, player, pos, state, Utils.getTile(world, pos), player.getHeldItem(hand));
        return super.removedByPlayer(state, world, pos, player, true);
    }
    
    /** TileEntity Drops End **/

    @Override
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        TileEntity tile = Utils.getTile(world, pos);
        return tile instanceof TileEntityMaterial && i == 1 ? ((TileEntityMaterial) tile).getMaterial().getRGB() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_rock", "inventory"));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_rock")));
    }

    public static IBlockState get(StoneType stoneType) {
        return GregTechAPI.get(BlockRock.class, "rock_" + stoneType.getId()).getDefaultState();
    }
}
