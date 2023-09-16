package muramasa.antimatter.gui.container;

import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.gui.MenuHandlerMachine;
import net.minecraft.world.entity.player.Inventory;

public class ContainerMultiMachine<T extends BlockEntityBasicMultiMachine<T>> extends ContainerMachine<T> {

    public ContainerMultiMachine(T tile, Inventory playerInv, MenuHandlerMachine<T, ContainerMachine<T>> menuHandler, int windowId) {
        super(tile, playerInv, menuHandler, windowId);
    }
}
