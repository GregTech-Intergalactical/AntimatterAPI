package muramasa.antimatter.materials;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import static muramasa.antimatter.materials.MaterialTag.METAL;
import static muramasa.antimatter.materials.MaterialType.*;

public class Material implements IAntimatterObject {

    private int hash;

    /** Basic Members **/
    private String domain, id;
    private ITextComponent displayName;
    private int rgb;
    private TextureSet set;

    /** Element Members **/
    private Element element;
    private String chemicalFormula = "";

    /** Solid Members **/
    private int meltingPoint, blastFurnaceTemp;
    private boolean needsBlastFurnace;

    /** Gem Members **/
    private boolean transparent;

    /** Fluid/Gas/Plasma Members **/
    private Fluid liquid, gas, plasma;
    private int fuelPower, liquidTemperature, gasTemperature;
    
    /** Tool Members **/
    private float toolDamage, toolSpeed;
    private int toolDurability, toolQuality;
    private boolean isHandle;
    private int handleDurability;
    private float handleSpeed;
    private ImmutableMap<Enchantment, Integer> toolEnchantment;

    /** Processing Members **/
    private int oreMulti = 1, smeltingMulti = 1, byProductMulti = 1;
    private Material smeltInto, directSmeltInto, arcSmeltInto, macerateInto;
    private ArrayList<MaterialStack> processInto = new ArrayList<>();
    private ArrayList<Material> byProducts = new ArrayList<>();

    public Material(String domain, String id, int rgb, TextureSet set) {
        this.domain = domain;
        this.id = id;
        this.hash = id.hashCode();
        this.rgb = rgb;
        this.set = set;
        this.smeltInto = directSmeltInto = arcSmeltInto = macerateInto = this;
        AntimatterAPI.register(Material.class, this);
    }

    public Material(String domain, String id, int rgb, TextureSet set, Element element) {
        this(domain, id, rgb, set);
        this.element = element;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    public int getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return getId();
    }
    
    public Material asDust(IMaterialTag... tags) {
        return asDust(295, tags);
    }

    public Material asDust(int meltingPoint, IMaterialTag... tags) {
        flags(DUST, DUST_SMALL, DUST_TINY);
        flags(tags);
        this.meltingPoint = meltingPoint;
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
        flags(INGOT, NUGGET, BLOCK, LIQUID); //TODO: Shall we generate blocks for every solid?
        this.blastFurnaceTemp = blastFurnaceTemp;
        this.needsBlastFurnace = blastFurnaceTemp >= 1000;
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

    public Material asGemBasic(boolean transparent, IMaterialTag... tags) {
        asDust(tags);
        flags(GEM, BLOCK);
        if (transparent) {
            this.transparent = true;
            flags(PLATE, LENS, GEM_BRITTLE, GEM_POLISHED);
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
        flags(LIQUID);
        this.fuelPower = fuelPower;
        this.liquidTemperature = Math.max(meltingPoint, 295);
        return this;
    }

    public Material asGas() {
        return asGas(0);
    }

    public Material asGas(int fuelPower) {
        flags(GAS);
        this.gasTemperature = Math.max(meltingPoint, 295);
        this.fuelPower = fuelPower;
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

    //TODO temp method to fix compilation until a GT pr has been made
    public Material addTools(float toolSpeed, int toolDurability, int toolQuality) {
        return addTools(0f, toolSpeed, toolDurability, toolQuality);
    }

    public Material addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality) {
        if (has(INGOT)) flags(TOOLS, PLATE, ROD, SCREW, BOLT); //TODO: We need to add bolt for now since screws depends on bolt, need to find time to change it
        else flags(TOOLS, ROD);
        this.toolDamage = toolDamage;
        this.toolSpeed = toolSpeed;
        this.toolDurability = toolDurability;
        this.toolQuality = toolQuality;
        this.toolEnchantment = ImmutableMap.of();
        return this;
    }

    //TODO temp method to fix compilation until a GT pr has been made
    public Material addTools(float toolSpeed, int toolDurability, int toolQuality, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        return addTools(0f, toolSpeed, toolDurability, toolQuality, toolEnchantment);
    }
    
    public Material addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality, ImmutableMap<Enchantment, Integer> toolEnchantment) {
    	this.toolEnchantment = toolEnchantment;
    	return addTools(toolDamage, toolSpeed, toolDurability, toolQuality);
    }

    public Material addTools(Material derivedMaterial, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        return addTools(derivedMaterial.toolDamage, derivedMaterial.toolSpeed, derivedMaterial.toolDurability, derivedMaterial.toolQuality, toolEnchantment);
    }

    public Material addTools(Material derivedMaterial) {
        return addTools(derivedMaterial.toolDamage, derivedMaterial.toolSpeed, derivedMaterial.toolDurability, derivedMaterial.toolQuality);
    }

    public Material addHandleStat(int durability, float speed) {
        if (!has(ROD)) flags(ROD);
        this.isHandle = true;
        this.handleDurability = durability;
        this.handleSpeed = speed;
        return this;
    }

    public Material addHandleStat(int durability, float speed, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        this.toolEnchantment = toolEnchantment;
        return addHandleStat(durability, speed);
    }

    public boolean has(IMaterialTag... tags) {
        for (IMaterialTag t : tags) {
            if (!t.all().contains(this)) return false;
        }
        return true;
    }

    public Material flags(IMaterialTag... tags) {
        for (IMaterialTag t : tags) {
            if (t == ORE) flags(ORE_SMALL);
            if (t == ORE || t == ORE_SMALL || t == ORE_STONE) flags(ROCK, CRUSHED, CRUSHED_PURIFIED, CRUSHED_CENTRIFUGED, DUST_IMPURE, DUST_PURE, DUST);
            t.add(this);
        }
        return this;
    }

    public void remove(IMaterialTag... tags) {
        for (IMaterialTag t : tags) {
            t.remove(this);
        }
    }

    public Material mats(Function<ImmutableMap.Builder<Material, Integer>, ImmutableMap.Builder<Material, Integer>> func) {
        return mats(func.apply(new ImmutableMap.Builder<>()).build());
    }

    public Material mats(ImmutableMap<Material, Integer> stacks) {
        stacks.forEach((k, v) -> processInto.add(new MaterialStack(k, v)));
        return this;
    }
    
    public void setChemicalFormula() {
    	if (element != null) chemicalFormula = element.getElement();
    	else if (!processInto.isEmpty()) chemicalFormula = String.join("", processInto.stream().map(MaterialStack::toString).collect(Collectors.joining()));
    }

    public void setLiquid(Fluid fluid) {
        liquid = fluid;
    }

    public void setGas(Fluid fluid) {
        gas = fluid;
    }

    public void setPlasma(Fluid fluid) {
        plasma = fluid;
    }

    /** Basic Getters**/
    public ITextComponent getDisplayName() {
        return displayName == null ? displayName = new TranslationTextComponent("material." + getId()) : displayName;
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
        if (processInto.size() <= 0) return Element.Tc.getProtons();
        long rAmount = 0, tAmount = 0;
        for (MaterialStack stack : processInto) {
            tAmount += stack.s;
            rAmount += stack.s * stack.m.getProtons();
        }
        return (getDensity() * rAmount) / (tAmount * Ref.U);
    }

    public long getNeutrons() {
        if (element != null) return element.getNeutrons();
        if (processInto.size() <= 0) return Element.Tc.getNeutrons();
        long rAmount = 0, tAmount = 0;
        for (MaterialStack stack : processInto) {
            tAmount += stack.s;
            rAmount += stack.s * stack.m.getNeutrons();
        }
        return (getDensity() * rAmount) / (tAmount * Ref.U);
    }

    public long getMass() {
        if (element != null) return element.getMass();
        if (processInto.size() <= 0) return Element.Tc.getMass();
        long rAmount = 0, tAmount = 0;
        for (MaterialStack stack : processInto) {
            tAmount += stack.s;
            rAmount += stack.s * stack.m.getMass();
        }
        return (getDensity() * rAmount) / (tAmount * Ref.U);
    }

    /** Element Getters **/
    public Element getElement() {
        return element;
    }
    
    public String getChemicalFormula() {
    	return chemicalFormula;
    }

    /** Solid Getters **/
    public int getMeltingPoint() {
        return meltingPoint;
    }

    public int getBlastTemp() {
        return blastFurnaceTemp;
    }

    public boolean needsBlastFurnace() {
        return needsBlastFurnace;
    }

    /** Gem Getters **/
    public boolean isTransparent() {
        return transparent;
    }

    /** Tool Getters **/
    public float getToolDamage() { return toolDamage; }

    public float getToolSpeed() { return toolSpeed; }

    public int getToolDurability() {
        return toolDurability;
    }

    public int getToolQuality() {
        return toolQuality;
    }
    
    public ImmutableMap<Enchantment, Integer> getEnchantments() {
    	return toolEnchantment;
    }

    public boolean isHandle() { return isHandle; }

    public int getHandleDurability() { return handleDurability; }

    public float getHandleSpeed() { return handleSpeed; }

    /** Fluid/Gas/Plasma Getters **/
    public Fluid getLiquid() {
        return liquid;
    }

    public Fluid getGas() {
        return gas;
    }

    public Fluid getPlasma() {
        return plasma;
    }
    
    public int getLiquidTemperature() {
    	return liquidTemperature;
    }

    
    public int getGasTemperature() {
    	return gasTemperature;
    }

    public int getFuelPower() {
        return fuelPower;
    }

    /** Processing Getters/Setters **/
    public int getOreMulti() {
        return oreMulti;
    }

    public int getSmeltingMulti() {
        return smeltingMulti;
    }

    public int getByProductMulti() {
        return byProductMulti;
    }

    public Material setOreMulti(int multi) {
        oreMulti = multi;
        return this;
    }

    public Material setSmeltingMulti(int multi) {
        smeltingMulti = multi;
        return this;
    }

    public Material setByProductMulti(int multi) {
        byProductMulti = multi;
        return this;
    }

    public Material getSmeltInto() {
        return smeltInto;
    }

    public Material getDirectSmeltInto() {
        return directSmeltInto;
    }

    public Material getArcSmeltInto() {
        return arcSmeltInto;
    }

    public Material getMacerateInto() {
        return macerateInto;
    }

    public Material setSmeltInto(Material m) {
        smeltInto = m;
        return this;
    }

    public Material setDirectSmeltInto(Material m) {
        directSmeltInto = m;
        return this;
    }

    public Material setArcSmeltInto(Material m) {
        arcSmeltInto = m;
        return this;
    }

    public Material setMacerateInto(Material m) {
        macerateInto = m;
        return this;
    }

    public boolean hasSmeltInto() {
        return smeltInto != this;
    }

    public boolean hasDirectSmeltInto() {
        return directSmeltInto != this;
    }

    public boolean hasArcSmeltInto() {
        return arcSmeltInto != this;
    }

    public boolean hasMacerateInto() {
        return macerateInto != this;
    }

    public ArrayList<MaterialStack> getProcessInto() {
        return processInto;
    }

    public ArrayList<Material> getByProducts() {
        return byProducts;
    }

    public boolean hasByProducts() {
        return byProducts.size() > 0;
    }

    public Material addByProduct(Material... mats) {
        for (Material mat : mats) {
            byProducts.add(mat);
        }
        return this;
    }

    public ItemStack getCell(int amount) {
    	//return Utils.ca(amount, Data.CellTin.fill(getLiquid()));
        return ItemStack.EMPTY;
    }

    public ItemStack getCellGas(int amount) {
        //return Utils.ca(amount, Data.CellTin.fill(getGas()));
        return ItemStack.EMPTY;
    }

    public ItemStack getCellPlasma(int amount) {
        //return Utils.ca(amount, Data.CellTin.fill(getPlasma()));
        return ItemStack.EMPTY;
    }

    public FluidStack getLiquid(int amount) {
        if (liquid == null) throw new NullPointerException(getId() + ": Liquid is null");
        return new FluidStack(liquid, amount);
    }

    public FluidStack getGas(int amount) {
        if (gas == null) throw new NullPointerException(getId() + ": Gas is null");
        return new FluidStack(getGas(), amount);
    }

    public FluidStack getPlasma(int amount) {
        if (plasma == null) throw new NullPointerException(getId() + ": Plasma is null");
        return new FluidStack(getPlasma(), amount);
    }

    public static Material get(String id) {
        return AntimatterAPI.getMaterial(id);
    }

    public static Material get(int hash) {
        return AntimatterAPI.getMaterialById(hash);
    }
}
