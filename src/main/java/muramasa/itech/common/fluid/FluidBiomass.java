package muramasa.itech.common.fluid;

import muramasa.itech.ITech;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidBiomass extends Fluid {

    public FluidBiomass() {
        super("biomass", new ResourceLocation(ITech.MODID, "textures/blocks/fluidStack/biomass"), new ResourceLocation(ITech.MODID, "textures/blocks/fluidStack/biomass"));
        FluidRegistry.registerFluid(this);
    }
}
