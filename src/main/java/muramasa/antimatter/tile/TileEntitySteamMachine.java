package muramasa.antimatter.tile;

import muramasa.antimatter.Data;
import muramasa.antimatter.capability.impl.FluidResourceMachineRecipeHandler;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.Material;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;

//TODO move to GT
public class TileEntitySteamMachine extends TileEntityMachine {

    private static FluidStack STEAM;

    public TileEntitySteamMachine(Machine<?> type) {
        super(type);
    }

    @Override
    public void onLoad() {
        if (isServerSide() && has(RECIPE)) recipeHandler = Optional.of(new FluidResourceMachineRecipeHandler<>(this, STEAM));
        super.onLoad();
    }

    public FluidStack getSteam() {
        return STEAM != null ? STEAM : (STEAM = Data.GAS.get().get(Material.get("steam"), 1));
    }
}
