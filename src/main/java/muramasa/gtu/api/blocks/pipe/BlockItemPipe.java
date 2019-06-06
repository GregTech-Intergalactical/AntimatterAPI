package muramasa.gtu.api.blocks.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.pipe.ItemPipeStack;
import muramasa.gtu.api.pipe.PipeSize;
import muramasa.gtu.api.pipe.types.ItemPipe;
import muramasa.gtu.api.tileentities.pipe.TileEntityItemPipe;
import muramasa.gtu.api.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockItemPipe extends BlockPipe {

    private ItemPipe type;

    public BlockItemPipe(ItemPipe type) {
        super("item_pipe_" + type.getName(), Textures.PIPE_DATA[0]);
        this.type = type;
    }

    @Override
    public ItemPipe getType() {
        return type;
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
                boolean restrictive = stack.getTagCompound().getBoolean(Ref.KEY_ITEM_PIPE_STACK_RESTRICTIVE);
                ((TileEntityItemPipe) tile).init(restrictive);
            }
        }
    }
}
