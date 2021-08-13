package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.gui.BarDir;
import muramasa.antimatter.gui.widget.*;
import muramasa.antimatter.tile.TileEntityMachine;

import static muramasa.antimatter.machine.MachineFlag.*;

public class BasicMachine extends Machine<BasicMachine> {

    public BasicMachine(String domain, String id) {
        super(domain, id);
        addFlags(BASIC, ENERGY, COVERABLE);
        setTile(() -> new TileEntityMachine<>(this));
        setGUI(Data.BASIC_MENU_HANDLER);

        addGuiCallback(t -> {
            t.addWidget(TextWidget.build(t.title.getString(), 4210752).setPos(9, 5));
            if (has(RECIPE)) {
                t.addWidget(ProgressWidget.build(BarDir.LEFT, true))
                        .addWidget(MachineStateWidget.build().setPos(84,46).setWH(8,8));
            }
            if ((has(ITEM) || has(FLUID))) t.addWidget(IOWidget.build(9,63,16,16));
        });
    }
}