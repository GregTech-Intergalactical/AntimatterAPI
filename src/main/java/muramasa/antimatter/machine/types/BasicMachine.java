package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.gui.BarDir;
import muramasa.antimatter.gui.widget.IOWidget;
import muramasa.antimatter.gui.widget.MachineStateWidget;
import muramasa.antimatter.gui.widget.ProgressWidget;
import muramasa.antimatter.tile.TileEntityMachine;

import static muramasa.antimatter.machine.MachineFlag.*;

public class BasicMachine extends Machine<BasicMachine> {

    public BasicMachine(String domain, String id) {
        super(domain, id);
        addFlags(BASIC, ENERGY, COVERABLE);
        setTile(() -> new TileEntityMachine<>(this));
        setGUI(Data.BASIC_MENU_HANDLER);

        addGuiCallback(t -> {
            if (has(RECIPE)) {
                getGui().widget(ProgressWidget.build(BarDir.LEFT, true))
                        .widget(MachineStateWidget.build().setPos(84,46).setWH(8,8));
            }
            getGui().widget(IOWidget.build(9,63,16,16));
        });
    }
}