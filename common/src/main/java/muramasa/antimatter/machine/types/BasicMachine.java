package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.gui.widget.*;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.blockentity.multi.BlockEntityMultiMachine;

import static muramasa.antimatter.machine.MachineFlag.*;

public class BasicMachine extends Machine<BasicMachine> {

    public BasicMachine(String domain, String id) {
        super(domain, id);
        addFlags(BASIC, EU, COVERABLE);
        setTile(BlockEntityMachine::new);
        setGUI(Data.BASIC_MENU_HANDLER);
    }

    @Override
    protected void setupGui() {
        super.setupGui();
        addGuiCallback(t -> {
            t.addWidget(WidgetSupplier.build((a, b) -> TextWidget.build(((AntimatterContainerScreen<?>) b).getTitle().getString(), 4210752, false).build(a, b)).setPos(9, 5).clientSide());
            if (has(RECIPE)) {
                t.addWidget(ProgressWidget.build())
                        .addWidget(MachineStateWidget.build());
            }
            if ((has(ITEM) || has(FLUID)))
                t.addWidget(IOWidget.build(9, 63).onlyIf(u -> u.handler instanceof BlockEntityMachine<?> machine &&
                        machine.getOutputFacing() != null &&
                        machine.coverHandler.map(c -> c.getOutputCover() instanceof CoverOutput).orElse(false) &&
                        !(u.handler instanceof BlockEntityMultiMachine<?>)));
        });
    }
}