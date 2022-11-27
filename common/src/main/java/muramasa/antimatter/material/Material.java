package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import tesseract.TesseractGraphWrappers;

import java.util.List;
import java.util.stream.Collectors;

import static muramasa.antimatter.material.TextureSet.NONE;

public class Material implements ISharedAntimatterObject {

    public static final Material NULL = AntimatterAPI.register(Material.class, new Material(Ref.ID, "null", 0xffffff, NONE));
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

    public static void init(){}

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

    public void remove(IMaterialTag... tags) {
        if (!enabled) return;
        for (IMaterialTag t : tags) {
            t.remove(this);
        }
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
        return AntimatterMaterialTypes.LIQUID.get().get(this, 1).getFluid();
    }

    public Fluid getGas() {
        return AntimatterMaterialTypes.GAS.get().get(this, 1).getFluid();
    }

    public Fluid getPlasma() {
        return AntimatterMaterialTypes.PLASMA.get().get(this, 1).getFluid();
    }

    public FluidStack getLiquid(long droplets) {
        return AntimatterMaterialTypes.LIQUID.get().get(this, droplets);
    }

    public FluidStack getGas(long droplets) {
        return AntimatterMaterialTypes.GAS.get().get(this, droplets);
    }

    public FluidStack getPlasma(long droplets) {
        return AntimatterMaterialTypes.PLASMA.get().get(this, droplets);
    }

    public FluidStack getLiquid(int mb) {
        return this.getLiquid(mb * TesseractGraphWrappers.dropletMultiplier);
    }

    public FluidStack getGas(int mb) {
        return this.getGas(mb * TesseractGraphWrappers.dropletMultiplier);
    }

    public FluidStack getPlasma(int mb) {
        return this.getPlasma(mb * TesseractGraphWrappers.dropletMultiplier);
    }

    /**
     * Processing Getters/Setters
     **/

    public List<MaterialStack> getProcessInto() {
        return MaterialTags.PROCESS_INTO.getList(this);
    }

    public List<Material> getByProducts() {
        return MaterialTags.BYPRODUCTS.getList(this);
    }

    public boolean hasByProducts() {
        return MaterialTags.BYPRODUCTS.getList(this).size() > 0;
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
        return material == null ? NULL : material;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
