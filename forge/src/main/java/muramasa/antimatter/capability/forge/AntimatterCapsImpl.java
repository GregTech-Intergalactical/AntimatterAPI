package muramasa.antimatter.capability.forge;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Ref.ID)
public class AntimatterCapsImpl {
    public static final Map<String, Capability<?>> CAP_MAP = new HashMap<>();
    public static final Capability<ICoverHandler<?>> COVERABLE_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IComponentHandler> COMPONENT_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<MachineRecipeHandler<?>> RECIPE_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});


    public static Capability<ICoverHandler<?>> getCOVERABLE_HANDLER_CAPABILITY(){
        return COVERABLE_HANDLER_CAPABILITY;
    }

    public static Capability<IComponentHandler> getCOMPONENT_HANDLER_CAPABILITY(){
        return COMPONENT_HANDLER_CAPABILITY;
    }

    public static Capability<MachineRecipeHandler<?>> getRECIPE_HANDLER_CAPABILITY(){
        return RECIPE_HANDLER_CAPABILITY;
    }
    public static void register(RegisterCapabilitiesEvent ev) {
        ev.register(ICoverHandler.class);
        ev.register(IComponentHandler.class);
        ev.register(MachineRecipeHandler.class);
    }
}
