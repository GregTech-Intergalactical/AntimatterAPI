package muramasa.antimatter.gui.container;

import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.gui.MenuHandlerMachine;
import net.minecraft.world.entity.player.Inventory;

public class ContainerBasicMachine<T extends BlockEntityMachine<T>> extends ContainerMachine<T> {

    public ContainerBasicMachine(T tile, Inventory playerInv, MenuHandlerMachine<T, ContainerMachine<T>> handler, int windowId) {
        super(tile, playerInv, handler, windowId);
    }
}
