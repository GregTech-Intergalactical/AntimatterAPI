package muramasa.antimatter.fluid;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;

/**
 * AntimatterMaterialFluid is an extension of AntimatterFluid that includes both {@link Material} and {@link MaterialType} parameters
 *
 * This allows for straightforward fluid generation derived from base Material values, these are of course overridable with the different constructors still.
 */
public class AntimatterMaterialFluid extends AntimatterFluid {

    protected Material material;
    protected MaterialType<?> type;

    public AntimatterMaterialFluid(String domain, Material material, MaterialType<?> type, FluidAttributes.Builder builder, Block.Properties blockProperties) {
        super(domain, type == MaterialType.LIQUID ? material.getId() : type.getId() + '_' + material.getId(), builder, blockProperties);
        this.material = material;
        this.type = type;
    }

    public AntimatterMaterialFluid(String domain, Material material, MaterialType<?> type, ResourceLocation stillLoc, ResourceLocation flowLoc) {
        super(domain, type == MaterialType.LIQUID ? material.getId() : type.getId() + '_' + material.getId(), stillLoc, flowLoc);
        this.material = material;
        this.type = type;
    }

    public AntimatterMaterialFluid(String domain, Material material, MaterialType<?> type) {
        this(domain, material, type, prepareAttributes(domain, material, type), prepareProperties(type));
    }

    public Material getMaterial() {
        return material;
    }

    public MaterialType<?> getType() {
        return type;
    }

    private static FluidAttributes.Builder prepareAttributes(String domain, Material material, MaterialType<?> type) {
        FluidAttributes.Builder builder = getDefaultAttributesBuilder().color(material.getRGB()).translationKey("block." + domain + type.getId() + "." + material.getId());
        int mass = material.getElement() == null ? 0 : material.getElement().getMass();
        if (type == MaterialType.GAS) {
            return builder.viscosity(200).density(-100 - mass).gaseous().temperature(material.getGasTemperature());
        }
        else if (type == MaterialType.PLASMA) {
            return builder.viscosity(10).density(55536 - mass).luminosity(15).gaseous().temperature(10000);
        }
        else {
            return builder.viscosity(1000).density(1000 + mass).temperature(material.getLiquidTemperature());
        }
    }

    private static Block.Properties prepareProperties(MaterialType<?> type) {
        return getDefaultBlockProperties().lightValue(type == MaterialType.PLASMA ? 15 : 0);
    }

}
