package muramasa.gregtech.loaders;

import muramasa.gregtech.api.enums.GenerationFlag;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.recipe.RecipeBuilder;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import static muramasa.gregtech.api.data.Machines.*;
import static muramasa.gregtech.api.data.Materials.*;

public class MachineRecipeLoader {

    public static void init() {
        for (Material m : GenerationFlag.ORE.getMats()) {
            RecipeBuilder.add(PULVERIZER).ii(m.getChunk(1)).io(m.getCrushed(2)).t(40, 1).build();
            RecipeBuilder.add(THERMAL_CENTRIFUGE).ii(m.getCrushed(1)).io(m.getCrushedC(1), m.getDust(1), m.getDustT(4)).t(40, 1).build();
            RecipeBuilder.add(ORE_WASHER).ii(m.getCrushed(1)).fi(new FluidStack(FluidRegistry.WATER, 100)).io(Aluminium.getCrushedP(1), Aluminium.getDustT(1), Stone.getDust(1)).t(40, 1).build();
        }

        RecipeBuilder.add(ALLOY_SMELTER).ii(Copper.getIngot(1), Redstone.getDust(4)).io(RedAlloy.getIngot(1)).t(10, 1).build();
        RecipeBuilder.add(ALLOY_SMELTER).ii(Copper.getIngot(1), Cobalt.getDust(4)).io(RedAlloy.getIngot(16)).t(10, 1).build();

        RecipeBuilder.add(ELECTRIC_BLAST_FURNACE).ii(Silicon.getDust(1)).io(Silicon.getIngot(1)).t(10, 1).build();
        RecipeBuilder.add(ELECTRIC_BLAST_FURNACE).ii(Nickel.getIngot(4), Chrome.getIngot(1)).io(Nichrome.getIngotH(5), DarkAsh.getDustS(2)).t(10, 1).build();
        RecipeBuilder.add(ELECTRIC_BLAST_FURNACE).ii(Aluminium.getIngot(1), Aluminium.getIngot(1), Aluminium.getIngot(1)).io(Aluminium.getIngot(1), Aluminium.getIngot(1), Aluminium.getIngot(1)).t(10, 1).build();

        RecipeBuilder.add(PRIMITIVE_BLAST_FURNACE).ii(Coal.getGem(4), Iron.getIngot(1)).io(Steel.getIngot(1)).t(7200, 0).build();
        RecipeBuilder.add(BRONZE_BLAST_FURNACE).ii(Coal.getGem(4), Iron.getIngot(1)).io(Steel.getIngot(1)).t(7200, 0).build();
    }
}
