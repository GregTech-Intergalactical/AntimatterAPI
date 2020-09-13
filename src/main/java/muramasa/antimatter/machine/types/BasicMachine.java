package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.TileEntityRecipeMachine;

import static muramasa.antimatter.machine.MachineFlag.*;

public class BasicMachine extends Machine<BasicMachine> {

    public BasicMachine(String domain, String id, Object... data) {
        super(domain, id, data);
        addFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        if (has(RECIPE)) {
            setTile(() -> new TileEntityRecipeMachine(this));
        } else {
            setTile(() -> new TileEntityMachine(this));
        }
        setGUI(Data.BASIC_MENU_HANDLER);
    }
}