package muramasa.gregtech.common.blocks;

import net.minecraft.block.state.BlockStateContainer;

import static muramasa.gregtech.api.properties.GTProperties.*;

public class BlockHatch extends BlockMachine {

    public BlockHatch(String type) {
        super(type);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(TYPE, FACING, TEXTURE).build();
    }
}
