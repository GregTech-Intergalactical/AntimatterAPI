package muramasa.itech.common.blocks;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.ItemFlag;
import muramasa.itech.api.materials.Material;
import muramasa.itech.api.properties.UnlistedString;
import muramasa.itech.common.items.ItemBlockOres;
import muramasa.itech.common.tileentities.base.TileEntityOre;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockOres extends Block {

    public static final PropertyInteger STONETYPE = PropertyInteger.create("stonetype", 0, 6);
    public static final UnlistedString TEXTURE = new UnlistedString();

    private static Material[] generatedOres;


    //TODO Determine texture type via getActualState, use in exState, BakedModel to return re-textured quads based on texture

    public BlockOres() {
        super(net.minecraft.block.material.Material.ROCK);
        setUnlocalizedName(ITech.MODID + ".blockores");
        setRegistryName("blockores");
        setCreativeTab(ITech.TAB_ORES);
        setDefaultState(blockState.getBaseState().withProperty(STONETYPE, 0));
        generatedOres = ItemFlag.CRUSHED.getMats();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(STONETYPE).add(TEXTURE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        String name = world.getBlockState(pos.down()).getBlock().getRegistryName().toString();
//        System.out.println("EX: " + name);
        exState = exState.withProperty(TEXTURE, name);
        return exState;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STONETYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return blockState.getBaseState().withProperty(STONETYPE, meta);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (Material mat : generatedOres) {
            items.add(new ItemStack(this, 1, mat.getId()));
        }
    }

//    @Override
//    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
//        return null;
//    }

//    @Override
//    public void breakBlock(World world, BlockPos pos, IBlockState state) {
//        TileEntity tile = world.getTileEntity(pos);
//        System.out.println(tile == null);
//        if (tile instanceof TileEntityOre) {
//            int id = ((TileEntityOre) tile).materialId;
//            if (id > -1 && Material.generated[id].hasFlag(MaterialFlag.CRUSHED)) {
//                ItemStack stack = MetaItem.get(Prefix.chunk, Material.generated[id]);
//                world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack));
//            }
//        }
//    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.getItem() instanceof ItemBlockOres) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityOre) {
                ((TileEntityOre) tile).init(Material.generated[stack.getMetadata()].getName());
            }
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockToAir(pos);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        super.getDrops(drops, world, pos, state, fortune);
        TileEntityOre tile = (TileEntityOre) world.getTileEntity(pos);
        System.out.println(tile);
        if (tile != null) {
            drops.add(tile.getMaterial().getChunk(1));
        }
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityOre();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (Material mat : generatedOres) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), mat.getId(), new ModelResourceLocation(getRegistryName(), "stonetype=0"));
        }
    }

    public static class ColorHandler implements IBlockColor {
        @Override
        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
            if (tintIndex == 1) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile != null && tile instanceof TileEntityOre) {
                    Material material = ((TileEntityOre) tile).getMaterial();
                    return material != null ? material.getRGB() : 0xffffff;
                }
            }
            return -1;
        }
    }
}
