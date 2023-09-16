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
import muramasa.antimatter.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

@SuppressWarnings("removal")
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
    public boolean handleClick(@NotNull MultiMachineInfoPage recipe, double mouseX, double mouseY, int mouseButton) {
        return recipe.handleClick(mouseX, mouseY, mouseButton);
    }

    @Override
    public void draw(@NotNull MultiMachineInfoPage recipe, @NotNull PoseStack matrixStack, double mouseX, double mouseY) {
        recipe.drawInfo(matrixStack, (int)mouseX, (int)mouseY);
    }

    @NotNull
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @NotNull
    @Override
    public Class<? extends MultiMachineInfoPage> getRecipeClass() {
        return MultiMachineInfoPage.class;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return Utils.literal( "Multi Machines Title");
    }

    @NotNull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @NotNull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(@NotNull MultiMachineInfoPage recipe, @NotNull IIngredients ingredients) {
        recipe.setIngredients(ingredients);
    }
    
    @NotNull
    @Override
    public List<Component> getTooltipStrings(@NotNull MultiMachineInfoPage recipe, double mouseX, double mouseY) {
        return recipe.getTooltipStrings(mouseX, mouseY);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull MultiMachineInfoPage recipe, @NotNull IIngredients ingredients) {
        recipe.setRecipeLayout(recipeLayout, guiHelper);
    }

}
