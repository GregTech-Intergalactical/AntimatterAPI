package muramasa.antimatter.gui;

import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.ContainerPipe;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MenuHandlerPipe<T extends BlockEntityPipe<?>> extends MenuHandler<ContainerPipe<?>> {

    public MenuHandlerPipe(String domain, String id) {
        super(domain, id);
    }

    @Override
    protected ContainerPipe<?> getMenu(IGuiHandler source, Inventory playerInv, int windowId) {
        return source instanceof BlockEntityPipe ? new ContainerPipe((BlockEntityPipe<?>) source, playerInv, this, windowId) : null;
    }

    @Override
    public ContainerPipe<?> onContainerCreate(int windowId, Inventory inv, FriendlyByteBuf data) {
        BlockEntity tile = Utils.getTileFromBuf(data);
        boolean isMachine = tile instanceof BlockEntityPipe;
        if (isMachine) {
            BlockEntityPipe<?> machine = (BlockEntityPipe<?>) tile;
            return menu(machine, inv, windowId);
        }
        return null;
    }
}