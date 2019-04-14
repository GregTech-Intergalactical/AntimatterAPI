package muramasa.gtu.api.machines.types;

import muramasa.gtu.api.tileentities.TileEntityFluidMachine;

import static muramasa.gtu.api.machines.MachineFlag.FLUID;

public class FluidMachine extends BasicMachine {

    public FluidMachine(String name) {
        super(name, TileEntityFluidMachine.class);
        addFlags(FLUID);
//        getGui().add(CELL_IN, 35, 63).add(CELL_OUT, 125, 63).add(FL_IN, 53, 63).add(FL_OUT, 107, 63);
    }

    public FluidMachine(String name, Class tileClass) {
        this(name);
        setTileClass(tileClass);
    }
}
