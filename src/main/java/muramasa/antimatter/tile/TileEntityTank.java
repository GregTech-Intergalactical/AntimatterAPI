package muramasa.antimatter.tile;

import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.machine.types.Machine;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

public class TileEntityTank<T extends TileEntityMachine<T>> extends TileEntityMachine<T> {

    public TileEntityTank(Machine<?> type) {
        super(type);
        fluidHandler.set(() -> new MachineFluidHandler<T>((T) this) {
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
