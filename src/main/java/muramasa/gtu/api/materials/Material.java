package muramasa.gtu.api.materials;

import com.google.common.collect.ImmutableMap;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockOre;
import muramasa.gtu.api.blocks.BlockStorage;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.Data;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static muramasa.gtu.api.materials.MaterialType.*;
import static muramasa.gtu.api.materials.MaterialTag.METAL;

public class Material implements IGregTechObject {

    private static int LAST_INTERNAL_ID;

    public static final long M = 3628800;

    private int internalId;

    /** Basic Members **/
    private int rgb;
    private long itemMask, recipeMask;
    private String id;
    private TextureSet set;

    /** Element Members **/
    private Element element;
    private String chemicalFormula;

    /** Solid Members **/
    private int meltingPoint, blastFurnaceTemp;
    private boolean needsBlastFurnace;

    /** Gem Members **/
    private boolean transparent;

    /** Fluid/Gas/Plasma Members **/
    private Fluid liquid, gas, plasma;
    private int fuelPower, liquidTemperature, gasTemperature;
    
    /** Tool Members **/
    private float toolSpeed;
    private int toolDurability, toolQuality;
    private Material handleMaterial;
    private ImmutableMap<Enchantment, Integer> toolEnchantment;

    /** Processing Members **/
    private int oreMulti = 1, smeltingMulti = 1, byProductMulti = 1;
    private Material smeltInto, directSmeltInto, arcSmeltInto, macerateInto;
    private ArrayList<MaterialStack> processInto = new ArrayList<>();
    private ArrayList<Material> byProducts = new ArrayList<>();

    public Material(String id, int rgb, TextureSet set) {
        this.internalId = LAST_INTERNAL_ID++;
        this.id = id;
        this.smeltInto = directSmeltInto = arcSmeltInto = macerateInto = this;
        this.rgb = rgb;
        this.set = set;
        GregTechAPI.register(Material.class, this);
    }

    public Material(String id, int rgb, TextureSet set, Element element) {
        this(id, rgb, set);
        this.element = element;
    }

    public int getInternalId() {
        return internalId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getId();
    }
    
    public Material asDust(IMaterialFlag... flags) {
        return asDust(295, flags);
    }

    public Material asDust(int meltingPoint, IMaterialFlag... flags) {
        add(DUST, DUST_SMALL, DUST_TINY);
        add(flags);
        this.meltingPoint = meltingPoint;
        if (meltingPoint > 295) {
//            asFluid();//TODO disabled due to Sodium having a fluid
        }
        return this;
    }

    public Material asSolid(IMaterialFlag... flags) {
        return asSolid(295, 0, flags);
    }

    public Material asSolid(int meltingPoint, int blastFurnaceTemp, IMaterialFlag... flags) {
        asDust(meltingPoint, flags);
        add(INGOT, NUGGET, BLOCK, LIQUID); //TODO: Shall we generate blocks for every solid?
        this.blastFurnaceTemp = blastFurnaceTemp;
        this.needsBlastFurnace = blastFurnaceTemp >= 1000;
        if (blastFurnaceTemp > 1750) {
            add(INGOT_HOT);
        }
        return this;
    }

    public Material asMetal(IMaterialFlag... flags) {
        return asMetal(295, 0, flags);
    }

    public Material asMetal(int meltingPoint, int blastFurnaceTemp, IMaterialFlag... flags) {
        asSolid(meltingPoint, blastFurnaceTemp, flags);
        add(METAL);
        return this;
    }

    public Material asGemBasic(boolean transparent, IMaterialFlag... flags) {
        asDust(flags);
        add(GEM, BLOCK);
        if (transparent) {
            this.transparent = true;
            add(PLATE, LENS);
        }
        return this;
    }

    //TODO: Shall we do gem variants?
    public Material asGem(boolean transparent, IMaterialFlag... flags) {
        asGemBasic(transparent, flags);
        add(GEM_CHIPPED, GEM_FLAWED, GEM_FLAWLESS, GEM_EXQUISITE);
        return this;
    }

    public Material asFluid() {
        return asFluid(0);
    }

    public Material asFluid(int fuelPower) {
        add(LIQUID);
        this.fuelPower = fuelPower;
        this.liquidTemperature = meltingPoint > 295 ? meltingPoint : 295;
        return this;
    }

    public Material asGas() {
        return asGas(0);
    }

    public Material asGas(int fuelPower) {
        add(GAS);
        this.gasTemperature = meltingPoint > 295 ? meltingPoint : 295;
        this.fuelPower = fuelPower;
        return this;
    }

    public Material asPlasma() {
        return asPlasma(0);
    }

    public Material asPlasma(int fuelPower) {
        asGas(fuelPower);
        add(PLASMA);
        return this;
    }

    public Material addTools(float toolSpeed, int toolDurability, int toolQuality) {
        if (has(INGOT)) {
            add(TOOLS, PLATE, ROD, SCREW, BOLT); //TODO: We need to add bolt for now since screws depends on bolt, need to find time to change it
        } else if (has(GEM)) {
            add(TOOLS, ROD);
        }
        this.toolSpeed = toolSpeed;
        this.toolDurability = toolDurability;
        this.toolQuality = toolQuality;
        this.handleMaterial = this;
        this.toolEnchantment = ImmutableMap.of();
        return this;
    }
    
    public Material addTools(float toolSpeed, int toolDurability, int toolQuality, ImmutableMap<Enchantment, Integer> toolEnchantment) {
    	this.toolEnchantment = toolEnchantment;
    	return addTools(toolSpeed, toolDurability, toolQuality);
    }

    public boolean has(IMaterialFlag... flags) {
        for (IMaterialFlag flag : flags) {
            if (flag instanceof MaterialType) {
                if ((itemMask & flag.getBit()) == 0) return false;
            } else if (flag instanceof MaterialTag) {
                if ((recipeMask & flag.getBit()) == 0) return false;
            }
        }
        return true;
    }

    public void add(IMaterialFlag... flags) {
        for (IMaterialFlag flag : flags) {
            if (flag instanceof MaterialType) {
                if (flag == ORE) {
                    add(ORE_SMALL); //TODO this is temp for worldgen testing
                    add(CRUSHED, CRUSHED_PURIFIED, CRUSHED_CENTRIFUGED, DUST_IMPURE, DUST_PURE, DUST);
                }
                itemMask |= flag.getBit();
            } else if (flag instanceof MaterialTag) {
                recipeMask |= flag.getBit();
            }
            if (!flag.getMats().contains(this)) flag.add(this);
        }
    }

    public void remove(IMaterialFlag... flags) {
        for (IMaterialFlag flag : flags) {
            if (flag instanceof MaterialType) {
                itemMask &= ~flag.getBit();
            } else if (flag instanceof MaterialTag) {
                recipeMask &= ~flag.getBit();
            }
            flag.remove(this);
        }
    }

    //TODO fix this...
    public Material add(Object... objects) {
        if (objects.length % 2 == 0) {
            for (int i = 0; i < objects.length; i += 2) {
                processInto.add(new MaterialStack(((Material) objects[i]), (int) objects[i + 1]));
            }
        }
        return this;
    }
    
    public void setChemicalFormula() {
    	if (element != null) chemicalFormula = element.getDisplayName();
    	else if (!processInto.isEmpty()) chemicalFormula = chemicalFormula.join("", processInto.stream().map(stack -> stack.toString()).collect(Collectors.joining()));
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
    public String getDisplayName() {
        return Utils.trans("material." + getId() + ".name");
    }

    public int getRGB() {
        return rgb;
    }

    public TextureSet getSet() {
        return set;
    }

    public long getItemMask() {
        return itemMask;
    }

    public long getRecipeMask() {
        return recipeMask;
    }

    public long getDensity() {
        return M;
    }

    public long getProtons() {
        if (element != null) return element.getProtons();
        if (processInto.size() <= 0) return Element.Tc.getProtons();
        long rAmount = 0, tAmount = 0;
        for (MaterialStack stack : processInto) {
            tAmount += stack.s;
            rAmount += stack.s * stack.m.getProtons();
        }
        return (getDensity() * rAmount) / (tAmount * M);
    }

    public long getNeutrons() {
        if (element != null) return element.getNeutrons();
        if (processInto.size() <= 0) return Element.Tc.getNeutrons();
        long rAmount = 0, tAmount = 0;
        for (MaterialStack stack : processInto) {
            tAmount += stack.s;
            rAmount += stack.s * stack.m.getNeutrons();
        }
        return (getDensity() * rAmount) / (tAmount * M);
    }

    public long getMass() {
        if (element != null) return element.getMass();
        if (processInto.size() <= 0) return Element.Tc.getMass();
        long rAmount = 0, tAmount = 0;
        for (MaterialStack stack : processInto) {
            tAmount += stack.s;
            rAmount += stack.s * stack.m.getMass();
        }
        return (getDensity() * rAmount) / (tAmount * M);
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
    public float getToolSpeed() {
        return toolSpeed;
    }

    public int getToolDurability() {
        return toolDurability;
    }

    public int getToolQuality() {
        return toolQuality;
    }
    
    public ImmutableMap<Enchantment, Integer> getEnchantments() {
    	return toolEnchantment;
    }

    public Material getHandleMaterial() {
        return handleMaterial;
    }

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

    /** Helpful Stack Getters **/
    public ItemStack getCrushed(int amount) {
        return MaterialItem.get(CRUSHED, this, amount);
    }

    public ItemStack getCrushedCentrifuged(int amount) {
        return MaterialItem.get(CRUSHED_CENTRIFUGED, this, amount);
    }

    public ItemStack getCrushedPurified(int amount) {
        return MaterialItem.get(CRUSHED_PURIFIED, this, amount);
    }

    public ItemStack getDust(int amount) {
        return MaterialItem.get(DUST, this, amount);
    }

    public ItemStack getDustPure(int amount) {
        return MaterialItem.get(DUST_PURE, this, amount);
    }

    public ItemStack getDustImpure(int amount) {
        return MaterialItem.get(DUST_IMPURE, this, amount);
    }

    public ItemStack getDustSmall(int amount) {
        return MaterialItem.get(DUST_SMALL, this, amount);
    }

    public ItemStack getDustTiny(int amount) {
        return MaterialItem.get(DUST_TINY, this, amount);
    }

    public ItemStack getNugget(int amount) {
        return MaterialItem.get(NUGGET, this, amount);
    }

    public ItemStack getIngot(int amount) {
        return MaterialItem.get(INGOT, this, amount);
    }

    public ItemStack getIngotHot(int amount) {
        return MaterialItem.get(INGOT_HOT, this, amount);
    }

    public ItemStack getPlate(int amount) {
        return MaterialItem.get(PLATE, this, amount);
    }

    public ItemStack getPlateDense(int amount) {
        return MaterialItem.get(PLATE_DENSE, this, amount);
    }

    public ItemStack getGem(int amount) {
        return MaterialItem.get(GEM, this, amount);
    }

    public ItemStack getGemChipped(int amount) {
        return MaterialItem.get(GEM_CHIPPED, this, amount);
    }

    public ItemStack getGemFlawed(int amount) {
        return MaterialItem.get(GEM_FLAWED, this, amount);
    }

    public ItemStack getGemFlawless(int amount) {
        return MaterialItem.get(GEM_FLAWLESS, this, amount);
    }

    public ItemStack getGemExquisite(int amount) {
        return MaterialItem.get(GEM_EXQUISITE, this, amount);
    }

    public ItemStack getFoil(int amount) {
        return MaterialItem.get(FOIL, this, amount);
    }

    public ItemStack getRod(int amount) {
        return MaterialItem.get(ROD, this, amount);
    }

    public ItemStack getBolt(int amount) {
        return MaterialItem.get(BOLT, this, amount);
    }

    public ItemStack getScrew(int amount) {
        return MaterialItem.get(SCREW, this, amount);
    }

    public ItemStack getRing(int amount) {
        return MaterialItem.get(RING, this, amount);
    }

    public ItemStack getSpring(int amount) {
        return MaterialItem.get(SPRING, this, amount);
    }

    public ItemStack getWireFine(int amount) {
        return MaterialItem.get(WIRE_FINE, this, amount);
    }

    public ItemStack getRotor(int amount) {
        return MaterialItem.get(ROTOR, this, amount);
    }

    public ItemStack getGear(int amount) {
        return MaterialItem.get(GEAR, this, amount);
    }

    public ItemStack getGearSmall(int amount) {
        return MaterialItem.get(GEAR_SMALL, this, amount);
    }

    public ItemStack getLens(int amount) {
        return MaterialItem.get(LENS, this, amount);
    }

    public ItemStack getCell(int amount) {
    	return Utils.ca(amount, Data.CellTin.fill(getLiquid()));
    }

    public ItemStack getCellGas(int amount) {
        return Utils.ca(amount, Data.CellTin.fill(getGas()));
    }

    public ItemStack getCellPlasma(int amount) {
        return Utils.ca(amount, Data.CellTin.fill(getPlasma()));
    }

    public ItemStack getOre(int amount) {
        if (!has(ORE)) Utils.onInvalidData("GET ERROR - DOES NOT GENERATE: P(" + ORE.getId() + ") M(" + id + ")");
        return new ItemStack(GregTechAPI.get(BlockOre.class, id), amount);
    }

    public ItemStack getBlock(int amount) {
        if (!has(BLOCK)) Utils.onInvalidData("GET ERROR - DOES NOT GENERATE: P(" + BLOCK.getId() + ") M(" + id + ")");
        return new ItemStack(GregTechAPI.get(BlockStorage.class, id), amount);
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
}
