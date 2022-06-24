package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.capability.item.ROCombinedInvWrapper;
import net.fabricatedforgeapi.item.IItemHandlerStorage;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ROCombinedInvWrapper.class)
public class ROCombionedInvWrapperMixin implements IItemHandlerStorage {
    @Override
    public IItemHandler getHandler() {
        return (ROCombinedInvWrapper)(Object)this;
    }
}
