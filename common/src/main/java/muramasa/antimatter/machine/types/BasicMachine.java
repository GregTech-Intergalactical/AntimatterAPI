package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.gui.BarDir;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.gui.widget.IOWidget;
import muramasa.antimatter.gui.widget.MachineStateWidget;
import muramasa.antimatter.gui.widget.ProgressWidget;
import muramasa.antimatter.gui.widget.TextWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.tile.TileEntityMachine;

import static muramasa.antimatter.machine.MachineFlag.*;

public class BasicMachine extends Machine<BasicMachine> {

    public BasicMachine(String domain, String id) {
        super(domain, id);
        addFlags(BASIC, ENERGY, COVERABLE);
        setTile(TileEntityMachine::new);
        setGUI(Data.BASIC_MENU_HANDLER);
    }

    @Override
    protected void setupGui() {
        super.setupGui();
        addGuiCallback(t -> {
            t.addWidget(WidgetSupplier.build((a, b) -> TextWidget.build(((AntimatterContainerScreen<?>) b).getTitle().getString(), 4210752).build(a, b)).setPos(9, 5).clientSide());
            if (has(RECIPE)) {
                t.addWidget(ProgressWidget.build())
                        .addWidget(MachineStateWidget.build().setPos(84, 46).setWH(8, 8));
            }
            if ((has(ITEM) || has(FLUID)))
                t.addWidget(IOWidget.build(9, 63, 16, 16).onlyIf(u -> u.handler.getClass() == TileEntityMachine.class));
        });
    }
}