package muramasa.antimatter.fluid.forge;

import earth.terrarium.botarium.common.registry.fluid.FluidData;
import muramasa.antimatter.fluid.FluidUtils;
import net.minecraft.world.level.material.FlowingFluid;

public class FluidUtilsImpl implements FluidUtils {
    public FlowingFluid createSourceFluid(FluidData data){
        return new AntimatterSourceFluid(data);
    }

    public FlowingFluid createFlowingFluid(FluidData data){
        return new AntimatterFlowingFluid(data);
    }
}
