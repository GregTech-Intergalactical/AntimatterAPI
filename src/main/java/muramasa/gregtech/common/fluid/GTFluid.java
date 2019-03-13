package muramasa.gregtech.common.fluid;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.materials.ItemFlag;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.Ref;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GTFluid extends Fluid {

    private String name;
    private ItemFlag flag;

    public GTFluid(Material mat, ItemFlag flag) {
        super(mat.getName() + "_" + flag.getName(), new ResourceLocation(Ref.MODID, "blocks/machine/base/liquid"), new ResourceLocation(Ref.MODID, "blocks/machine/base/lv"));
        setColor(mat.getRGB());
        switch (flag) {
            case LIQUID:
                setViscosity(1000);
                setTemperature(mat.getMeltingPoint() <= 0 ? 1000 : mat.getMeltingPoint());
                break;
            case GAS:
                setViscosity(200);
                setDensity(-100);
                setGaseous(true);
                setTemperature(mat.getMeltingPoint() <= 0 ? 1000 : mat.getMeltingPoint());
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
        name = mat.getName();
        this.flag = flag;
        FluidRegistry.registerFluid(this);
    }

    @Override
    public String getLocalizedName(FluidStack stack) {
        switch (flag) {
            case LIQUID:
                return "Molten " + Materials.get(name).getDisplayName();
            case GAS:
                return Materials.get(name).getDisplayName() + " Gas";
            case PLASMA:
                return Materials.get(name).getDisplayName() + " Plasma";
            default:
                return "FLUID NAME ERROR";
        }
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
