package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.TileEntityRecipeMachine;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class BasicMachine extends Machine {

    public BasicMachine(String name, Class<? extends TileEntityMachine> tileClass, Object... data) {
        super(name, tileClass, data);
        setFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        setGUI(GregTech.INSTANCE, Ref.GUI_ID_MACHINE);
    }

    public BasicMachine(String name, Object... data) {
        this(name, TileEntityRecipeMachine.class, data);
    }
}
