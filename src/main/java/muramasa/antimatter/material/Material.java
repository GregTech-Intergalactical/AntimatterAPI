package muramasa.antimatter.material;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.block.BlockSurfaceRock;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.material.MaterialTag.HANDLE;
import static muramasa.antimatter.material.MaterialTag.METAL;

public class Material implements ISharedAntimatterObject, IRegistryEntryProvider {

    /**
     * Basic Members
     **/
    private String domain, id;
    private ITextComponent displayName;
    private int rgb;
    private TextureSet set;

    /**
     * Element Members
     **/
    private Element element;
    private String chemicalFormula = "";

    /**
     * Solid Members
     **/
    private int meltingPoint, blastFurnaceTemp, miningLevel = 0;
    private boolean needsBlastFurnace;

    /**
     * Gem Members
     **/
    private boolean transparent;

    /**
     * Fluid/Gas/Plasma Members
     **/
    private int fuelPower, liquidTemperature, gasTemperature;

    /**
     * Tool Members
     **/
    private float toolDamage, toolSpeed, toughness, knockbackResistance;
    private int toolDurability, toolQuality, armorDurabilityFactor;
    private int[] armor;
    private boolean isHandle;
    private int handleDurability;
    private float handleSpeed;
    private ImmutableMap<Enchantment, Integer> toolEnchantment;
    private ImmutableMap<Enchantment, Integer> armorEnchantment;
    private ImmutableMap<Enchantment, Integer> handleEnchantment;
    private List<AntimatterToolType> toolTypes;

    /**
     * Ore members
     **/
    private RangedInteger expRange = null;

    private final boolean enabled;

    /**
     * Processing Members
     **/
    private int oreMulti = 1, smeltingMulti = 1, byProductMulti = 1;
    private Material smeltInto, directSmeltInto, arcSmeltInto, macerateInto;
    private List<MaterialStack> processInto = new ObjectArrayList<>();
    private List<Material> byProducts = new ObjectArrayList<>();

    public Material(String domain, String id, int rgb, TextureSet set, String... modIds) {
        this.domain = domain;
        this.id = id;
        this.rgb = rgb;
        this.set = set;
        this.smeltInto = directSmeltInto = arcSmeltInto = macerateInto = this;
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
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        if (!enabled) return;
        if (registry == ForgeRegistries.ITEMS) {
            AntimatterAPI.all(MaterialTypeItem.class).stream().filter(t -> t.allowItemGen(this)).forEach(t -> t.getSupplier().supply(domain, t, this));
        } else if (registry == ForgeRegistries.BLOCKS) {
            if (has(BLOCK)) new BlockStorage(domain, this, BLOCK);
            if (has(FRAME)) new BlockStorage(domain, this, FRAME);
            if (has(ROCK)) AntimatterAPI.all(StoneType.class, s -> new BlockSurfaceRock(s.getDomain(), this, s));
            if (has(ORE)) AntimatterAPI.all(StoneType.class, s -> new BlockOre(domain, this, s, ORE));
            if (has(ORE_SMALL)) AntimatterAPI.all(StoneType.class, s -> new BlockOre(domain, this, s, ORE_SMALL));
            if (has(ORE_STONE)) new BlockOreStone(domain, this);
        }
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
        flags(INGOT, NUGGET, BLOCK).asFluid(); //TODO: Shall we generate blocks for every solid?
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

    public Material asOre(int minXp, int maxXp, boolean small, IMaterialTag... tags) {
        this.expRange = new RangedInteger(minXp, maxXp);
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
        return asFluid(fuelPower, Math.max(meltingPoint, 295));
    }

    public Material asFluid(int fuelPower, int temp) {
        flags(LIQUID);
        this.fuelPower = fuelPower;
        this.liquidTemperature = temp;
        return this;
    }

    public Material asGas() {
        return asGas(0);
    }

    public Material asGas(int fuelPower) {
        return asGas(fuelPower, Math.max(meltingPoint, 295));
    }

    public Material asGas(int fuelPower, int temp) {
        flags(GAS);
        this.gasTemperature = temp;
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

    public Material harvestLevel(int harvestLevel) {
        this.miningLevel = harvestLevel;
        return this;
    }

    public Material addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality) {
        return addTools(toolDamage, toolSpeed, toolDurability, toolQuality, ImmutableMap.of());
    }

    public Material addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality, ImmutableMap<Enchantment, Integer> toolEnchantment, AntimatterToolType... toolTypes) {
        if (has(INGOT))
            flags(TOOLS, PLATE, ROD, SCREW, BOLT); //TODO: We need to add bolt for now since screws depends on bolt, need to find time to change it
        else flags(TOOLS, ROD);
        this.toolDamage = toolDamage;
        this.toolSpeed = toolSpeed;
        this.toolDurability = toolDurability;
        this.toolQuality = toolQuality;
        this.miningLevel = toolQuality - 1;
        this.toolEnchantment = toolEnchantment;
        if (toolTypes.length > 0) {
            this.toolTypes = Arrays.asList(toolTypes);
        } else {
            this.toolTypes = AntimatterAPI.all(AntimatterToolType.class);
        }
        if (this.toolTypes.contains(ELECTRIC_WRENCH)) flags(WRENCHBIT);
        if (this.toolTypes.contains(BUZZSAW)) flags(BUZZSAW_BLADE);
        if (this.toolTypes.contains(DRILL)) flags(DRILLBIT);
        if (this.toolTypes.contains(CHAINSAW)) flags(CHAINSAWBIT);
        return this;
    }

    public Material addTools(Material derivedMaterial, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        return addTools(derivedMaterial.toolDamage, derivedMaterial.toolSpeed, derivedMaterial.toolDurability, derivedMaterial.toolQuality, toolEnchantment);
    }

    public Material addTools(Material derivedMaterial) {
        return addTools(derivedMaterial.toolDamage, derivedMaterial.toolSpeed, derivedMaterial.toolDurability, derivedMaterial.toolQuality);
    }

    public Material setAllowedToolTypes(AntimatterToolType... toolTypes){
        if (toolTypes.length > 0) {
            this.toolTypes = Arrays.asList(toolTypes);
        } else {
            this.toolTypes = AntimatterAPI.all(AntimatterToolType.class);
        }
        return this;
    }

    public Material addArmor(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor) {
        return addArmor(armor, toughness, knockbackResistance, armorDurabilityFactor, ImmutableMap.of());
    }

    public Material addArmor(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (armor.length < 4) {
            Antimatter.LOGGER.info("Material " + this.getId() + " unable to add armor, protection array must have at least 4 values");
            return this;
        }
        if (has(INGOT)) flags(ARMOR, PLATE);
        else flags(ARMOR);
        this.armor = armor;
        this.toughness = toughness;
        this.armorDurabilityFactor = armorDurabilityFactor;
        this.knockbackResistance = knockbackResistance;
        this.armorEnchantment = toolEnchantment;
        return this;
    }

    public Material addArmor(Material material, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        return addArmor(material.armor, material.toughness, material.knockbackResistance, material.armorDurabilityFactor, toolEnchantment);
    }

    public Material addArmor(Material material) {
        return addArmor(material.armor, material.toughness, material.knockbackResistance, material.armorDurabilityFactor);
    }

    public Material addHandleStat(int durability, float speed) {
        return addHandleStat(durability, speed, ImmutableMap.of());
    }

    public Material addHandleStat(int durability, float speed, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (!has(ROD)) flags(ROD);
        flags(HANDLE);
        this.isHandle = true;
        this.handleDurability = durability;
        this.handleSpeed = speed;
        this.handleEnchantment = toolEnchantment;
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
            if (!this.has(t)){
                t.add(this);
            }
            flags(t.dependents().stream().filter(d -> !this.has(d)).toArray(IMaterialTag[]::new));
        }
        return this;
    }

    public Material setExpRange(RangedInteger expRange) {
        this.expRange = expRange;
        return this;
    }

    public Material setExpRange(int min, int max) {
        this.expRange = new RangedInteger(min, max);
        return this;
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
        stacks.forEach((k, v) -> processInto.add(new MaterialStack(k, v)));
        return this;
    }

    public void setChemicalFormula() {
        if (!enabled) return;
        if (element != null) chemicalFormula = element.getElement();
        else if (!processInto.isEmpty())
            chemicalFormula = String.join("", processInto.stream().map(MaterialStack::toString).collect(Collectors.joining()));
    }

    /**
     * Basic Getters
     **/
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

    /**
     * Element Getters
     **/
    public Element getElement() {
        return element;
    }

    public String getChemicalFormula() {
        return chemicalFormula;
    }

    /**
     * Solid Getters
     **/
    public int getMeltingPoint() {
        return meltingPoint;
    }

    public int getBlastTemp() {
        return blastFurnaceTemp;
    }

    public boolean needsBlastFurnace() {
        return needsBlastFurnace;
    }

    public int getMiningLevel() {
        return miningLevel;
    }

    /**
     * Gem Getters
     **/
    public boolean isTransparent() {
        return transparent;
    }

    /**
     * Tool Getters
     **/
    public float getToolDamage() {
        return toolDamage;
    }

    public float getToolSpeed() {
        return toolSpeed;
    }

    public int getToolDurability() {
        return toolDurability;
    }

    public int getToolQuality() {
        return toolQuality;
    }

    public Map<Enchantment, Integer> getToolEnchantments() {
        return toolEnchantment != null ? toolEnchantment : Collections.emptyMap();
    }

    public Map<Enchantment, Integer> getArmorEnchantments() {
        return armorEnchantment != null ? armorEnchantment : Collections.emptyMap();
    }

    public Map<Enchantment, Integer> getHandleEnchantments() {
        return handleEnchantment != null ? handleEnchantment : Collections.emptyMap();
    }

    public int getArmorDurabilityFactor() {
        return armorDurabilityFactor;
    }

    public int[] getArmor() {
        return armor;
    }

    public float getToughness() {
        return toughness;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public List<AntimatterToolType> getToolTypes() {
        return toolTypes != null ? toolTypes : Collections.emptyList();
    }

    public boolean isHandle() {
        return isHandle;
    }

    public int getHandleDurability() {
        return handleDurability;
    }

    public float getHandleSpeed() {
        return handleSpeed;
    }

    public RangedInteger getExpRange() {
        return expRange;
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

    public FluidStack getLiquid(int amount) {
        return LIQUID.get().get(this, amount);
    }

    public FluidStack getGas(int amount) {
        return GAS.get().get(this, amount);
    }

    public FluidStack getPlasma(int amount) {
        return PLASMA.get().get(this, amount);
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

    /**
     * Processing Getters/Setters
     **/
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

    public List<MaterialStack> getProcessInto() {
        return processInto;
    }

    public List<Material> getByProducts() {
        return byProducts;
    }

    public boolean hasByProducts() {
        return byProducts.size() > 0;
    }

    public Material addByProduct(Material... mats) {
        byProducts.addAll(Arrays.asList(mats));
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
