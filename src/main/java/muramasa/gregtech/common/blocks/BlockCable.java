package muramasa.gregtech.common.blocks;

import muramasa.gregtech.api.properties.UnlistedInteger;
import muramasa.gregtech.common.tileentities.base.TileEntityCable;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockCable extends Block {

    public static final UnlistedInteger CONNECTIONS = new UnlistedInteger();

    public BlockCable() {
        super(net.minecraft.block.material.Material.CARPET);
        setUnlocalizedName(Ref.MODID + "block_cable");
        setRegistryName("block_cable");
        setCreativeTab(Ref.TAB_MACHINES);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CONNECTIONS).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedState = (IExtendedBlockState) state;
        TileEntityCable tile = (TileEntityCable) world.getTileEntity(pos);
        if (tile != null) {
            extendedState = extendedState.withProperty(CONNECTIONS, tile.cableConnections);
        }
        return extendedState;
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
        return new TileEntityCable();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        TileEntityCable tile = (TileEntityCable) world.getTileEntity(pos);
//        if (tile != null) {
//            if (ItemList.DebugScanner.isEqual(player.getHeldItem(hand))) {
//                player.sendMessage(new TextComponentString(tile.cableConnections + ""));
//                tile.toggleShouldConnect(facing);
//            }
//            return true;
//        }
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
//        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(this));
    }
}
