package muramasa.gregtech.common.fluid;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.GenerationFlag;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GTFluid extends Fluid {

    private String name;
    private GenerationFlag flag;

    public GTFluid(Material mat, GenerationFlag flag) {
        super(mat.getName() + "_" + flag.getName(), new ResourceLocation(Ref.MODID, "blocks/machine/base/liquid"), new ResourceLocation(Ref.MODID, "blocks/machine/base/lv"));
        setColor(mat.getRGB());
        switch (flag) {
            case LIQUID:
                setViscosity(1000);
                break;
            case GAS:
                setViscosity(200);
                setDensity(-100);
                setGaseous(true);
                break;
            case PLASMA:
                setViscosity(10);
                setDensity(55536);
                setLuminosity(15);
                setGaseous(true);
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
}
