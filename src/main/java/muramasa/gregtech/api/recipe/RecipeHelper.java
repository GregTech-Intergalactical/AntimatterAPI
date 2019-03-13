package muramasa.gregtech.api.recipe;

import muramasa.gregtech.Ref;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RecipeHelper {

    public static void addShaped(ItemStack output, Object... data) {
        if (output.getItem().getRegistryName() == null) throw new NullPointerException("addShaped: output registry name null");
        GameRegistry.addShapedRecipe(output.getItem().getRegistryName(), new ResourceLocation(Ref.MODID, "shaped"), output, data);
    }

    public static void addShapeless(ItemStack output, ItemStack... inputs) {
        if (output.getItem().getRegistryName() == null) throw new NullPointerException("addShapeless: output registry name null");
        GameRegistry.addShapelessRecipe(output.getItem().getRegistryName(), new ResourceLocation(Ref.MODID, "shapeless"), output, Ingredient.fromStacks(inputs));
    }

    public static void addSmelting(ItemStack input, ItemStack output, float xp) {
        GameRegistry.addSmelting(input, output, xp);
    }

    public static void addSmelting(ItemStack input, ItemStack output) {
        addSmelting(input, output, 1.0f);
    }

    //TODO is this really needed?
    public static void removeSmelting(ItemStack input) {

    }
}
