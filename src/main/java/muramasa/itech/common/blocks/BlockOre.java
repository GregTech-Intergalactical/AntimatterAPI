package muramasa.itech.common.blocks;

import muramasa.itech.api.enums.ItemFlag;
import muramasa.itech.api.materials.Material;
import muramasa.itech.api.properties.ITechProperties;
import muramasa.itech.api.util.Utils;
import muramasa.itech.common.items.ItemBlockOres;
import muramasa.itech.common.tileentities.base.TileEntityOre;
import muramasa.itech.common.utils.Ref;
import net.minecraft.block.Block;
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

public class BlockOre extends Block {

    private static Material[] generatedOres;

    public BlockOre() {
        super(net.minecraft.block.material.Material.ROCK);
        setUnlocalizedName(Ref.MODID + ".block_ore");
        setRegistryName("block_ore");
        setCreativeTab(Ref.TAB_ORES);

        generatedOres = ItemFlag.CRUSHED.getMats();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(ITechProperties.MATERIAL, ITechProperties.STONE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityOre) {
            TileEntityOre ore = (TileEntityOre) tile;
            exState = exState
                .withProperty(ITechProperties.MATERIAL, ore.getMaterialId())
                .withProperty(ITechProperties.STONE, ore.getStoneId());
        }
        return exState;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i < generatedOres.length; i++) {
            items.add(new ItemStack(this, 1, generatedOres[i].getId()));
        }
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

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.getItem() instanceof ItemBlockOres) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityOre) {
//                ((TileEntityOre) tile).init(stack.getMetadata(), 0);
                ((TileEntityOre) tile).init(generatedOres[RANDOM.nextInt(generatedOres.length)].getId(), RANDOM.nextInt(6));
            }
        }
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return 1.0f + (getHarvestLevel(blockState) * 1.0f);
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 1;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityOre) {
            drops.add(Material.get(((TileEntityOre) tile).getMaterialId()).getChunk(1));
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
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (Material mat : generatedOres) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), mat.getId(), new ModelResourceLocation(getRegistryName(), "inventory"));//NOPMD
        }
    }

    public static class ColorHandler implements IBlockColor {
        @Override
        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
            if (tintIndex == 1) {
                TileEntity tile = Utils.getTile(worldIn, pos);
                if (tile instanceof TileEntityOre) {
                    Material material = ((TileEntityOre) tile).getMaterial();
                    return material != null ? material.getRGB() : 0xffffff;
                }
            }
            return -1;
        }
    }
}
