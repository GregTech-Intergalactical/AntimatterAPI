package muramasa.gtu.common.blocks.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.pipe.ItemPipeStack;
import muramasa.gtu.api.pipe.PipeSize;
import muramasa.gtu.api.pipe.types.ItemPipe;
import muramasa.gtu.api.properties.GTProperties;
import muramasa.gtu.api.properties.UnlistedInteger;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.tileentities.pipe.TileEntityItemPipe;
import muramasa.gtu.client.render.models.ModelPipe;
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
        super("item_pipe_" + type.getName(), new TextureData().base(ModelPipe.PIPE).overlay(ModelPipe.PIPE_FACE));
        this.type = type;
    }

    @Override
    public ItemPipe getType() {
        return type;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CONNECTIONS, SIZE, RESTRICTIVE, GTProperties.TEXTURE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityItemPipe) {
            TileEntityItemPipe pipe = (TileEntityItemPipe) tile;
            exState = exState.withProperty(SIZE, pipe.getSize().ordinal());
            exState = exState.withProperty(CONNECTIONS, pipe.getConnections());
            exState = exState.withProperty(RESTRICTIVE, pipe.isRestrictive() ? 1 : 0);
            exState = exState.withProperty(GTProperties.TEXTURE, getBlockData());
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
