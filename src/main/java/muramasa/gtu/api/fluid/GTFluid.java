package muramasa.gtu.api.fluid;

import muramasa.gtu.Ref;
import muramasa.gtu.api.materials.GenerationFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.RecipeFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GTFluid extends Fluid {

    private Material material;
    private GenerationFlag flag;
    private String localizedName;

    public GTFluid(Material material, GenerationFlag flag) {
        super(material.getName() + "_" + flag.getName(), new ResourceLocation(Ref.MODID, "blocks/fluid/" + flag.getName() + "_still"), new ResourceLocation(Ref.MODID, "blocks/fluid/" + flag.getName() + "_still"));
        setColor(material.getRGB());
        switch (flag) {
            case LIQUID:
                setViscosity(1000);
                setTemperature(material.getLiquidTemperature());
                break;
            case GAS:
                setViscosity(200);
                setDensity(-100);
                setGaseous(true);
                setTemperature(material.getGasTemperature());
                break;
            case PLASMA:
                setViscosity(10);
                setDensity(55536);
                setLuminosity(15);
                setGaseous(true);
                setTemperature(10000);
                break;
            default:
                throw new IllegalArgumentException("Cannot create a fluid with the flag: " + flag.getName());
        }
        this.material = material;
        this.flag = flag;
        FluidRegistry.registerFluid(this);
    }

    @Override
    public String getLocalizedName(FluidStack stack) {
        if (localizedName == null) {
            switch (flag) {
                case LIQUID:
                    localizedName = (material.has(RecipeFlag.METAL) ? "Molten " : "Liquid ") + material.getName();
                case GAS:
                    localizedName = material.getDisplayName() + " Gas";
                case PLASMA:
                    localizedName = material.getDisplayName() + " Plasma";
                default:
                    return "FLUID NAME ERROR";
            }
        }
        return localizedName;
    }

    public String getState() {
        switch (flag) {
            case LIQUID: return "Liquid";
            case GAS: return "Gas";
            case PLASMA: return "Plasma";
            default: return "";
        }
    }
}
