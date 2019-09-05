package muramasa.gtu.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public abstract class BlockBase extends Block {

    public BlockBase(Material material) {
        super(material);
    }

    public BlockStateContainer getBlockStateContainer() {
        return new BlockStateContainer(this);
    }

    public void buildBlockState() {
        BlockStateContainer blockStateContainer = getBlockStateContainer();
        ObfuscationReflectionHelper.setPrivateValue(Block.class, this, blockStateContainer, 21);
        setDefaultState(blockStateContainer.getBaseState());
    }
}
