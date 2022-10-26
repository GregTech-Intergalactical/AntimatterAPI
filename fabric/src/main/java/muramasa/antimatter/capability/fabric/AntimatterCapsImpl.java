package muramasa.antimatter.capability.fabric;

import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import org.objectweb.asm.Type;

public class AntimatterCapsImpl {
    public static final Capability<ICoverHandler<?>> COVERABLE_HANDLER_CAPABILITY = CapabilityManager.INSTANCE.get(Type.getInternalName(ICoverHandler.class), false);
    public static final Capability<IComponentHandler> COMPONENT_HANDLER_CAPABILITY = CapabilityManager.get(IComponentHandler.class);
    public static final Capability<MachineRecipeHandler<?>> RECIPE_HANDLER_CAPABILITY = CapabilityManager.INSTANCE.get(Type.getInternalName(MachineRecipeHandler.class), false);


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
