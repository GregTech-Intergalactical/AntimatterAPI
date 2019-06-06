package muramasa.gtu.api.blocks.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.pipe.CableStack;
import muramasa.gtu.api.pipe.PipeSize;
import muramasa.gtu.api.pipe.types.Cable;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.tileentities.pipe.TileEntityCable;
import muramasa.gtu.api.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;

import static muramasa.gtu.api.properties.GTProperties.CONNECTIONS;
import static muramasa.gtu.api.properties.GTProperties.TEXTURE;

public class BlockCable extends BlockPipe implements IItemBlock, IColorHandler {

    private Cable type;

    public BlockCable(Cable type) {
        super("cable_" + type.getName(), Textures.PIPE_DATA[1]);
        this.type = type;
    }

    public Cable getType() {
        return type;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityCable) {
            TileEntityCable cable = (TileEntityCable) tile;
            exState = exState.withProperty(CONNECTIONS, cable.getConnections());
            exState = exState.withProperty(TEXTURE, cable.isInsulated() ? Textures.PIPE_DATA[2] : getBlockData());
        }
        return exState;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (PipeSize size : type.getValidSizes()) {
            items.add(new CableStack(this, type, size, false).asItemStack());
        }
        for (PipeSize size : type.getValidSizes()) {
            items.add(new CableStack(this, type, size, true).asItemStack());
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCable();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasTagCompound()) {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile instanceof TileEntityCable) {
                boolean insulated = stack.getTagCompound().getBoolean(Ref.KEY_CABLE_STACK_INSULATED);
                ((TileEntityCable) tile).init(insulated);
            }
        }
    }

    @Override
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityCable) {
            if (((TileEntityCable) tile).isInsulated()) {
                return i == 2 ? getRGB() : -1;
            } else {
                return i == 0 || i == 2 ? getRGB() : -1;
            }
        }
        return -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.KEY_CABLE_STACK_INSULATED) && stack.getTagCompound().getBoolean(Ref.KEY_CABLE_STACK_INSULATED)) {
            return i == 2 ? ((BlockPipe) block).getRGB() : -1;
        } else {
            return i == 0 || i == 1 || i == 2 ? ((BlockPipe) block).getRGB() : -1;
        }
    }
}
