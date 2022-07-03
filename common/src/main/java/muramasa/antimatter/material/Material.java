package muramasa.antimatter.material;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraftforge.fluids.FluidStack;
import tesseract.Tesseract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.material.MaterialTags.ARMOR;
import static muramasa.antimatter.material.MaterialTags.HANDLE;
import static muramasa.antimatter.material.MaterialTags.METAL;

public class Material implements ISharedAntimatterObject {

    /**
     * Basic Members
     **/
    private final String domain;
    private final String id;
    private Component displayName;
    private final int rgb;
    private final TextureSet set;

    /**
     * Element Members
     **/
    private Element element;
    private String chemicalFormula = null;

    public final boolean enabled;

    public Material(String domain, String id, int rgb, TextureSet set, String... modIds) {
        this.domain = domain;
        this.id = id;
        this.rgb = rgb;
        this.set = set;
        MaterialTags.ORE_MULTI.add(this, 1);
        MaterialTags.SMELTING_MULTI.add(this, 1);
        MaterialTags.BY_PRODUCT_MULTI.add(this, 1);
        MaterialTags.SMELT_INTO.add(this, this);
        MaterialTags.DIRECT_SMELT_INTO.add(this, this);
        MaterialTags.ARC_SMELT_INTO.add(this, this);
        MaterialTags.MACERATE_INTO.add(this, this);
        MaterialTags.PROCESS_INTO.add(this, new ObjectArrayList<>());
        MaterialTags.BYPRODUCTS.add(this, new ObjectArrayList<>());
        if (modIds != null && modIds.length > 0) {
            for (String modId : modIds) {
                if (!AntimatterAPI.isModLoaded(modId)) {
                    enabled = false;
                    return;
                }
            }
        }
        enabled = true;
    }

    public String materialDomain() {
        return domain;
    }

    @Override
    public boolean shouldRegister() {
        return enabled;
    }

    public Material(String domain, String id, int rgb, TextureSet set, Element element, String... modIds) {
        this(domain, id, rgb, set, modIds);
        this.element = element;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return Utils.lowerUnderscoreToUpperSpaced(getId());
    }

    public Material asDust(IMaterialTag... tags) {
        return asDust(295, tags);
    }

    public Material asDust(int meltingPoint, IMaterialTag... tags) {
        flags(DUST, DUST_SMALL, DUST_TINY);
        flags(tags);
        MaterialTags.MELTING_POINT.add(this, meltingPoint);
        if (meltingPoint > 295) {
//            asFluid();//TODO disabled due to Sodium having a fluid
        }
        return this;
    }

    public Material asSolid(IMaterialTag... tags) {
        return asSolid(295, 0, tags);
    }

    public Material asSolid(int meltingPoint, int blastFurnaceTemp, IMaterialTag... tags) {
        asDust(meltingPoint, tags);
        flags(INGOT, NUGGET, BLOCK).asFluid(); //TODO: Shall we generate blocks for every solid?
        MaterialTags.BLAST_FURNACE_TEMP.add(this, blastFurnaceTemp);
        if (blastFurnaceTemp >= 1000){
            flags(MaterialTags.NEEDS_BLAST_FURNACE);
        }
        if (blastFurnaceTemp > 1750) {
            flags(INGOT_HOT);
        }
        return this;
    }

    public Material asMetal(IMaterialTag... tags) {
        return asMetal(295, 0, tags);
    }

    public Material asMetal(int meltingPoint, int blastFurnaceTemp, IMaterialTag... tags) {
        asSolid(meltingPoint, blastFurnaceTemp, tags);
        flags(METAL);
        return this;
    }

    public Material asOre(int minXp, int maxXp, boolean small, IMaterialTag... tags) {
        MaterialTags.EXP_RANGE.add(this, UniformInt.of(minXp, maxXp));
        return asOre(small, tags);
    }

    public Material asOre(IMaterialTag... tags) {
        return asOre(true, tags);
    }

    public Material asOre(boolean small, IMaterialTag... tags) {
        asDust(ORE, ROCK, CRUSHED, CRUSHED_PURIFIED, CRUSHED_CENTRIFUGED, DUST_IMPURE, DUST_PURE, RAW_ORE);
        if (small) flags(ORE_SMALL);
        flags(tags);
        return this;
    }

    public Material asOreStone(int minXp, int maxXp, IMaterialTag... tags) {
        asOre(minXp, maxXp, false, tags);
        flags(ORE_STONE);
        return this;
    }

    public Material asOreStone(IMaterialTag... tags) {
        asOre(tags);
        asDust(ORE_STONE, ORE, ROCK, CRUSHED, CRUSHED_PURIFIED, CRUSHED_CENTRIFUGED, DUST_IMPURE, DUST_PURE);
        flags(tags);
        return this;
    }

    public Material asGemBasic(boolean transparent, IMaterialTag... tags) {
        asDust(tags);
        flags(GEM, BLOCK);
        if (transparent) {
            flags(MaterialTags.TRANSPARENT, PLATE, LENS, GEM_BRITTLE, GEM_POLISHED);
        }
        return this;
    }

    public Material asGem(boolean transparent, IMaterialTag... tags) {
        asGemBasic(transparent, tags);
        if (!transparent) flags(GEM_BRITTLE, GEM_POLISHED);
        return this;
    }

    public Material asFluid() {
        return asFluid(0);
    }

    public Material asFluid(int fuelPower) {
        int meltingPoint = this.has(MaterialTags.MELTING_POINT) ? MaterialTags.MELTING_POINT.getInt(this) : 295;
        return asFluid(fuelPower, Math.max(meltingPoint, 295));
    }

    public Material asFluid(int fuelPower, int temp) {
        flags(LIQUID);
        MaterialTags.FUEL_POWER.add(this, fuelPower);
        MaterialTags.LIQUID_TEMPERATURE.add(this, temp);
        return this;
    }

    public Material asGas() {
        return asGas(0);
    }

    public Material asGas(int fuelPower) {
        int meltingPoint = this.has(MaterialTags.MELTING_POINT) ? MaterialTags.MELTING_POINT.getInt(this) : 295;
        return asGas(fuelPower, Math.max(meltingPoint, 295));
    }

    public Material asGas(int fuelPower, int temp) {
        flags(GAS);
        MaterialTags.FUEL_POWER.add(this, fuelPower);
        MaterialTags.GAS_TEMPERATURE.add(this, temp);
        return this;
    }

    public Material asPlasma() {
        return asPlasma(0);
    }

    public Material asPlasma(int fuelPower) {
        asGas(fuelPower);
        flags(PLASMA);
        return this;
    }

    public Material harvestLevel(int harvestLevel) {
        MaterialTags.MINING_LEVEL.add(this, harvestLevel);
        return this;
    }

    public Material addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality) {
        return addTools(toolDamage, toolSpeed, toolDurability, toolQuality, ImmutableMap.of());
    }

    public Material addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality, ImmutableMap<Enchantment, Integer> toolEnchantment, AntimatterToolType... toolTypes) {
        if (has(INGOT))
            flags(PLATE, ROD, SCREW, BOLT); //TODO: We need to add bolt for now since screws depends on bolt, need to find time to change it
        else flags(ROD);
        List<AntimatterToolType> toolTypesList = toolTypes.length > 0 ? Arrays.asList(toolTypes) : AntimatterAPI.all(AntimatterToolType.class);
        MaterialTags.TOOLS.add(this, new ToolMaterialTag.ToolData(toolDamage, toolSpeed, toolDurability, toolQuality, toolEnchantment, toolTypesList));
        MaterialTags.MINING_LEVEL.add(this, toolQuality - 1);
        if (toolTypesList.contains(ELECTRIC_WRENCH)) flags(WRENCHBIT);
        if (toolTypesList.contains(BUZZSAW)) flags(BUZZSAW_BLADE);
        if (toolTypesList.contains(DRILL)) flags(DRILLBIT);
        if (toolTypesList.contains(CHAINSAW)) flags(CHAINSAWBIT);
        return this;
    }

    public Material addTools(Material derivedMaterial, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        ToolMaterialTag.ToolData data = MaterialTags.TOOLS.getToolData(derivedMaterial);
        return addTools(data.toolDamage(), data.toolSpeed(), data.toolDurability(), data.toolQuality(), toolEnchantment);
    }

    public Material addTools(Material derivedMaterial) {
        ToolMaterialTag.ToolData data = MaterialTags.TOOLS.getToolData(derivedMaterial);
        return addTools(data.toolDamage(), data.toolSpeed(), data.toolDurability(), data.toolQuality());
    }

    public Material setAllowedTypes(AntimatterToolType... toolTypes) {
        if (!has(MaterialTags.TOOLS)) return this;
        ToolMaterialTag.ToolData data = MaterialTags.TOOLS.getToolData(this);
        List<AntimatterToolType> toolTypesList = toolTypes.length > 0 ? Arrays.asList(toolTypes) : AntimatterAPI.all(AntimatterToolType.class);
        MaterialTags.TOOLS.add(this, new ToolMaterialTag.ToolData(data.toolDamage(), data.toolSpeed(), data.toolDurability(), data.toolQuality(), data.toolEnchantment(), toolTypesList));
        return this;
    }

    public Material addArmor(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor) {
        return addArmor(armor, toughness, knockbackResistance, armorDurabilityFactor, ImmutableMap.of());
    }

    public Material addArmor(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (armor.length != 4) {
            Antimatter.LOGGER.info("Material " + this.getId() + " unable to add armor, protection array must have exactly 4 values");
            return this;
        }
        if (has(INGOT)) flags(PLATE);
        MaterialTags.ARMOR.add(this, new ArmorMaterialTag.ArmorData(armor, toughness, knockbackResistance, armorDurabilityFactor, toolEnchantment));
        return this;
    }

    public Material addArmor(Material material, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (!material.has(ARMOR)) return this;
        ArmorMaterialTag.ArmorData data = ARMOR.getArmorData(material);
        return addArmor(data.armor(), data.toughness(), data.knockbackResistance(), data.armorDurabilityFactor(), toolEnchantment);
    }

    public Material addArmor(Material material) {
        if (!material.has(ARMOR)) return this;
        ArmorMaterialTag.ArmorData data = ARMOR.getArmorData(material);
        return addArmor(data.armor(), data.toughness(), data.knockbackResistance(), data.armorDurabilityFactor());
    }

    public Material addHandleStat(int durability, float speed) {
        return addHandleStat(durability, speed, ImmutableMap.of());
    }

    public Material addHandleStat(int durability, float speed, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (!has(ROD)) flags(ROD);
        HANDLE.add(this, new HandleMaterialTag.HandleData(durability, speed, toolEnchantment));
        return this;
    }

    public boolean has(IMaterialTag... tags) {
        for (IMaterialTag t : tags) {
            if (!t.all().contains(this)) return false;
        }
        return true;
    }

    public Material flags(IMaterialTag... tags) {
        if (!enabled) return this;
        for (IMaterialTag t : tags) {
            if (!this.has(t)) {
                t.add(this);
            }
            flags(t.dependents().stream().filter(d -> !this.has(d)).toArray(IMaterialTag[]::new));
        }
        return this;
    }

    public Material setExpRange(UniformInt expRange) {
        MaterialTags.EXP_RANGE.add(this, expRange);
        return this;
    }

    public Material setExpRange(int min, int max) {
        return this.setExpRange(UniformInt.of(min, max));
    }

    public void remove(IMaterialTag... tags) {
        if (!enabled) return;
        for (IMaterialTag t : tags) {
            t.remove(this);
        }
    }

    public Material mats(Function<ImmutableMap.Builder<Material, Integer>, ImmutableMap.Builder<Material, Integer>> func) {
        if (!enabled) return this;
        return mats(func.apply(new ImmutableMap.Builder<>()).build());
    }

    public Material mats(ImmutableMap<Material, Integer> stacks) {
        if (!enabled) return this;
        stacks.forEach((k, v) -> MaterialTags.PROCESS_INTO.add(this, new MaterialStack(k, v)));
        return this;
    }

    public void setChemicalFormula() {
        if (!enabled) return;
        if (chemicalFormula != null) return;
        if (element != null) chemicalFormula = element.getElement();
        else if (!MaterialTags.PROCESS_INTO.getList(this).isEmpty()) {
            MaterialTags.PROCESS_INTO.getList(this).forEach(t -> t.m.setChemicalFormula());
            chemicalFormula = String.join("", MaterialTags.PROCESS_INTO.getList(this).stream().map(MaterialStack::toString).collect(Collectors.joining()));
        }
    }

    /**
     * Basic Getters
     **/
    public Component getDisplayName() {
        return displayName == null ? displayName = new TranslatableComponent("material." + getId()) : displayName;
    }

    public int getRGB() {
        return rgb;
    }

    public TextureSet getSet() {
        return set;
    }

    public long getDensity() {
        return Ref.U;
    }

    public long getProtons() {
        if (element != null) return element.getProtons();
        if (MaterialTags.PROCESS_INTO.getList(this).size() <= 0) return Element.Tc.getProtons();
        long rAmount = 0, tAmount = 0;
        for (MaterialStack stack : MaterialTags.PROCESS_INTO.getList(this)) {
            tAmount += stack.s;
            rAmount += stack.s * stack.m.getProtons();
        }
        return (getDensity() * rAmount) / (tAmount * Ref.U);
    }

    public long getNeutrons() {
        if (element != null) return element.getNeutrons();
        if (MaterialTags.PROCESS_INTO.getList(this).size() <= 0) return Element.Tc.getNeutrons();
        long rAmount = 0, tAmount = 0;
        for (MaterialStack stack : MaterialTags.PROCESS_INTO.getList(this)) {
            tAmount += stack.s;
            rAmount += stack.s * stack.m.getNeutrons();
        }
        return (getDensity() * rAmount) / (tAmount * Ref.U);
    }

    public long getMass() {
        if (element != null) return element.getMass();
        if (MaterialTags.PROCESS_INTO.getList(this).size() <= 0) return Element.Tc.getMass();
        long rAmount = 0, tAmount = 0;
        for (MaterialStack stack : MaterialTags.PROCESS_INTO.getList(this)) {
            tAmount += stack.s;
            rAmount += stack.s * stack.m.getMass();
        }
        return (getDensity() * rAmount) / (tAmount * Ref.U);
    }

    /**
     * Element Getters
     **/
    public Element getElement() {
        return element;
    }

    public String getChemicalFormula() {
        return chemicalFormula == null ? "" : chemicalFormula;
    }

    /**
     * Fluid/Gas/Plasma Getters
     **/
    public Fluid getLiquid() {
        return LIQUID.get().get(this, 1).getFluid();
    }

    public Fluid getGas() {
        return GAS.get().get(this, 1).getFluid();
    }

    public Fluid getPlasma() {
        return PLASMA.get().get(this, 1).getFluid();
    }

    public FluidStack getLiquid(long droplets) {
        return LIQUID.get().get(this, droplets);
    }

    public FluidStack getGas(long droplets) {
        return GAS.get().get(this, droplets);
    }

    public FluidStack getPlasma(long droplets) {
        return PLASMA.get().get(this, droplets);
    }

    public FluidStack getLiquid(int mb) {
        return this.getLiquid(mb * Tesseract.dropletMultiplier);
    }

    public FluidStack getGas(int mb) {
        return this.getGas(mb * Tesseract.dropletMultiplier);
    }

    public FluidStack getPlasma(int mb) {
        return this.getPlasma(mb * Tesseract.dropletMultiplier);
    }

    /**
     * Processing Getters/Setters
     **/

    public Material setOreMulti(int multi) {
        MaterialTags.ORE_MULTI.add(this, multi);
        return this;
    }

    public Material setSmeltingMulti(int multi) {
        MaterialTags.SMELTING_MULTI.add(this, multi);
        return this;
    }

    public Material setByProductMulti(int multi) {
        MaterialTags.BY_PRODUCT_MULTI.add(this, multi);
        return this;
    }

    public Material setSmeltInto(Material m) {
        MaterialTags.SMELT_INTO.add(this, m);
        return this;
    }

    public Material setDirectSmeltInto(Material m) {
        MaterialTags.DIRECT_SMELT_INTO.add(this, m);
        return this;
    }

    public Material setArcSmeltInto(Material m) {
        MaterialTags.ARC_SMELT_INTO.add(this, m);
        return this;
    }

    public Material setMacerateInto(Material m) {
        MaterialTags.MACERATE_INTO.add(this, m);
        return this;
    }

    public List<MaterialStack> getProcessInto() {
        return MaterialTags.PROCESS_INTO.getList(this);
    }

    public List<Material> getByProducts() {
        return MaterialTags.BYPRODUCTS.getList(this);
    }

    public boolean hasByProducts() {
        return MaterialTags.BYPRODUCTS.getList(this).size() > 0;
    }

    public Material addByProduct(Material... mats) {
        MaterialTags.BYPRODUCTS.getList(this).addAll(Arrays.asList(mats));
        return this;
    }

    public ItemStack getCell(int amount, ItemFluidCell cell) {
        return Utils.ca(amount, cell.fill(getLiquid()));
        // return ItemStack.EMPTY;
    }

    public ItemStack getCellGas(int amount, ItemFluidCell cell) {
        return Utils.ca(amount, cell.fill(getGas()));
        //return ItemStack.EMPTY;
    }

    public ItemStack getCellPlasma(int amount, ItemFluidCell cell) {
        return Utils.ca(amount, cell.fill(getPlasma()));
        //return ItemStack.EMPTY;
    }

    public static Material get(String id) {
        Material material = AntimatterAPI.get(Material.class, id);
        return material == null ? Data.NULL : material;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
