package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Slot;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.api.structure.StructurePattern;
import muramasa.gregtech.common.blocks.BlockMachine;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Machine implements IStringSerializable {

    /** Global Members **/
    private static int lastInternalId;

    /** Basic Members **/
    protected int internalId;
    protected BlockMachine block;
    protected Class tileClass;
    protected String name, displayName;
    protected ArrayList<Tier> tiers;
    protected int machineMask;

    /** Recipe Members **/
    protected RecipeMap recipeMap;

    /** GUI Members **/
    protected Object modInstance;
    protected int guiId;
    protected ArrayList<Slot> slots;
    protected int inputCount, outputCount;

    /** Fluid Members **/
    protected int inputTankCount, outputTankCount;

    /** Multi Members **/
    protected StructurePattern structurePattern;

    //TODO add valid covers

    public Machine(String name) {
        this.name = name;
        this.block = new BlockMachine(name);
        this.tileClass = TileEntityMachine.class;
    }

    public Machine(String name, BlockMachine block, Class tileClass) {
        internalId = lastInternalId++;
        this.name = name;
        this.block = block;
        this.tileClass = tileClass;
        setTiers(Tier.LV);
        Machines.add(this);
    }

    public int getInternalId() {
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
        return new ResourceLocation(Ref.MODID, "textures/gui/machine/" + name + ".png");
    }

    public ResourceLocation getBaseTexture(String tier) {
        return new ResourceLocation(Ref.MODID, "blocks/machine/base/" + tier);
    }

    public ResourceLocation getOverlayTexture(int state, String type) {
        if (state == 0) {
            return new ResourceLocation(Ref.MODID + ":blocks/machine/overlay/" + name + "/" + type);
        } else if (state == 1) {
            return new ResourceLocation(Ref.MODID + ":blocks/machine/overlay/" + name + "/active/" + type);
        } else {
            return new ResourceLocation(Ref.MODID + ":blocks/machine/overlay/" + name + "/" + type);
        }
    }

    public ModelResourceLocation getOverlayModel(String type) {
//        return new ModelResourceLocation(Ref.MODID + ":machine_part/overlay/" + name);
        return new ModelResourceLocation(Ref.MODID + ":machine_part/overlay/" + name + "/" + type);
    }

    public void addFlags(MachineFlag... flags) {
        for (MachineFlag flag : flags) {
            machineMask |= flag.getBit();
            flag.add(this);
        }
    }

    public void setFlags(MachineFlag... flags) {
        machineMask = 0;
        addFlags(flags);
    }

    public void setBlock(BlockMachine block) {
        this.block = block;
    }

    public void setTileClass(Class tileClass) {
        this.tileClass = tileClass;
    }

    public Machine setTiers(Tier... tiers) {
        this.tiers = new ArrayList<>(Arrays.asList(tiers));
        return this;
    }

    public void addGUI(Object instance, int id) {
        modInstance = instance;
        guiId = id;
        addFlags(MachineFlag.GUI);
    }

    public void addSlots(Slot... slots) {
        if (this.slots == null) this.slots = new ArrayList<>();
        for (Slot slot : slots) {
            this.slots.add(slot);
            if (slot.type == 0) {
                inputCount++;
            } else if (slot.type == 1) {
                outputCount++;
            } else if (slot.type == 2) {
                inputTankCount++;
            } else if (slot.type == 3) {
                outputTankCount++;
            }
        }
    }

    public void addRecipeMap() {
        recipeMap = new RecipeMap(10);
    }

    public void addPattern(StructurePattern pattern) {
        structurePattern = pattern;
    }

    public Recipe findRecipe(MachineItemHandler stackHandler, MachineFluidHandler tankHandler) {
        return RecipeMap.findRecipeItem(recipeMap, stackHandler.getInputs());
    }

    public boolean hasFlag(MachineFlag flag) {
        return (machineMask & flag.getBit()) != 0;
    }

    /** Getters **/
    public BlockMachine getBlock() {
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

    public ArrayList<Slot> getSlots() {
        return slots;
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
