package muramasa.gtu.loaders;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.recipe.RecipeHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CraftingRecipeLoader {

    public static void init() {
        RecipeHelper.addShaped(Ref.MODID + "hopper", new ItemStack(Blocks.HOPPER), "IwI", "ICI", " I ", 'I', Materials.Iron.getPlate(1), 'C', new ItemStack(Blocks.CHEST));
    }
}
