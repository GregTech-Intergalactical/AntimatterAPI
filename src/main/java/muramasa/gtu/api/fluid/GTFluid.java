package muramasa.gtu.api.fluid;

import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.ItemFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.Ref;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GTFluid extends Fluid {

    private String name;
    private ItemFlag flag;

    public GTFluid(Material mat, ItemFlag flag) {
        super(mat.getName() + "_" + flag.getName(), new ResourceLocation(Ref.MODID, "blocks/fluid/" + flag.getName() + "_still"), new ResourceLocation(Ref.MODID, "blocks/fluid/" + flag.getName() + "_still"));
        setColor(mat.getRGB());
        switch (flag) {
            case GENERATE_LIQUID:
                setViscosity(1000);
                setTemperature(mat.getMeltingPoint() <= 0 ? 1000 : mat.getMeltingPoint());
                break;
            case GENERATE_GAS:
                setViscosity(200);
                setDensity(-100);
                setGaseous(true);
                setTemperature(mat.getMeltingPoint() <= 0 ? 1000 : mat.getMeltingPoint());
                break;
            case GENERATE_PLASMA:
                setViscosity(10);
                setDensity(55536);
                setLuminosity(15);
                setGaseous(true);
                setTemperature(10000);
                break;
            default:
                throw new IllegalArgumentException("Cannot create a fluid with the flag: " + flag.getName());
        }
        name = mat.getName();
        this.flag = flag;
        FluidRegistry.registerFluid(this);
    }

    @Override
    public String getLocalizedName(FluidStack stack) {
        switch (flag) {
            case GENERATE_LIQUID:
                return "Molten " + Materials.get(name).getDisplayName();
            case GENERATE_GAS:
                return Materials.get(name).getDisplayName() + " Gas";
            case GENERATE_PLASMA:
                return Materials.get(name).getDisplayName() + " Plasma";
            default:
                return "FLUID NAME ERROR";
        }
    }

    public String getState() {
        switch (flag) {
            case GENERATE_LIQUID: return "Liquid";
            case GENERATE_GAS: return "Gas";
            case GENERATE_PLASMA: return "Plasma";
            default: return "";
        }
    }
}
