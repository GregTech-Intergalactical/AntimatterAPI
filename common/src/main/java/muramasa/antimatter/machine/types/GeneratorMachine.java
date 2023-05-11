package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.single.TileEntityGenerator;

import static muramasa.antimatter.machine.MachineFlag.BASIC;
import static muramasa.antimatter.machine.MachineFlag.COVERABLE;
import static muramasa.antimatter.machine.MachineFlag.ENERGY;
import static muramasa.antimatter.machine.MachineFlag.GENERATOR;

public class GeneratorMachine extends Machine<GeneratorMachine> {
    public GeneratorMachine(String domain, String id) {
        super(domain, id);
        addFlags(BASIC, ENERGY, COVERABLE, GENERATOR);
        setTile(TileEntityGenerator::new);
        setGUI(Data.BASIC_MENU_HANDLER);
        noCovers();
        setAllowVerticalFacing(true);
    }
}
