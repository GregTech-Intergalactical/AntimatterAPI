package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.SlotData;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.api.structure.StructurePattern;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Machine implements IStringSerializable {

    /** Global Members **/
    private static int lastInternalId;

    /** Basic Members **/
    protected int internalId;
    protected Block block;
    protected Class tileClass;
    protected String name, displayName;
    protected ArrayList<Tier> tiers;
    protected int machineMask;

    /** Recipe Members **/
    protected RecipeMap recipeMap;

    /** GUI Members **/
    protected Object modInstance;
    protected int guiId;
    protected SlotData[] slots;
    protected int inputCount, outputCount;

    /** Fluid Members **/
    protected SlotData[] fluidSlots;
    protected int inputTankCount, outputTankCount;

    /** Multi Members **/
    protected StructurePattern structurePattern;

    //TODO add valid covers

    public Machine(String name, Block block, Class tileClass) {
        internalId = lastInternalId++;
        this.name = name;
        this.block = block;
        this.tileClass = tileClass;
        Machines.machineTypeLookup.put(name, this);
    }

    public int getId() {
        return internalId;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName(String tier) {
        if (displayName == null) {
            displayName = I18n.format("machine." + name + "." + tier + ".name");
        }
        return displayName;
    }

    public String getJeiCategoryID() {
        return "it.recipe_map." + name;
    }

    public String getJeiCategoryName() {
        return I18n.format("jei.category." + name + ".name");
    }

    public RecipeMap getRecipeMap() {
        return recipeMap;
    }

    public ResourceLocation getGUITexture(String tier) {
        return new ResourceLocation(Ref.MODID, "textures/gui/machines/" + name + ".png");
    }

    public ResourceLocation getBaseTexture(String tier) {
        return new ResourceLocation(Ref.MODID, "blocks/machines/base/" + tier);
    }

    public ResourceLocation getOverlayTexture(int type) {
        if (type == 0) {
            return new ResourceLocation(Ref.MODID + ":blocks/machines/overlays/" + name);
        } else if (type == 1) {
            return new ResourceLocation(Ref.MODID + ":blocks/machines/overlays/active/" + name);
        } else {
            return new ResourceLocation(Ref.MODID + ":blocks/machines/overlays/" + name);
        }
    }

    public ModelResourceLocation getOverlayModel() {
        return new ModelResourceLocation(Ref.MODID + ":machine_part/overlays/" + name);
    }

    public Machine addFlags(MachineFlag... flags) {
        for (MachineFlag flag : flags) {
            machineMask |= flag.getBit();
            flag.add(this);
        }
        return this;
    }

    public Machine setFlags(MachineFlag... flags) {
        machineMask = 0;
        addFlags(flags);
        return this;
    }

    public Machine setBlock(Block block) {
        this.block = block;
        return this;
    }

    public Machine setTileClass(Class tileClass) {
        this.tileClass = tileClass;
        return this;
    }

    public Machine setTiers(Tier... tiers) {
        this.tiers = new ArrayList<>(Arrays.asList(tiers));
        return this;
    }

    public Machine addGUI(Object instance, int id) {
        modInstance = instance;
        guiId = id;
        addFlags(MachineFlag.GUI);
        return this;
    }

    public Machine addSlots(Machine slotsToCopy) {
        return addSlots(slotsToCopy.slots);
    }

    public Machine addSlots(SlotData... slots) {
        this.slots = slots;
        for (SlotData slot : slots) {
            if (slot.type == 0) {
                inputCount++;
            } else if (slot.type == 1) {
                outputCount++;
            }
        }
        return this;
    }

    public Machine addFluidSlots(SlotData... slots) {
        this.fluidSlots = slots;
        addFlags(MachineFlag.FLUID);
        for (SlotData slot : slots) {
            if (slot.type == 2) {
                inputTankCount++;
            } else if (slot.type == 3) {
                outputTankCount++;
            }
        }
        return this;
    }

    public Machine addRecipeMap() {
        recipeMap = new RecipeMap(10);
        return this;
    }

    public Recipe findRecipe(ItemStack[] inputs, FluidStack... fluidInputs) {
        return RecipeMap.findRecipeItem(recipeMap, inputs);
    }

    public Machine addPattern(StructurePattern pattern) {
        structurePattern = pattern;
        return this;
    }

    public boolean hasFlag(MachineFlag flag) {
        return (machineMask & flag.getBit()) != 0;
    }

    /** Getters **/
    public Block getBlock() {
        return block;
    }

    public Class getTileClass() {
        return tileClass;
    }

    public int getMask() {
        return machineMask;
    }

    public Collection<Tier> getTiers() {
        return tiers;
    }

    public SlotData[] getSlots() {
        return slots;
    }

    public SlotData[] getFluidSlots() {
        return fluidSlots;
    }

    public int getSlotCount() {
//        int count = slots == null ? 0 : slots.length;
//        if (hasFlag(MachineFlag.FLUID_INPUT)) {
//            count++;
//        }
//        return count;
        return slots == null ? 0 : slots.length;
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public int getFluidInputCount() {
        return inputTankCount;
    }

    public int getFluidOutputCount() {
        return outputTankCount;
    }

    public int getGuiId() {
        return guiId;
    }

    public StructurePattern getPattern() {
        return structurePattern;
    }

    /** Static Methods **/
    public static int getLastInternalId() {
        return lastInternalId;
    }
}
