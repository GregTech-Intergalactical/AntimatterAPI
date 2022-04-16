package muramasa.antimatter.capability;

import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraftforge.common.capabilities.Capability;

public class AntimatterCaps {
    public static final Capability<ICoverHandler> COVERABLE_HANDLER_CAPABILITY = AntimatterPlatformUtils.getNewCap();
    public static final Capability<IComponentHandler> COMPONENT_HANDLER_CAPABILITY = AntimatterPlatformUtils.getNewCap();
    public static final Capability<MachineRecipeHandler> RECIPE_HANDLER_CAPABILITY = AntimatterPlatformUtils.getNewCap();
}
