package muramasa.antimatter.mixin;

import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(/*remap = false,*/method = "getRecipes(Lnet/minecraft/item/crafting/IRecipeType;)Ljava/util/Map;", at = @At("RETURN"), cancellable = true)
    private void handleGetRecipe(IRecipeType recipeTypeIn, CallbackInfoReturnable<Map<ResourceLocation, IRecipe>> cir) {
        Map<ResourceLocation, IRecipe> map = AntimatterRecipeProvider.handleGetRecipe(recipeTypeIn, cir.getReturnValue());
        if (map.size() != cir.getReturnValue().size()) cir.setReturnValue(map);
    }
}
