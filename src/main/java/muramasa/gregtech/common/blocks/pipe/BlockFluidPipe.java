package muramasa.gregtech.common.blocks.pipe;

import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.PipeStack;
import muramasa.gregtech.api.pipe.types.FluidPipe;
import muramasa.gregtech.api.texture.TextureData;
import muramasa.gregtech.api.tileentities.pipe.TileEntityFluidPipe;
import muramasa.gregtech.client.render.models.ModelPipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockFluidPipe extends BlockPipe {

    private FluidPipe type;

    public BlockFluidPipe(FluidPipe type) {
        super("fluid_pipe_" + type.getName(), new TextureData().base(ModelPipe.PIPE).overlay(ModelPipe.PIPE_FACE));
        this.type = type;
    }

    public FluidPipe getType() {
        return type;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (PipeSize size : type.getValidSizes()) {
            items.add(new PipeStack(this, type, size).asItemStack());
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityFluidPipe();
    }
}
