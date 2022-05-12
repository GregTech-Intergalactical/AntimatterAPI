package muramasa.antimatter.gui;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.ContainerPipe;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MenuHandlerPipe<T extends TileEntityPipe<?>> extends MenuHandler<ContainerPipe<?>> {

    public MenuHandlerPipe(String domain, String id) {
        super(domain, id);
    }

    @Override
    protected ContainerPipe<?> getMenu(IGuiHandler source, Inventory playerInv, int windowId) {
        return source instanceof TileEntityPipe ? new ContainerPipe((TileEntityPipe<?>) source, playerInv, this, windowId) : null;
    }

    @Override
    public ContainerPipe<?> onContainerCreate(int windowId, Inventory inv, FriendlyByteBuf data) {
        BlockEntity tile = Utils.getTileFromBuf(data);
        boolean isMachine = tile instanceof TileEntityPipe;
        if (isMachine) {
            TileEntityPipe<?> machine = (TileEntityPipe<?>) tile;
            return menu(machine, inv, windowId);
        }
        return null;
    }
}