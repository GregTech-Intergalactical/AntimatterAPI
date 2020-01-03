package muramasa.antimatter.machines.types;

import muramasa.gtu.data.Guis;
import muramasa.antimatter.tileentities.TileEntityMachine;
import muramasa.antimatter.tileentities.TileEntityRecipeMachine;

import java.util.function.Supplier;

import static muramasa.antimatter.machines.MachineFlag.*;

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
