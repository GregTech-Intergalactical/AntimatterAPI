package muramasa.gregtech.common.blocks;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.CableStack;
import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.types.Cable;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.items.ItemBlockCable;
import muramasa.gregtech.common.tileentities.base.TileEntityCable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCable extends BlockPipe {

    private Cable type;

    public BlockCable(Cable type) {
        super("cable_" + type.getMaterial().getName());
        this.type = type;
    }

    public Cable getType() {
        return type;
    }

    @Override
    public int getRGB() {
        return type.getMaterial().getRGB();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (PipeSize size : PipeSize.VALUES) {
            items.add(new CableStack(type, size, false).asItemStack());
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
        if (stack.getItem() instanceof ItemBlockCable) {
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
}
