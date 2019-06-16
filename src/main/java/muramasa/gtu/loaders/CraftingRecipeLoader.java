package muramasa.gtu.loaders;

import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.recipe.RecipeHelper;
import muramasa.gtu.common.Data;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CraftingRecipeLoader {

    public static void init() {
        RecipeHelper.addShaped("hopper", Materials.Copper.getIngot(1), "IxI", "ICI", " I ", 'I', Materials.Vibranium.getIngot(1), 'C', new ItemStack(Blocks.CHEST));
        RecipeHelper.addShapeless("scanner", Data.DebugScanner.get(1), Materials.Vibranium.getIngot(1), Materials.Iridium.getIngot(1));
    }
}
