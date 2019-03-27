package muramasa.gregtech.common.blocks.pipe;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.ItemPipeStack;
import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.types.ItemPipe;
import muramasa.gregtech.api.properties.UnlistedInteger;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.pipe.TileEntityItemPipe;
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

public class BlockItemPipe extends BlockPipe {

    public static UnlistedInteger RESTRICTIVE = new UnlistedInteger();

    private ItemPipe type;

    public BlockItemPipe(ItemPipe type) {
        super("item_pipe_" + type.getName());
        this.type = type;
    }

    @Override
    public ItemPipe getType() {
        return type;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CONNECTIONS, SIZE, RESTRICTIVE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityItemPipe) {
            PipeSize size = ((TileEntityItemPipe) tile).getSize();
            exState = exState.withProperty(SIZE, size != null ? size.ordinal() : PipeSize.TINY.ordinal());
            exState = exState.withProperty(RESTRICTIVE, ((TileEntityItemPipe) tile).isRestrictive() ? 1 : 0);
        }
        return exState;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (PipeSize size : type.getValidSizes()) {
            items.add(new ItemPipeStack(this, type, size, false).asItemStack());
        }
        for (PipeSize size : type.getValidSizes()) {
            items.add(new ItemPipeStack(this, type, size, true).asItemStack());
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityItemPipe();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasTagCompound()) {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile instanceof TileEntityItemPipe) {
                PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
                boolean restrictive = stack.getTagCompound().getBoolean(Ref.KEY_ITEM_PIPE_STACK_RESTRICTIVE);
                ((TileEntityItemPipe) tile).init(getType(), size, restrictive);
            }
        }
    }
}
