package muramasa.antimatter.fluid.forge;

import earth.terrarium.botarium.common.registry.fluid.BotariumFlowingFluid;
import earth.terrarium.botarium.common.registry.fluid.BotariumSourceFluid;
import earth.terrarium.botarium.common.registry.fluid.FluidData;
import net.minecraft.world.level.material.FlowingFluid;

public class FluidUtilsImpl {
    public static FlowingFluid createSourceFluid(FluidData data){
        return new AntimatterSourceFluid(data);
    }

    public static FlowingFluid createFlowingFluid(FluidData data){
        return new AntimatterFlowingFluid(data);
    }
}
