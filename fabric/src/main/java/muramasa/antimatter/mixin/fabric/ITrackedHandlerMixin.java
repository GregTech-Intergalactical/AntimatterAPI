package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.capability.item.ITrackedHandler;
import net.fabricatedforgeapi.item.IItemHandlerStorage;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ITrackedHandler.class)
public interface ITrackedHandlerMixin extends IItemHandlerStorage {
    @Override
    default IItemHandler getHandler(){
        return (ITrackedHandler) this;
    }
}
