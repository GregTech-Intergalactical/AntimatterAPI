package muramasa.antimatter.gui;

import muramasa.antimatter.client.ClientData;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

public abstract class MenuHandlerMachine<T extends TileEntityMachine<T>, U extends ContainerMachine<T>> extends MenuHandler<U> {

    public MenuHandlerMachine(String domain, String id) {
        super(domain, id);
    }

    @Override
    public U onContainerCreate(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileEntity tile = Utils.getTileFromBuf(data);
        boolean isMachine = tile instanceof TileEntityMachine;
        if (isMachine) {
            TileEntityMachine<?> machine = (TileEntityMachine) tile;
            return menu(machine, inv, windowId);
        }
        return null;
    }

    @Override
    public Object screen() {
        return ClientData.SCREEN_MACHINE;
    }
}
