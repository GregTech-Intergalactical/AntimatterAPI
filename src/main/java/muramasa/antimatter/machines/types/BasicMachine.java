package muramasa.antimatter.machines.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tileentities.TileEntityMachine;
import muramasa.antimatter.tileentities.TileEntityRecipeMachine;

import java.util.function.Supplier;

import static muramasa.antimatter.machines.MachineFlag.*;

public class BasicMachine extends Machine {

    public BasicMachine(String namespace, String id, Supplier<? extends TileEntityMachine> tile, Object... data) {
        super(namespace, id, tile, data);
        addFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
    }

    public BasicMachine(String namespace, String name, Object... data) {
        this(namespace, name, TileEntityRecipeMachine::new, data);
    }
}
