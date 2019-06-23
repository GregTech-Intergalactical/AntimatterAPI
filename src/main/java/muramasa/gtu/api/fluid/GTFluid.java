package muramasa.gtu.api.fluid;

import muramasa.gtu.Ref;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.RecipeFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import static muramasa.gtu.api.materials.MaterialType.*;

public class GTFluid extends Fluid {

    private Material material;
    private MaterialType flag;
    private String localizedName;

    public GTFluid(Material material, MaterialType flag) {
        super(material.getId() + "_" + flag.getName(), new ResourceLocation(Ref.MODID, "blocks/fluid/" + flag.getName() + "_still"), new ResourceLocation(Ref.MODID, "blocks/fluid/" + flag.getName() + "_still"));
        setColor(material.getRGB());
        if (flag == LIQUID) {
            setViscosity(1000);
            setTemperature(material.getLiquidTemperature());
        } else if (flag == GAS) {
            setViscosity(200);
            setDensity(-100);
            setGaseous(true);
            setTemperature(material.getGasTemperature());
        } else if (flag == PLASMA) {
            setViscosity(10);
            setDensity(55536);
            setLuminosity(15);
            setGaseous(true);
            setTemperature(10000);
        } else {
            throw new IllegalArgumentException("Cannot create a fluid with the flag: " + flag.getName());
        }
        this.material = material;
        this.flag = flag;
        FluidRegistry.registerFluid(this);
    }

    @Override
    public String getLocalizedName(FluidStack stack) {
        if (localizedName == null) {
            if (flag == LIQUID) localizedName = (material.has(RecipeFlag.METAL) ? "Molten " : "Liquid ") + material.getId();
            else if (flag == GAS) localizedName = material.getDisplayName() + " Gas";
            else if (flag == PLASMA) localizedName = material.getDisplayName() + " Plasma";
            else localizedName = "FLUID NAME ERROR";
        }
        return localizedName;
    }

    public String getState() {
        if (flag == LIQUID) return "Liquid";
        else if (flag == GAS) return "Gas";
        else if (flag == PLASMA) return "Plasma";
        else return "State Unknown";
    }
}
