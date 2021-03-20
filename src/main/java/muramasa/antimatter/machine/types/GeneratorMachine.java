package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.single.TileEntityGenerator;


import static muramasa.antimatter.Data.COVERDYNAMO;
import static muramasa.antimatter.machine.MachineFlag.*;
import static muramasa.antimatter.machine.MachineFlag.CONFIGURABLE;

public class GeneratorMachine extends Machine<BasicMachine> {
    public GeneratorMachine(String domain, String id, Object... data) {
        super(domain, id,data);
        addFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE,GENERATOR);
        setTile(() -> new TileEntityGenerator(this));
        setGUI(Data.BASIC_MENU_HANDLER);
        covers(COVERDYNAMO);
    }

}
