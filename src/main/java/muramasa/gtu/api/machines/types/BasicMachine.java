package muramasa.gtu.api.machines.types;

import muramasa.gtu.api.data.Guis;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.TileEntityRecipeMachine;

import java.util.function.Supplier;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class BasicMachine extends Machine {

    public BasicMachine(String name, Supplier<? extends TileEntityMachine> tile, Object... data) {
        super(name, tile, data);
        addFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        setGUI(Guis.BASIC_MENU_HANDLER);
    }

    public BasicMachine(String name, Object... data) {
        this(name, TileEntityRecipeMachine::new, data);
    }
}
