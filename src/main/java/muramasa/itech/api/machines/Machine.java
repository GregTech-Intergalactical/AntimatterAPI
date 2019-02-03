package muramasa.itech.api.machines;

import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.recipe.Recipe;
import muramasa.itech.api.recipe.RecipeMap;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.api.util.Utils;
import muramasa.itech.common.utils.Ref;
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
    private static int lastInternalId = 0;

    /** Basic Members **/
    private int internalId;
    private Block block;
    private Class tileClass;
    private String name, displayName;
    private ArrayList<Tier> tiers;
//    private ArrayList<String> stateTextures;
    private int machineMask;

    /** Recipe Members **/
    private RecipeMap recipeMap;

    /** GUI Members **/
    private SlotData[] slots;
    private int guiId, inputCount, outputCount;
    private boolean isGuiTierSensitive;

    /** Multi Members **/
    private StructurePattern structurePattern;

    //TODO add valid covers

    public Machine(String name, Block block, Class tileClass) {
        internalId = lastInternalId++;
        this.name = name;
        this.block = block;
        this.tileClass = tileClass;
        tiers = new ArrayList<>();
//        stateTextures = new ArrayList<>();
        MachineList.machineTypeLookup.put(name, this);
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
        if (isGuiTierSensitive) {
            return new ResourceLocation(Ref.MODID, "textures/gui/machines/" + name + tier + ".png");
        } else {
            return new ResourceLocation(Ref.MODID, "textures/gui/machines/" + name + ".png");
        }
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

    //TODO target type specific recipe find
    public Recipe findRecipe(ItemStack[] inputs, FluidStack... fluidInputs) {
        return RecipeMap.findRecipeItem(name, inputs);
    }

    public Machine addFlags(MachineFlag... flags) {
        for (MachineFlag flag : flags) {
            machineMask = Utils.addFlag(machineMask, flag.getBit());
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

    public Machine addGUI(int id, boolean isTierSensitive) {
        guiId = id;
        isGuiTierSensitive = isTierSensitive;
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

    public Machine setGuiTierSensitive() {
        isGuiTierSensitive = true;
        return this;
    }

    public Machine addRecipeMap() {
        recipeMap = new RecipeMap(this);
        return this;
    }

    public Machine addPattern(StructurePattern pattern) {
        structurePattern = pattern;
        return this;
    }

//    public Machine addStateTextures(String... textures) {
//        stateTextures.addAll(Arrays.asList(textures));
//        return this;
//    }

    public boolean hasFlag(MachineFlag flag) {
        return Utils.hasFlag(machineMask, flag.getBit());
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

    public int getSlotCount() {
        return slots == null ? 0 : slots.length;
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public int getGuiId() {
        return guiId;
    }

    public StructurePattern getPattern() {
        return structurePattern;
    }

//    public String[] getStateTextures() {
//        return stateTextures.toArray(new String[0]);
//    }

    public static int getLastInternalId() {
        return lastInternalId;
    }
}
