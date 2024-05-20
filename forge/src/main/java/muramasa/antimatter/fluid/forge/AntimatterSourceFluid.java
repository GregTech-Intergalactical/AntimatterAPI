package muramasa.antimatter.fluid.forge;

import earth.terrarium.botarium.common.registry.fluid.FluidData;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class AntimatterSourceFluid extends ForgeFlowingFluid.Source {
    private final FluidData data;

    public AntimatterSourceFluid(FluidData data) {
        super(FluidUtilsImpl.propertiesFromFluidProperties(data));
        this.data = data;
        data.setStillFluid(() -> this);
    }

    public FluidData getData() {
        return data;
    }
}
