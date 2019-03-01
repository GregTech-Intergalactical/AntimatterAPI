package muramasa.gregtech.api.materials;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.Element;
import muramasa.gregtech.api.enums.StoneType;
import muramasa.gregtech.api.interfaces.IMaterialFlag;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.common.blocks.BlockOre;
import muramasa.gregtech.common.blocks.BlockStorage;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Locale;

import static muramasa.gregtech.api.materials.ItemFlag.*;
import static muramasa.gregtech.api.materials.RecipeFlag.METAL;

public class Material {

    /** Basic Members **/
    private int rgb, protons, mass;
    private long itemMask, recipeMask;
    private String name, displayName;
    private MaterialSet set;
    private boolean hasLocName;

    /** Element Members **/
    private Element element;

    /** Solid Members **/
    private int meltingPoint, blastFurnaceTemp;
    private boolean needsBlastFurnace;

    /** Gem Members **/
    private boolean transparent;

    /** Fluid/Gas/Plasma Members **/
    private Fluid liquid, gas, plasma;
    private int fuelPower;

    /** Tool Members **/
    private float toolSpeed;
    private int toolDurability, toolQuality;
    private String handleMaterial;

    /** Processing Members **/
    private int oreMulti, smeltingMulti, byProductMulti;
    private Material smeltInto, directSmeltInto, arcSmeltInto, macerateInto;
    private ArrayList<MaterialStack> processInto = new ArrayList<>();
    private ArrayList<String> byProducts = new ArrayList<>();

    public Material(String name, int rgb, MaterialSet set, Element element) {
        this(name, rgb, set);
        this.element = element;
    }

    public Material(String displayName, int rgb, MaterialSet set) {
        this.name = displayName.toLowerCase(Locale.ENGLISH).replace("-", "_").replace(" ", "_");
        this.smeltInto = directSmeltInto = arcSmeltInto = macerateInto = this;
        this.displayName = displayName;
        this.rgb = rgb;
        this.set = set;
        Materials.MATERIAL_LOOKUP.put(name, this);
    }

    public Material asDust(int meltingPoint, IMaterialFlag... flags) {
        add(DUST, DUSTS, DUSTT);
        add(flags);
        for (IMaterialFlag flag : flags) {
            if (flag == ORE) {
                add(CRUSHED, CRUSHEDP, CRUSHEDC, DUSTIP, DUSTP);
            }
        }
        this.meltingPoint = meltingPoint;
        if (meltingPoint > 0) {
            asFluid();
        }
        return this;
    }

    public Material asDust(IMaterialFlag... flags) {
        return asDust(0, flags);
    }

    public Material asSolid(IMaterialFlag... flags) {
        return asSolid(0, 0, flags);
    }

    public Material asSolid(int meltingPoint, int blastFurnaceTemp, IMaterialFlag... flags) {
        asDust(meltingPoint, flags);
        add(INGOT, NUGGET);
        this.blastFurnaceTemp = blastFurnaceTemp;
        this.needsBlastFurnace = blastFurnaceTemp >= 1000;
        if (blastFurnaceTemp > 1750) {
            add(HINGOT);
        }
        return this;
    }

    public Material asMetal(IMaterialFlag... flags) {
        return asMetal(0, 0, flags);
    }

    public Material asMetal(int meltingPoint, int blastFurnaceTemp, IMaterialFlag... flags) {
        asSolid(meltingPoint, blastFurnaceTemp, flags);
        add(METAL);
        return this;
    }

    public Material asGemBasic(boolean transparent, IMaterialFlag... flags) {
        asDust(flags);
        add(BGEM);
        if (transparent) {
            this.transparent = true;
            add(BLOCK, PLATE, LENS);
        }
        return this;
    }

    public Material asGem(boolean transparent, IMaterialFlag... flags) {
        asGemBasic(transparent, flags);
        add(GEM);
        return this;
    }

    public Material asFluid() {
        return asFluid(0);
    }

    public Material asFluid(int fuelPower) {
        add(LIQUID);
        this.fuelPower = fuelPower;
        return this;
    }

    public Material asGas() {
        return asGas(0);
    }

    public Material asGas(int fuelPower) {
        add(GAS);
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
        if (hasFlag(INGOT)) {
            add(TOOLS, PLATE, ROD, BOLT);
        } else if (hasFlag(BGEM)) {
            add(TOOLS, ROD);
        } /*else {
            add(TOOLS);
        }*/
        this.toolSpeed = toolSpeed;
        this.toolDurability = toolDurability;
        this.toolQuality = toolQuality;
        return this;
    }

    public boolean hasFlag(IMaterialFlag... flags) {
        for (IMaterialFlag flag : flags) {
            if (flag instanceof ItemFlag) {
                return (itemMask & flag.getBit()) != 0;
            } else if (flag instanceof RecipeFlag) {
                return (recipeMask & flag.getBit()) != 0;
            }
        }
        return true;
    }

    public void add(IMaterialFlag... flags) {
        for (IMaterialFlag flag : flags) {
            if (flag instanceof ItemFlag) {
                itemMask |= flag.getBit();
            } else if (flag instanceof RecipeFlag) {
                recipeMask |= flag.getBit();
            }
            flag.add(this);
        }
    }

    public Material add(Object... objects) {
        if (objects.length % 2 == 0) {
            for (int i = 0; i < objects.length; i += 2) {
                processInto.add(new MaterialStack(((Material) objects[i]).getName(), (int) objects[i + 1]));
            }
        }
        return this;
    }

    public Material add(Material... mats) {
        for (Material mat : mats) {
            byProducts.add(mat.getName());
        }
        return this;
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
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getDisplayName() {
        if (!hasLocName) {
            displayName = I18n.format("material." + getName() + ".name");
        }
        return displayName;
    }

    public int getRGB() {
        return rgb;
    }

    public MaterialSet getSet() {
        return set;
    }

    public long getItemMask() {
        return itemMask;
    }

    public long getRecipeMask() {
        return recipeMask;
    }

    public int getProtons() {
        if (protons == 0) {
            if (element != null) return element.getProtons();
            if (processInto.size() <= 0) return Element.Tc.getProtons();
            for (MaterialStack stack : processInto) {
                protons += stack.size * stack.get().getProtons();
            }
        }
        return protons;
    }

    public int getMass() {
        if (mass == 0) {
            if (element != null) return element.getMass();
            if (processInto.size() <= 0) return Element.Tc.getMass();
            for (MaterialStack stack : processInto) {
                mass += stack.size * stack.get().getMass();
            }
        }
        return mass;
    }

    /** Element Getters **/
    public Element getElement() {
        return element;
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

    public int getFuelPower() {
        return fuelPower;
    }

    /** Processing Getters **/
    public int getOreMulti() {
        return oreMulti;
    }

    public int getSmeltingMulti() {
        return smeltingMulti;
    }

    public int getByProductMulti() {
        return byProductMulti;
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

    public boolean hasSmeltInto() {
        return !name.equals(smeltInto);
    }

    public boolean hasDirectSmeltInto() {
        return !name.equals(directSmeltInto);
    }

    public boolean hasArcSmeltInto() {
        return !name.equals(arcSmeltInto);
    }

    public boolean hasMacerateInto() {
        return !name.equals(macerateInto);
    }

    public ArrayList<MaterialStack> getProcessInto() {
        return processInto;
    }

    public ArrayList<Material> getByProducts() {
        ArrayList<Material> materials = new ArrayList<>(byProducts.size());
        int size = byProducts.size();
        for (int i = 0; i < size; i++) {
            materials.add(Materials.get(byProducts.get(i)));
        }
        return materials;
    }

    public boolean hasByProducts() {
        return byProducts.size() > 0;
    }

    /** Helpful Stack Getters **/
//    public ItemStack getChunk(int amount) {
//        return MaterialItem.get(Prefix.Chunk, this, amount);
//    }

    public ItemStack getCrushed(int amount) {
        return MaterialItem.get(Prefix.Crushed, this, amount);
    }

    public ItemStack getCrushedC(int amount) {
        return MaterialItem.get(Prefix.CrushedCentrifuged, this, amount);
    }

    public ItemStack getCrushedP(int amount) {
        return MaterialItem.get(Prefix.CrushedPurified, this, amount);
    }

    public ItemStack getDust(int amount) {
        return MaterialItem.get(Prefix.Dust, this, amount);
    }

    public ItemStack getDustP(int amount) {
        return MaterialItem.get(Prefix.DustPure, this, amount);
    }

    public ItemStack getDustIP(int amount) {
        return MaterialItem.get(Prefix.DustImpure, this, amount);
    }

    public ItemStack getDustS(int amount) {
        return MaterialItem.get(Prefix.DustSmall, this, amount);
    }

    public ItemStack getDustT(int amount) {
        return MaterialItem.get(Prefix.DustTiny, this, amount);
    }

    public ItemStack getNugget(int amount) {
        return MaterialItem.get(Prefix.Nugget, this, amount);
    }

    public ItemStack getIngot(int amount) {
        return MaterialItem.get(Prefix.Ingot, this, amount);
    }

    public ItemStack getIngotH(int amount) {
        return MaterialItem.get(Prefix.IngotHot, this, amount);
    }

    public ItemStack getPlate(int amount) {
        return MaterialItem.get(Prefix.Plate, this, amount);
    }

    public ItemStack getPlateD(int amount) {
        return MaterialItem.get(Prefix.PlateDense, this, amount);
    }

    public ItemStack getGem(int amount) {
        return MaterialItem.get(Prefix.Gem, this, amount);
    }

    public ItemStack getGemChipped(int amount) {
        return MaterialItem.get(Prefix.GemChipped, this, amount);
    }

    public ItemStack getGemFlawed(int amount) {
        return MaterialItem.get(Prefix.GemFlawed, this, amount);
    }

    public ItemStack getGemFlawless(int amount) {
        return MaterialItem.get(Prefix.GemFlawless, this, amount);
    }

    public ItemStack getGemExquisite(int amount) {
        return MaterialItem.get(Prefix.GemExquisite, this, amount);
    }

    public ItemStack getFoil(int amount) {
        return MaterialItem.get(Prefix.Foil, this, amount);
    }

    public ItemStack getRod(int amount) {
        return MaterialItem.get(Prefix.Rod, this, amount);
    }

    public ItemStack getBolt(int amount) {
        return MaterialItem.get(Prefix.Bolt, this, amount);
    }

    public ItemStack getScrew(int amount) {
        return MaterialItem.get(Prefix.Screw, this, amount);
    }

    public ItemStack getRing(int amount) {
        return MaterialItem.get(Prefix.Ring, this, amount);
    }

    public ItemStack getSpring(int amount) {
        return MaterialItem.get(Prefix.Spring, this, amount);
    }

    public ItemStack getWireF(int amount) {
        return MaterialItem.get(Prefix.WireFine, this, amount);
    }

    public ItemStack getRotor(int amount) {
        return MaterialItem.get(Prefix.Rotor, this, amount);
    }

    public ItemStack getGear(int amount) {
        return MaterialItem.get(Prefix.Gear, this, amount);
    }

    public ItemStack getGearS(int amount) {
        return MaterialItem.get(Prefix.GearSmall, this, amount);
    }

    public ItemStack getLens(int amount) {
        return MaterialItem.get(Prefix.Lens, this, amount);
    }

    public ItemStack getCell(int amount) {
        return MaterialItem.get(Prefix.Cell, this, amount);
    }

    public ItemStack getCellG(int amount) {
        return MaterialItem.get(Prefix.CellGas, this, amount);
    }

    public ItemStack getCellP(int amount) {
        return MaterialItem.get(Prefix.CellPlasma, this, amount);
    }

    public ItemStack getOre(int amount) {
        return new ItemStack(BlockOre.get(StoneType.STONE, this), amount);
    }

    public ItemStack getBlock(int amount) {
        return new ItemStack(BlockStorage.get(this), amount);
    }

    public FluidStack getLiquid(int amount) {
        if (liquid == null) {
            throw new NullPointerException(getName() + ": Liquid is null");
        }
        return new FluidStack(liquid, amount);
    }

    public FluidStack getGas(int amount) {
        if (gas == null) {
            throw new NullPointerException(getName() + ": Gas is null");
        }
        return new FluidStack(getGas(), amount);
    }

    public FluidStack getPlasma(int amount) {
        if (plasma == null) {
            throw new NullPointerException(getName() + ": Plasma is null");
        }
        return new FluidStack(getPlasma(), amount);
    }
}
