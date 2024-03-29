package muramasa.antimatter.fluid.forge;

import earth.terrarium.botarium.common.registry.fluid.FluidData;
import earth.terrarium.botarium.forge.regsitry.fluid.ForgeFluidAttributesHandler;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class AntimatterFlowingFluid extends ForgeFlowingFluid.Flowing {
    private final FluidData data;

    public AntimatterFlowingFluid(FluidData data) {
        super(ForgeFluidAttributesHandler.propertiesFromFluidProperties(data));
        this.data = data;
        registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        data.setFlowingFluid(() -> this);
    }

    public FluidData getData() {
        return data;
    }
}
