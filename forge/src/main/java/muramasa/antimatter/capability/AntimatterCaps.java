package muramasa.antimatter.capability;

import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class AntimatterCaps {
    

    public static final Capability<ICoverHandler<?>> COVERABLE_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IComponentHandler> COMPONENT_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<MachineRecipeHandler<?>> RECIPE_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});


    public static void register(RegisterCapabilitiesEvent ev) {
        ev.register(ICoverHandler.class);
        ev.register(IComponentHandler.class);
        ev.register(MachineRecipeHandler.class);
    }
}
