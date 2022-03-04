package muramasa.antimatter.integration.jei.category;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeRegistration;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class MultiMachineInfoCategory implements IRecipeCategory<MultiMachineInfoPage> {
    private static IGuiHelper guiHelper;
    private final IDrawable background;
    private final IDrawable icon;
    private static final ResourceLocation UID = new ResourceLocation(Ref.SHARED_ID, "multi_machine_info");
    private static final Set<MultiMachineInfoPage> MULTI_MACHINES_PAGES = Sets.newHashSet();

    public MultiMachineInfoCategory() {
        this.background = guiHelper.createBlankDrawable(176, 150);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Data.DEBUG_SCANNER, 1));
    }

    public static void setGuiHelper(IGuiHelper guiHelper) {
        MultiMachineInfoCategory.guiHelper = guiHelper;
    }
    
    public static void addMultiMachine(MultiMachineInfoPage page) {
        MULTI_MACHINES_PAGES.add(page);
    }

    public static void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(MULTI_MACHINES_PAGES, UID);
    }

    @Override
    public boolean handleClick(@Nonnull MultiMachineInfoPage recipe, double mouseX, double mouseY, int mouseButton) {
        return recipe.handleClick(mouseX, mouseY, mouseButton);
    }

    @Override
    public void draw(@Nonnull MultiMachineInfoPage recipe, @Nonnull PoseStack matrixStack, double mouseX, double mouseY) {
        recipe.drawInfo(matrixStack, (int)mouseX, (int)mouseY);
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public Class<? extends MultiMachineInfoPage> getRecipeClass() {
        return MultiMachineInfoPage.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return new TextComponent( "Multi Machines Title");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(@Nonnull MultiMachineInfoPage recipe, @Nonnull IIngredients ingredients) {
        recipe.setIngredients(ingredients);
    }
    
    @Nonnull
    @Override
    public List<Component> getTooltipStrings(@Nonnull MultiMachineInfoPage recipe, double mouseX, double mouseY) {
        return recipe.getTooltipStrings(mouseX, mouseY);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull MultiMachineInfoPage recipe, @Nonnull IIngredients ingredients) {
        recipe.setRecipeLayout(recipeLayout, guiHelper);
    }

}
