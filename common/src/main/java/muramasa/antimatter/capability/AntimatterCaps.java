package muramasa.antimatter.capability;

import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import net.minecraftforge.common.capabilities.Capability;

public class AntimatterCaps {
    @ExpectPlatform
    public static Capability<ICoverHandler<?>> getCOVERABLE_HANDLER_CAPABILITY(){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static Capability<IComponentHandler> getCOMPONENT_HANDLER_CAPABILITY(){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static Capability<MachineRecipeHandler<?>> getRECIPE_HANDLER_CAPABILITY(){
        throw new AssertionError();
    }
}
