package muramasa.antimatter.fluid;

import earth.terrarium.botarium.common.registry.fluid.FluidProperties;
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

    public AntimatterMaterialFluid(String domain, Material material, MaterialType<?> type, FluidProperties.Builder builder, Block.Properties blockProperties) {
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

    private static FluidProperties.Builder prepareAttributes(String domain, Material material, MaterialType<?> type) {
        if (type == AntimatterMaterialTypes.GAS) {
            return FluidProperties.create().still(GAS_TEXTURE).flowing(GAS_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE).tintColor((70 << 24) | (material.getRGB() & 0x00ffffff))
                    .viscosity(200).density(-1000).supportsBloating(true).temperature(MaterialTags.GAS_TEMPERATURE.getInt(material))
                    .sounds("bucket_fill", SoundEvents.BUCKET_FILL).sounds("bucket_empty", SoundEvents.BUCKET_EMPTY);
                    //.translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
        } else if (type == AntimatterMaterialTypes.PLASMA) {
            return FluidProperties.create().still(PLASMA_TEXTURE).flowing(PLASMA_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE).tintColor((50 << 24) | (material.getRGB() & 0x00ffffff))
                    .viscosity(10).density(-55536).lightLevel(15).supportsBloating(true).temperature(10000)
                    .sounds("bucket_fill", SoundEvents.BUCKET_FILL).sounds("bucket_empty", SoundEvents.BUCKET_EMPTY);
                    //.translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
        } else {
            FluidProperties.Builder b = getDefaultAttributesBuilder(material.has(MaterialTags.MOLTEN));
            if (material.has(MaterialTags.MOLTEN)){
                b = b.density(3000).viscosity(6000).lightLevel(15);
            }
            int alpha = material.has(MaterialTags.MOLTEN) ? 0xFF000000 : (155 << 24);
            return b.tintColor(alpha | (material.getRGB() & 0x00FFFFFF))
                    //.translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
                    .temperature(MaterialTags.LIQUID_TEMPERATURE.getInt(material));
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
        return type == AntimatterMaterialTypes.PLASMA || type == AntimatterMaterialTypes.GAS || this.getAttributes().supportsBloating();
    }
}
