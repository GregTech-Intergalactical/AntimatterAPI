package muramasa.gregtech.common.blocks;

import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

import static muramasa.gregtech.api.properties.ITechProperties.*;

public class BlockHatch extends BlockMachine {

    public BlockHatch(String name) {
        super(name);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(TYPE, TIER, FACING, OVERLAY, TINT, TEXTURE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityHatch) {
            TileEntityHatch hatch = (TileEntityHatch) tile;
            exState = exState
                .withProperty(TYPE, hatch.getTypeId())
                .withProperty(TIER, hatch.getTierId())
                .withProperty(FACING, hatch.getFacing())
                .withProperty(OVERLAY, hatch.getMachineState().getOverlayId())
                .withProperty(TINT, hatch.getTint())
                .withProperty(TEXTURE, hatch.getTexture());
        }
        return exState;
    }
}
