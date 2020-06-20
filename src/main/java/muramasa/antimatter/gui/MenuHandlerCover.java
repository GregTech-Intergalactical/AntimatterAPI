package muramasa.antimatter.gui;

import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.gui.screen.ScreenCover;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

public abstract class MenuHandlerCover<T extends ContainerCover, U extends ScreenCover<T>>  extends MenuHandler<T, U> {

    public MenuHandlerCover(String domain, String id) {
        super(domain, id);
    }

    @Override
    public T onContainerCreate(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileEntity tile = Utils.getTileFromBuf(data);
        if (tile instanceof TileEntityMachine) {
            Direction dir = Direction.byIndex(data.readInt());
            return getMenu(((TileEntityMachine)tile).coverHandler.get().getCoverInstance(dir),inv,windowId);
        }
        return null;
    }
}
