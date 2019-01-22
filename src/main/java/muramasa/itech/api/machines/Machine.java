package muramasa.itech.api.machines;

import muramasa.itech.ITech;
import muramasa.itech.api.behaviour.BehaviourMultiMachine;
import muramasa.itech.api.enums.AbilityFlag;
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
    private String name, displayName, jeiCategoryID, jeiCategoryName;
    private RecipeMap recipeMap;
    private ResourceLocation overlayTexture;
    private ModelResourceLocation overlayModel;
    private int abilityMask;

    /** Multi Members **/
    private ResourceLocation baseTexture;
    private StructurePattern structurePattern;
    private BehaviourMultiMachine multiBehaviour;

    /** GUI Members **/
    private ResourceLocation guiTexture;
    private SlotData[] slots;
    private int inputCount, outputCount;
    private boolean isGuiTierSensitive;

    /** Powered Members **/
    private ArrayList<Tier> tiers; //TODO not specifically for power tiers?

    public Machine(String name) {
        this.name = name;
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

    public Recipe findRecipe(ItemStack[] inputs, FluidStack... fluidInputs) {
        return RecipeMap.findRecipeItem(name, inputs);
    }

    public Machine setGuiTierSensitive() {
        isGuiTierSensitive = true;
        return this;
    }

    /** Type Construction **/
    public Machine add(AbilityFlag... flags) {
        for (AbilityFlag flag : flags) {
            abilityMask = Utils.addFlag(abilityMask, flag.getBit());
            flag.add(this);
        }
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

    public Machine addGUI(Machine machineToCopy) {
        return addGUI(machineToCopy.slots);
    }

    public Machine addGUI(SlotData... slots) {
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

    public Machine addBehaviour(BehaviourMultiMachine behaviour) {
        multiBehaviour = behaviour;
        return this;
    }

    /** Getters **/
    public Block getBlock() {
        return block;
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

    public ResourceLocation getBaseTexture() {
        return baseTexture;
    }

    public StructurePattern getPattern() {
        return structurePattern;
    }

    public BehaviourMultiMachine getBehaviour() {
        return multiBehaviour;
    }
}
