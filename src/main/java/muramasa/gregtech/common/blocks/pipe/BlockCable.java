package muramasa.gregtech.common.blocks.pipe;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.CableStack;
import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.types.Cable;
import muramasa.gregtech.api.properties.UnlistedInteger;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.pipe.TileEntityCable;
import net.minecraft.block.state.BlockStateContainer;
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

public class BlockCable extends BlockPipe {

    public static UnlistedInteger INSULATED = new UnlistedInteger();

    private Cable type;

    public BlockCable(Cable type) {
        super("cable_" + type.getName());
        this.type = type;
    }

    public Cable getType() {
        return type;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CONNECTIONS, SIZE, INSULATED).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityCable) {
            PipeSize size = ((TileEntityCable) tile).getSize();
            exState = exState.withProperty(SIZE, size != null ? size.ordinal() : PipeSize.TINY.ordinal());
            exState = exState.withProperty(INSULATED, ((TileEntityCable) tile).isInsulated() ? 1 : 0);
        }
        return exState;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (PipeSize size : type.getValidSizes()) {
            items.add(new CableStack(type, size, false).asItemStack());
        }
        for (PipeSize size : type.getValidSizes()) {
            if (size == PipeSize.VTINY) continue;
            items.add(new CableStack(type, size, true).asItemStack());
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
                PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
                boolean insulated = stack.getTagCompound().getBoolean(Ref.KEY_CABLE_STACK_INSULATED);
                ((TileEntityCable) tile).init(size, insulated);
            }
        }
    }
}
