package muramasa.antimatter.fluid;

import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.util.Utils;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;

/**
 * AntimatterMaterialFluid is an extension of AntimatterFluid that includes both {@link Material} and {@link MaterialType} parameters
 * <p>
 * This allows for straightforward fluid generation derived from base Material values, these are of course overridable with the different constructors still.
 */
public class AntimatterMaterialFluid extends AntimatterFluid {

    protected Material material;
    protected MaterialType<?> type;

    public AntimatterMaterialFluid(String domain, Material material, MaterialType<?> type, AntimatterFluidAttributes.Builder builder, Block.Properties blockProperties) {
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

    private static AntimatterFluidAttributes.Builder prepareAttributes(String domain, Material material, MaterialType<?> type) {
        if (type == AntimatterMaterialTypes.GAS) {
            return AntimatterFluidAttributes.builder(GAS_TEXTURE, GAS_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE).color((70 << 24) | (material.getRGB() & 0x00ffffff))
                    .translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
                    .viscosity(200).density(-1000).gaseous().temperature(MaterialTags.GAS_TEMPERATURE.getInt(material)).sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
        } else if (type == AntimatterMaterialTypes.PLASMA) {
            return AntimatterFluidAttributes.builder(PLASMA_TEXTURE, PLASMA_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE).color((50 << 24) | (material.getRGB() & 0x00ffffff))
                    .translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
                    .viscosity(10).density(-55536).luminosity(15).gaseous().temperature(10000).sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
        } else {
            return getDefaultAttributesBuilder(material.has(MaterialTags.MOLTEN)).color((155 << 24) | (material.getRGB() & 0x00ffffff))
                    .translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
                    .viscosity(1000).density(1000).temperature(MaterialTags.LIQUID_TEMPERATURE.getInt(material));
        }
    }

    private static Block.Properties prepareProperties(MaterialType<?> type) {
        return getDefaultBlockProperties().lightLevel(s -> type == AntimatterMaterialTypes.PLASMA ? 15 : 0);
    }


    @Override
    public String getLang(String lang) {
        if (lang.equals(Language.DEFAULT)) {
            String display = material.getDisplayNameString() != null && !material.getDisplayNameString().isEmpty() ? material.getDisplayNameString() : Utils.lowerUnderscoreToUpperSpaced(material.getId());
            if (isGasType()) {
                String gas = getType() == AntimatterMaterialTypes.PLASMA ? " Plasma" : "";
                return display + gas;
            }
            String liquid = material.has(MaterialTags.MOLTEN) ? "Molten " : "";
            return liquid + display;
        }
        return super.getLang(lang);
    }

    private boolean isGasType(){
        return type == AntimatterMaterialTypes.PLASMA || type == AntimatterMaterialTypes.GAS || this.getAttributes().isGaseous();
    }
}
