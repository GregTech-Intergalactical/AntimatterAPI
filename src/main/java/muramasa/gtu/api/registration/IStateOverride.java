package muramasa.gtu.api.registration;

import net.minecraft.block.Block;
import net.minecraft.state.StateContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public interface IStateOverride {

    //Hack to dynamically create a BlockStates after Block constructor is called
    default void overrideState(Block block, StateContainer container) {
        ObfuscationReflectionHelper.setPrivateValue(Block.class, block, container, 21);
        ObfuscationReflectionHelper.setPrivateValue(Block.class, block, container.getBaseState(), 22);
    }
}
