package muramasa.gtu.api.fluid;

import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialTag;
import muramasa.gtu.api.materials.MaterialType;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import static muramasa.gtu.api.materials.MaterialType.*;

public class GTFluid extends Fluid {

    private Material material;
    private MaterialType type;
    private String localizedName;

    public GTFluid(Material material, MaterialType type) {
        super(material.getId() + "_" + type.getId(), material.getSet().getTexture(type, 0), material.getSet().getTexture(type, 0));
        setColor(material.getRGB());
        if (type == LIQUID) {
            setViscosity(1000);
            setTemperature(material.getLiquidTemperature());
        } else if (type == GAS) {
            setViscosity(200);
            setDensity(-100);
            setGaseous(true);
            setTemperature(material.getGasTemperature());
        } else if (type == PLASMA) {
            setViscosity(10);
            setDensity(55536);
            setLuminosity(15);
            setGaseous(true);
            setTemperature(10000);
        } else {
            throw new IllegalArgumentException("Cannot create a fluid with the type: " + type.getId());
        }
        this.material = material;
        this.type = type;
        FluidRegistry.registerFluid(this);
    }

    @Override
    public String getLocalizedName(FluidStack stack) {
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
