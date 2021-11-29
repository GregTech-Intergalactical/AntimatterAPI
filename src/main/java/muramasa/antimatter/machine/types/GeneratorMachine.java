package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.single.TileEntityGenerator;

import static muramasa.antimatter.machine.MachineFlag.*;

public class GeneratorMachine extends Machine<GeneratorMachine> {
    public GeneratorMachine(String domain, String id) {
        super(domain, id);
        addFlags(BASIC, ENERGY, COVERABLE, GENERATOR);
        setTile(() -> new TileEntityGenerator<>(this));
        setGUI(Data.BASIC_MENU_HANDLER);
        noCovers();
        custom();
        setAllowVerticalFacing(true);
    }
}
