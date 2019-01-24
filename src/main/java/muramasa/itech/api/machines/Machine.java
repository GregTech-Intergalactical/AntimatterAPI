package muramasa.itech.api.machines;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.recipe.Recipe;
import muramasa.itech.api.recipe.RecipeMap;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.api.util.Utils;
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

    /** Basic Members **/
    private Block block;
    private Class tileClass;
    private String name, displayName;
    private ResourceLocation overlayTexture;
    private ModelResourceLocation overlayModel;
    private int machineMask;

    /** Recipe Members **/
    private RecipeMap recipeMap;
    private String jeiCategoryID, jeiCategoryName;

    /** GUI Members **/
    private ResourceLocation guiTexture;
    private SlotData[] slots;
    private int guiId, inputCount, outputCount;
    private boolean isGuiTierSensitive;

    /** Multi Members **/
    private ResourceLocation baseTexture;
    private StructurePattern structurePattern;

    /** Powered Members **/
    private ArrayList<Tier> tiers; //TODO not specifically for power tiers?

    //TODO add valid covers

    public Machine(String name, Block block, Class tileClass) {
        this.name = name;
        this.block = block;
        this.tileClass = tileClass;
        guiTexture = new ResourceLocation(ITech.MODID, "textures/gui/machines/" + name + ".png");
        overlayTexture = new ResourceLocation(ITech.MODID + ":blocks/machines/overlays/" + name);
        overlayModel = new ModelResourceLocation(ITech.MODID + ":machineparts/overlays/" + name);
        tiers = new ArrayList<>();
        MachineList.machineTypeLookup.put(name, this);
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
        return jeiCategoryID;
    }

    public String getJeiCategoryName() {
        return jeiCategoryName;
    }

    public RecipeMap getRecipeMap() {
        return recipeMap;
    }

    public ResourceLocation getGuiTexture(String tier) {
        if (isGuiTierSensitive) {
            return new ResourceLocation(guiTexture.getResourceDomain(), guiTexture.getResourcePath().replace(".png", "").concat(Tier.get(tier).getName()).concat(".png"));
        } else {
            return guiTexture;
        }
    }

    public ResourceLocation getOverlayTexture() {
        return overlayTexture;
    }

    public ModelResourceLocation getOverlayModel() {
        return overlayModel;
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

    public Machine setTiers(Tier... tiers) {
        this.tiers = new ArrayList<>(Arrays.asList(tiers));
        return this;
    }

    public Machine addGUI(int id, boolean isTierSensitive) {
        guiId = id;
        isGuiTierSensitive = isTierSensitive;
        return this;
    }

    public Machine addSlots(Machine slotsToCopy) {
        return addSlots(slotsToCopy.slots);
    }

    public Machine addSlots(SlotData... slots) {
        guiTexture = new ResourceLocation(ITech.MODID, "textures/gui/machines/" + name + ".png");
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
        this.jeiCategoryID = "it.recipemap." + name;
        this.jeiCategoryName = I18n.format("jei.category." + name + ".name");
        recipeMap = new RecipeMap(this);
        return this;
    }

    public Machine setBaseTexture(ResourceLocation location) {
        baseTexture = location;
        return this;
    }

    public Machine addPattern(StructurePattern pattern) {
        structurePattern = pattern;
        return this;
    }

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

    public int getInputCount() {
        return inputCount;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public int getGuiId() {
        return guiId;
    }

    public ResourceLocation getBaseTexture() {
        return baseTexture;
    }

    public StructurePattern getPattern() {
        return structurePattern;
    }
}
