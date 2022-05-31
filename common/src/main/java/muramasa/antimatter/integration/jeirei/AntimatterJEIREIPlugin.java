package muramasa.antimatter.integration.jeirei;

import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.recipe.map.IRecipeMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class AntimatterJEIREIPlugin{
    @ExpectPlatform
    public static void registerCategory(IRecipeMap map, GuiData gui, Tier tier, ResourceLocation model, boolean override) {
    }

    @ExpectPlatform
    public static void showCategory(Machine<?>... types) {
    }

    //To perform a JEI lookup for fluid. Use defines direction.
    @ExpectPlatform
    public static void uses(FluidStack val, boolean USE) {
    }

    @ExpectPlatform
    public static <T> void addModDescriptor(List<Component> tooltip, T t) {
    }
}
