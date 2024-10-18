package muramasa.antimatter.fluid.fabric;

import earth.terrarium.botarium.common.registry.fluid.BotariumFlowingFluid;
import earth.terrarium.botarium.common.registry.fluid.BotariumSourceFluid;
import earth.terrarium.botarium.common.registry.fluid.FluidData;
import muramasa.antimatter.fluid.FluidUtils;
import net.minecraft.world.level.material.FlowingFluid;

public class FluidUtilsImpl implements FluidUtils {
    public FlowingFluid createSourceFluid(FluidData data){
        return new BotariumSourceFluid(data);
    }

    public FlowingFluid createFlowingFluid(FluidData data){
        return new BotariumFlowingFluid(data);
    }
}
