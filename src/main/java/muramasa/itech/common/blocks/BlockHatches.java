package muramasa.itech.common.blocks;

import net.minecraft.block.state.BlockStateContainer;

import static muramasa.itech.api.properties.ITechProperties.*;

public class BlockHatches extends BlockMachines {

    public BlockHatches(String name) {
        super(name);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(HATCH_TEXTURE, FACING).add(TYPE, TIER, ACTIVE).build();
    }
}
