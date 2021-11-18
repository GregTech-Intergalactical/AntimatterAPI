package muramasa.antimatter.fluid;

import muramasa.antimatter.Data;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;

/**
 * AntimatterMaterialFluid is an extension of AntimatterFluid that includes both {@link Material} and {@link MaterialType} parameters
 * <p>
 * This allows for straightforward fluid generation derived from base Material values, these are of course overridable with the different constructors still.
 */
public class AntimatterMaterialFluid extends AntimatterFluid {

    protected Material material;
    protected MaterialType<?> type;

    public AntimatterMaterialFluid(String domain, Material material, MaterialType<?> type, FluidAttributes.Builder builder, Block.Properties blockProperties) {
        super(domain, type.getId() + "_" + material.getId(), builder, blockProperties);
        this.material = material;
        this.type = type;
    }

    public AntimatterMaterialFluid(String domain, Material material, MaterialType<?> type, ResourceLocation stillLoc, ResourceLocation flowLoc) {
        super(domain, type.getId() + "_" + material.getId(), stillLoc, flowLoc);
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
        if (type == Data.GAS) {
            return FluidAttributes.builder(GAS_TEXTURE, GAS_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE).color((70 << 24) | (material.getRGB() & 0x00ffffff))
                    .translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
                    .viscosity(200).density(-1000).gaseous().temperature(material.getGasTemperature());
        } else if (type == Data.PLASMA) {
            return FluidAttributes.builder(PLASMA_TEXTURE, PLASMA_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE).color((50 << 24) | (material.getRGB() & 0x00ffffff))
                    .translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
                    .viscosity(10).density(-55536).luminosity(15).gaseous().temperature(10000);
        } else {
            return getDefaultAttributesBuilder(material.getLiquidTemperature() >= 400).color((155 << 24) | (material.getRGB() & 0x00ffffff))
                    .translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
                    .viscosity(1000).density(1000).temperature(material.getLiquidTemperature());
        }
    }

    private static Block.Properties prepareProperties(MaterialType<?> type) {
        return getDefaultBlockProperties().lightLevel(s -> type == Data.PLASMA ? 15 : 0);
    }
}
