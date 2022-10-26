package muramasa.antimatter.gui.screen;

import muramasa.antimatter.gui.container.ContainerMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenPipe extends ScreenMachine {
    public ScreenPipe(ContainerMachine container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void init() {
        super.init();
    }
}
