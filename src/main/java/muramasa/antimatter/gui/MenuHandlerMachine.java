package muramasa.antimatter.gui;

import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.screen.ScreenMachine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

public abstract class MenuHandlerMachine<T extends ContainerMachine, U extends ScreenMachine<T>> extends MenuHandler<T, U> {

    public MenuHandlerMachine(String domain, String id) {
        super(domain, id);
    }

    @Override
    public T onContainerCreate(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileEntity tile = Utils.getTileFromBuf(data);
        return tile instanceof TileEntityMachine ? getMenu(tile, inv, windowId) : null;
    }
}
