package muramasa.gregtech.common.fluid;

import muramasa.gregtech.api.enums.GenerationFlag;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class GTFluid extends Fluid {

    public GTFluid(Material mat, GenerationFlag flag) {
        super(mat.getName() + "_" + flag.getName(), new ResourceLocation(Ref.MODID, "blocks/machines/base/liquid"), new ResourceLocation(Ref.MODID, "blocks/machines/base/liquid"));
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
        FluidRegistry.registerFluid(this);
    }
}
