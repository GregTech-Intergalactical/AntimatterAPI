package muramasa.antimatter.machines.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tileentities.TileEntityMachine;
import muramasa.antimatter.tileentities.TileEntityRecipeMachine;

import java.util.function.Supplier;

import static muramasa.antimatter.machines.MachineFlag.*;

public class BasicMachine extends Machine {

    public BasicMachine(String domain, String id, Supplier<? extends TileEntityMachine> tile, Object... data) {
        super(domain, id, tile, data);
        addFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
    }

    public BasicMachine(String domain, String name, Object... data) {
        this(domain, name, TileEntityRecipeMachine::new, data);
    }
}
