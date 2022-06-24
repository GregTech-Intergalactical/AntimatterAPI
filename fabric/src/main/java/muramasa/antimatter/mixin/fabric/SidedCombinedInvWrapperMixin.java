package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.capability.item.SidedCombinedInvWrapper;
import net.fabricatedforgeapi.item.IItemHandlerStorage;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SidedCombinedInvWrapper.class)
public class SidedCombinedInvWrapperMixin implements IItemHandlerStorage {
    @Override
    public IItemHandler getHandler() {
        return (SidedCombinedInvWrapper)(Object)this;
    }
}
