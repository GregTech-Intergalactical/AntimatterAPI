package muramasa.gtu.api.recipe;

import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class RecipeAdder {

    private static RecipeBuilder RB = new RecipeBuilder();

    public static ItemStack[] BLAST_FUELS = new ItemStack[] {
        Materials.Coal.getGem(1),
        Materials.Coal.getDust(1),
        Materials.Charcoal.getGem(1),
        Materials.Charcoal.getDust(1),
        Materials.CoalCoke.getGem(1),
        Materials.LigniteCoke.getGem(1)
    };
    public static void addBlastRecipe(ItemStack[] inputs, ItemStack[] outputs, int coal, int duration) {
        duration = 20;//TODO temp
        ItemStack[] inputsCpy = Arrays.copyOf(inputs, inputs.length + 1);
        for (int i = 0; i < BLAST_FUELS.length; i++) {
            inputsCpy[inputsCpy.length - 1] = Utils.ca(coal, BLAST_FUELS[i]);
            RB.get(Machines.PRIMITIVE_BLAST_FURNACE).ii(inputsCpy).io(outputs).add(duration);
            RB.get(Machines.BRONZE_BLAST_FURNACE).ii(inputsCpy).io(outputs).add(duration);
        }
    }
}
