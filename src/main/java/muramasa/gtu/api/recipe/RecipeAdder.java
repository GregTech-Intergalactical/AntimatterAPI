package muramasa.gtu.api.recipe;

import muramasa.gtu.api.data.Machines;
import net.minecraft.item.ItemStack;

public class RecipeAdder {

    public static void addBasicBlast(ItemStack[] inputs, ItemStack[] outputs, int coal, int duration) {
        Machines.PRIMITIVE_BLAST_FURNACE.RB().add(inputs, outputs, coal, duration);
        Machines.BRONZE_BLAST_FURNACE.RB().add(inputs, outputs, coal, duration);
    }
}
