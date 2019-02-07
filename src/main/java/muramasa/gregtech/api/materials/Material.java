package muramasa.gregtech.api.materials;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.Element;
import muramasa.gregtech.api.enums.GenerationFlag;
import muramasa.gregtech.api.enums.RecipeFlag;
import muramasa.gregtech.api.interfaces.IMaterialFlag;
import muramasa.gregtech.api.items.MetaItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Locale;

import static muramasa.gregtech.api.enums.Element.Tc;
import static muramasa.gregtech.api.enums.GenerationFlag.*;
import static muramasa.gregtech.api.enums.RecipeFlag.METAL;

public class Material {

    /** Basic Members **/
    private int id, rgb, mass;
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
    private int fuelPower;

    /** Tool Members **/
    private float toolSpeed;
    private int toolDurability, toolQuality;
    private String handleMaterial;

    /** Processing Members **/
    private int smeltInto, directSmeltInto, arcSmeltInto, macerateInto;
    private ArrayList<MaterialStack> processInto = new ArrayList<>();
    private ArrayList<Integer> byProducts = new ArrayList<>();

    public static void init() {
//        for (Material material : generated) {
//            if (material == Blaze) {
//                material.handleMaterial = "blaze";
//            } /*else if (aMaterial.contains(SubTag.MAGICAL) && aMaterial.contains(SubTag.CRYSTAL) && Loader.isModLoaded(MOD_ID_TC)) {
//                    aMaterial.mHandleMaterial = Thaumium;
//                }*/ else if (material.getMass() > Element.Tc.getMass() * 2) {
//                material.handleMaterial = Tungstensteel.;
//            } else if (material.getMass() > Element.Tc.getMass()) {
//                material.handleMaterial = Steel;
//            } else {
//                material.handleMaterial = Wood;
//            }
//        }
    }

    public Material(int id, String name, int rgb, MaterialSet set, Element element) {
        this(id, name, rgb, set);
        this.element = element;
    }

    public Material(int id, String displayName, int rgb, MaterialSet set) {
        this.id = smeltInto = directSmeltInto = arcSmeltInto = macerateInto = id;
        this.displayName = displayName;
        this.name = displayName.toLowerCase(Locale.ENGLISH).replaceAll("-", "_").replaceAll(" ", "_");
        this.rgb = rgb;
        this.set = set;
        Materials.generated[id] = this;
        Materials.generatedMap.put(name, this);
    }

    public Material asDust(int... temps) {
        add(DUST);
        if (temps.length >= 1 && temps[0] > 0) {
            meltingPoint = temps[0];
            asFluid();
        }
        return this;
    }

    public Material asSolid(int... temps) {
        asDust(temps);
        add(INGOT);
        if (temps.length >= 2 && temps[1] > 0) {
            needsBlastFurnace = temps[1] >= 1000;
            blastFurnaceTemp = temps[1];
            if (temps[1] > 1750) add(HINGOT);
        }
        return this;
    }

    public Material asMetal(int... temps) {
        asSolid(temps);
        add(METAL);
        return this;
    }

    public Material asGemBasic(boolean transparent) {
        asDust();
        add(BGEM);
        if (transparent) {
            this.transparent = true;
            add(PLATE);
        }
        return this;
    }

    public Material asGem(boolean transparent) {
        asGemBasic(transparent);
        add(GEM);
        return this;
    }

    public Material asFluid(int... fuelPower) {
        add(FLUID);
        if (fuelPower.length >= 1) this.fuelPower = fuelPower[0];
        return this;
    }

    public Material asGas(int... fuelPower) {
        asFluid(fuelPower);
        add(GAS);
        return this;
    }

    public Material asPlasma(int... fuelPower) {
        asFluid(fuelPower);
        add(GAS, PLASMA);
        return this;
    }

    public Material addOre() {
        add(CRUSHED, CRUSHEDC, CRUSHEDP);
        return this;
    }

    public Material addTools(float toolSpeed, int toolDurability, int toolQuality) {
        if (hasFlag(INGOT)) {
            add(TOOLS, PLATE, ROD, BOLT);
        } else if (hasFlag(BGEM)) {
            add(TOOLS, ROD);
        } /*else if (this == Material.Stone || this == Material.Wood) {
            add(TOOLS);
        }*/
        this.toolSpeed = toolSpeed;
        this.toolDurability = toolDurability;
        this.toolQuality = toolQuality;
        return this;
    }

    public boolean hasFlag(IMaterialFlag... flags) {
        for (IMaterialFlag flag : flags) {
            if (flag instanceof GenerationFlag) {
                return (itemMask & flag.getBit()) != 0;
            } else if (flag instanceof RecipeFlag) {
                return (recipeMask & flag.getBit()) != 0;
            }
        }
        return true;
    }

    public Material add(IMaterialFlag... flags) {
        for (IMaterialFlag flag : flags) {
            if (flag instanceof GenerationFlag) {
                itemMask |= flag.getBit();
            } else if (flag instanceof RecipeFlag) {
                recipeMask |= flag.getBit();
            }
            flag.add(this);
        }
        return this;
    }

    public Material add(Object... objects) {
        if (objects.length % 2 == 0) {
            for (int i = 0; i < objects.length; i += 2) {
                processInto.add(new MaterialStack(((Material) objects[i]).getId(), (int) objects[i + 1]));
            }
        }
        return this;
    }

    public Material add(Material... mats) {
        for (Material mat : mats) {
            byProducts.add(mat.getId());
        }
        return this;
    }

    /** Basic Getters**/
    public int getId() {
        return id;
    }

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

    public int getMass() {
        if (mass == 0) {
            if (element != null) return element.getMass();
            if (processInto.size() <= 0) return Tc.getMass();
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

    public int getBlastFurnaceTemp() {
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
    public int getFuelPower() {
        return fuelPower;
    }

    /** Processing Helpers **/
    public Material getSmeltInto() {
        return Materials.get(smeltInto);
    }

    public Material getDirectSmeltInto() {
        return Materials.get(directSmeltInto);
    }

    public Material getArcSmeltInto() {
        return Materials.get(arcSmeltInto);
    }

    public Material getMacerateInto() {
        return Materials.get(macerateInto);
    }

    public boolean hasSmeltInto() {
        return id != smeltInto;
    }

    public boolean hasDirectSmeltInto() {
        return id != directSmeltInto;
    }

    public boolean hasArcSmeltInto() {
        return id != arcSmeltInto;
    }

    public boolean hasMacerateInto() {
        return id != macerateInto;
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
    public ItemStack getChunk(int amount) {
        return MetaItem.get(Prefix.CHUNK, this, amount);
    }

    public ItemStack getCrushed(int amount) {
        return MetaItem.get(Prefix.CRUSHED, this, amount);
    }

    public ItemStack getCrushedC(int amount) {
        return MetaItem.get(Prefix.CRUSHED_CENTRIFUGED, this, amount);
    }

    public ItemStack getCrushedP(int amount) {
        return MetaItem.get(Prefix.CRUSHED_PURIFIED, this, amount);
    }

    public ItemStack getDust(int amount) {
        return MetaItem.get(Prefix.DUST, this, amount);
    }

    public ItemStack getDustS(int amount) {
        return MetaItem.get(Prefix.DUST_SMALL, this, amount);
    }

    public ItemStack getDustT(int amount) {
        return MetaItem.get(Prefix.DUST_TINY, this, amount);
    }

    public ItemStack getNugget(int amount) {
        return MetaItem.get(Prefix.NUGGET, this, amount);
    }

    public ItemStack getIngot(int amount) {
        return MetaItem.get(Prefix.INGOT, this, amount);
    }

    public ItemStack getIngotH(int amount) {
        return MetaItem.get(Prefix.INGOT_HOT, this, amount);
    }

    public ItemStack getPlate(int amount) {
        return MetaItem.get(Prefix.PLATE, this, amount);
    }

    public ItemStack getPlateD(int amount) {
        return MetaItem.get(Prefix.PLATE_DENSE, this, amount);
    }

    public ItemStack getGem(int amount) {
        return MetaItem.get(Prefix.GEM, this, amount);
    }

    public ItemStack getGemChipped(int amount) {
        return MetaItem.get(Prefix.GEM_CHIPPED, this, amount);
    }

    public ItemStack getGemFlawed(int amount) {
        return MetaItem.get(Prefix.GEM_FLAWED, this, amount);
    }

    public ItemStack getGemFlawless(int amount) {
        return MetaItem.get(Prefix.GEM_FLAWLESS, this, amount);
    }

    public ItemStack getGemExquisite(int amount) {
        return MetaItem.get(Prefix.GEM_EXQUISITE, this, amount);
    }

    public ItemStack getFoil(int amount) {
        return MetaItem.get(Prefix.FOIL, this, amount);
    }

    public ItemStack getRod(int amount) {
        return MetaItem.get(Prefix.ROD, this, amount);
    }

    public ItemStack getBolt(int amount) {
        return MetaItem.get(Prefix.BOLT, this, amount);
    }

    public ItemStack getScrew(int amount) {
        return MetaItem.get(Prefix.SCREW, this, amount);
    }

    public ItemStack getRing(int amount) {
        return MetaItem.get(Prefix.RING, this, amount);
    }

    public ItemStack getSpring(int amount) {
        return MetaItem.get(Prefix.SPRING, this, amount);
    }

    public ItemStack getWireF(int amount) {
        return MetaItem.get(Prefix.WIRE_FINE, this, amount);
    }

    public ItemStack getRotor(int amount) {
        return MetaItem.get(Prefix.ROTOR, this, amount);
    }

    public ItemStack getGear(int amount) {
        return MetaItem.get(Prefix.GEAR, this, amount);
    }

    public ItemStack getGearS(int amount) {
        return MetaItem.get(Prefix.GEAR_SMALL, this, amount);
    }

    public ItemStack getLens(int amount) {
        return MetaItem.get(Prefix.LENS, this, amount);
    }

    public ItemStack getCell(int amount) {
        return MetaItem.get(Prefix.CELL, this, amount);
    }

    public ItemStack getCellP(int amount) {
        return MetaItem.get(Prefix.CELL_PLASMA, this, amount);
    }
}
