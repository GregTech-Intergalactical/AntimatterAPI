package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import net.minecraft.world.item.crafting.Ingredient;

public interface MachineRecipeSchema {
    RecipeKey<String> MAP = StringComponent.NON_EMPTY.key("map");
    RecipeKey<InputItem[]> INPUT_ITEMS = ItemComponents.INPUT_ARRAY.key("inputItems");
    RecipeKey<OutputItem[]> OUTPUT_ITEMS = ItemComponents.OUTPUT_ARRAY.key("outputItems");
}
