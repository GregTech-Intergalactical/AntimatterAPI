package muramasa.gtu.api.data;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.recipe.RecipeBuilder;
import muramasa.gtu.api.registration.RegistrationEvent;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class RecipeBuilders {

    public static class BasicBlasting extends RecipeBuilder {

        public static ItemStack[] FUELS;

        static {
            GregTechAPI.onEvent(RegistrationEvent.DATA_BUILT, () -> FUELS = new ItemStack[] {
                Materials.Coal.getGem(1),
                Materials.Coal.getDust(1),
                Materials.Charcoal.getGem(1),
                Materials.Charcoal.getDust(1),
                Materials.CoalCoke.getGem(1),
                Materials.LigniteCoke.getGem(1)
            });
        }

        public void add(ItemStack[] inputs, ItemStack[] outputs, int coal, int duration) {
            duration = 20;//TODO temp
            ItemStack[] inputsCpy = Arrays.copyOf(inputs, inputs.length + 1);
            for (int i = 0; i < FUELS.length; i++) {
                inputsCpy[inputsCpy.length - 1] = Utils.ca(coal, FUELS[i]);
                ii(inputsCpy).io(outputs).add(duration);
            }
        }
    }
}
