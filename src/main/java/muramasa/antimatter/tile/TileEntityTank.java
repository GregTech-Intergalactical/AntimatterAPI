package muramasa.antimatter.tile;

import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.util.LazyHolder;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

public class TileEntityTank extends TileEntityMachine {

    public TileEntityTank(Machine<?> type) {
        super(type);
        this.fluidHandler = LazyOptional.of(() -> new MachineFluidHandler<TileEntityTank>(this) {
            @Nullable
            @Override
            public FluidTanks getOutputTanks() {
                return super.getInputTanks();
            }

            @Override
            protected FluidTank getTank(int tank) {
                return getInputTanks().getTank(tank);
            }

            @Override
            public FluidTanks getTanks(int tank) {
                return getInputTanks();
            }
        });
    }
}
