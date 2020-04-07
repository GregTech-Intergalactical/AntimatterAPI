package muramasa.antimatter.tile;

import muramasa.antimatter.capability.impl.FluidResourceMachineRecipeHandler;
import muramasa.antimatter.material.Material;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;

public class TileEntitySteamMachine extends TileEntityMachine {

    protected static FluidStack STEAM = Material.get("steam").getGas(1);

    @Override
    public void onLoad() {
        if (isServerSide() && has(RECIPE)) recipeHandler = Optional.of(new FluidResourceMachineRecipeHandler<>(this, STEAM));
    }
}
