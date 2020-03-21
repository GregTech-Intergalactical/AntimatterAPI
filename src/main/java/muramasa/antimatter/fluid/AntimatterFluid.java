package muramasa.antimatter.fluid;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTag;
import muramasa.antimatter.material.MaterialType;
import net.minecraft.fluid.EmptyFluid;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidAttributes;

import static muramasa.antimatter.material.MaterialType.*;

public class AntimatterFluid extends EmptyFluid {

    private Material material;
    private MaterialType<?> type;
    private String localizedName;

    public AntimatterFluid(Material material, MaterialType<?> type) {
        this.material = material;
        this.type = type;
//        FluidRegistry.registerFluid(this);
    }

    @Override
    protected FluidAttributes createAttributes() {
        FluidAttributes.Builder builder = FluidAttributes.builder(material.getSet().getTexture(type, 0), material.getSet().getTexture(type, 0)).color(material.getRGB());
        if (type == LIQUID) {
            return builder.viscosity(1000).temperature(material.getLiquidTemperature()).build(this);
        } else if (type == GAS) {
            return builder.viscosity(200).density(-100).gaseous().temperature(material.getGasTemperature()).build(this);
        } else if (type == PLASMA) {
            return builder.viscosity(10).density(55536).luminosity(15).gaseous().temperature(10000).build(this);
        } else {
            return ForgeHooks.createVanillaFluidAttributes(this);
        }
    }

//    @Override
//    public BlockRenderLayer getRenderLayer() {
//        return BlockRenderLayer.TRANSLUCENT;
//    }

    public String getLocalizedName() {
        //TODO localize
        if (localizedName == null) {
            if (type == LIQUID) localizedName = (material.has(MaterialTag.METAL) ? "Molten " : "Liquid ") + material.getId();
            else if (type == GAS) localizedName = material.getDisplayName() + " Gas";
            else if (type == PLASMA) localizedName = material.getDisplayName() + " Plasma";
            else localizedName = "FLUID NAME ERROR";
        }
        return localizedName;
    }

    public String getState() {
        if (type == LIQUID) return "Liquid";
        else if (type == GAS) return "Gas";
        else if (type == PLASMA) return "Plasma";
        else return "State Unknown";
    }
}
