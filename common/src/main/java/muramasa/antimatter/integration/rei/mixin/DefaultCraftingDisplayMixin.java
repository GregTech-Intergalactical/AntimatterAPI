package muramasa.antimatter.integration.rei.mixin;

import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import muramasa.antimatter.recipe.material.MaterialRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(DefaultCraftingDisplay.class)
public abstract class DefaultCraftingDisplayMixin<C extends Recipe<?>> extends BasicDisplay {
    public DefaultCraftingDisplayMixin(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    @Inject(method = "<init>(Ljava/util/List;Ljava/util/List;Ljava/util/Optional;Ljava/util/Optional;)V", at = @At("TAIL"))
    private void injectInit(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location, Optional<C> recipe, CallbackInfo ci){
        if (recipe.map((r) -> r instanceof MaterialRecipe).orElse(false)) {
            this.inputs = new ArrayList<>(this.inputs);
            this.outputs = new ArrayList<>(this.outputs);
        }

    }
}
