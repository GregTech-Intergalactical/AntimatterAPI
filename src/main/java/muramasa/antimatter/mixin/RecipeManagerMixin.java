package muramasa.antimatter.mixin;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
@Mixin(RecipeManager.class)
public interface RecipeManagerMixin {
    @Accessor("recipes")
    Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> getRecipes();

    @Accessor("recipes")
    void setRecipes(Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes);
}
