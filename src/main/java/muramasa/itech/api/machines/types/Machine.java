package muramasa.itech.api.machines.types;

import muramasa.itech.ITech;
import muramasa.itech.api.machines.objects.Tier;
import muramasa.itech.api.recipe.Recipe;
import muramasa.itech.api.recipe.RecipeMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class Machine implements IStringSerializable {

    private String name, displayName, jeiCategoryID, jeiCategoryName;
    private RecipeMap recipeMap;
    private ResourceLocation guiTexture, overlayTexture;
    private ModelResourceLocation overlayModel;
    private boolean isGuiTierSensitive;

    public Machine(String name, boolean hasRecipes) {
        this.name = name;
        guiTexture = new ResourceLocation(ITech.MODID, "textures/gui/machines/" + name + ".png");
        overlayTexture = new ResourceLocation(ITech.MODID + ":blocks/machines/overlays/" + name);
        overlayModel = new ModelResourceLocation(ITech.MODID + ":machineparts/overlays/" + name);
        if (hasRecipes) {
            this.jeiCategoryID = "it.recipemap." + name;
            this.jeiCategoryName = I18n.format("jei.category." + name + ".name");
            recipeMap = new RecipeMap(this);
        }
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
}
