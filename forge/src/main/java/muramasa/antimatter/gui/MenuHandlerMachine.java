package muramasa.antimatter.gui;

import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class MenuHandlerMachine<T extends TileEntityMachine<T>, U extends ContainerMachine<T>> extends MenuHandler<U> {

    public MenuHandlerMachine(String domain, String id) {
        super(domain, id);
    }

    @Override
    public U onContainerCreate(int windowId, Inventory inv, FriendlyByteBuf data) {
        BlockEntity tile = Utils.getTileFromBuf(data);
        boolean isMachine = tile instanceof TileEntityMachine;
        if (isMachine) {
            TileEntityMachine<?> machine = (TileEntityMachine) tile;
            return menu(machine, inv, windowId);
        }
        return null;
    }
}
