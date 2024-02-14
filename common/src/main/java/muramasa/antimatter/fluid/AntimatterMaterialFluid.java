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

import static muramasa.antimatter.material.MaterialTags.MOLTEN;

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
        this(domain, material, type, prepareAttributes(domain, material, type), prepareProperties(material));
    }

    public Material getMaterial() {
        return material;
    }

    public MaterialType<?> getType() {
        return type;
    }

    private static FluidProperties.Builder prepareAttributes(String domain, Material material, MaterialType<?> type) {
        int density = material.has(MaterialTags.FLUID_DENSITY) ? MaterialTags.FLUID_DENSITY.getInt(material) : type == AntimatterMaterialTypes.GAS ? -1000 : material.has(MOLTEN) ? 3000 : 1000;
        if (type == AntimatterMaterialTypes.GAS) {
            return FluidProperties.create().still(GAS_TEXTURE).flowing(GAS_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE).tintColor((70 << 24) | (material.getRGB() & 0x00ffffff))
                    .viscosity(200).density(density).supportsBloating(true).temperature(MaterialTags.GAS_TEMPERATURE.getInt(material))
                    .sounds("bucket_fill", SoundEvents.BUCKET_FILL).sounds("bucket_empty", SoundEvents.BUCKET_EMPTY);
                    //.translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
        } else {
            FluidProperties.Builder b = getDefaultAttributesBuilder(material.has(MOLTEN)).density(density);
            if (material.has(MOLTEN)){
                b = b.viscosity(6000).lightLevel(15);
            }
            int alpha = material.has(MOLTEN) ? 0xFF000000 : (155 << 24);
            return b.tintColor(alpha | (material.getRGB() & 0x00FFFFFF))
                    //.translationKey(String.join("", "block.", domain, type.getId(), ".", material.getId()))
                    .temperature(MaterialTags.LIQUID_TEMPERATURE.getInt(material));
        }
    }

    private static Block.Properties prepareProperties(Material material) {
        return getDefaultBlockProperties().lightLevel(s -> material.has(MOLTEN)? 15 : 0);
    }


    @Override
    public String getLang(String lang) {
        if (lang.equals(Language.DEFAULT)) {
            String display = material.getDisplayNameString() != null && !material.getDisplayNameString().isEmpty() ? material.getDisplayNameString() : Utils.lowerUnderscoreToUpperSpaced(material.getId());
            if (isGasType()) {
                return display;
            }
            String liquid = material.has(MOLTEN) ? "Molten " : "";
            return liquid + display;
        }
        return super.getLang(lang);
    }

    private boolean isGasType(){
        return type == AntimatterMaterialTypes.GAS || this.getAttributes().supportsBloating();
    }
}
