package muramasa.gregtech.api.recipe;

import muramasa.gregtech.common.utils.Ref;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RecipeHelper {

    public static void addShaped(String name, String group, ItemStack output, Object... data) {
        GameRegistry.addShapedRecipe(new ResourceLocation(Ref.MODID, name), new ResourceLocation(Ref.MODID, group), output, data);
    }

    public static void addShaped(ItemStack output, Object... data) {
        ResourceLocation registryName = output.getItem().getRegistryName();
        if (registryName != null) {
            addShaped(registryName.toString(), "shaped", output, data);
        }
    }

    public static void addShapeless(String name, String group, ItemStack output, ItemStack... inputs) {
        GameRegistry.addShapelessRecipe(new ResourceLocation(Ref.MODID, name), new ResourceLocation(Ref.MODID, group), output, Ingredient.fromStacks(inputs));
    }

    public static void addShapeless(ItemStack output, ItemStack... inputs) {
        ResourceLocation registryName = output.getItem().getRegistryName();
        if (registryName != null) {
            addShapeless(registryName.toString(), "shapeless", output, inputs);
        }
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
