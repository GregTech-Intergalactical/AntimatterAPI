package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandlerPipe;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IWorldPosCallable;

public class ContainerPipe<T extends TileEntityPipe<?>> extends AntimatterContainer {

    protected T tile;

    public ContainerPipe(T tile, PlayerInventory playerInv, MenuHandlerPipe<?> menuHandler, int windowId) {
        super(tile, menuHandler.getContainerType(), windowId, playerInv, 0);
        //super(menuHandler.getContainerType(), windowId, playerInv, tile.getMachineType().getSlots(tile.getMachineTier()).size());
        //addSlots(tile);
        //if (tile.getMachineType().getGui().enablePlayerSlots()) addPlayerSlots();
        addPlayerSlots();
        this.tile = tile;
    }

    public T getTile() {
        return tile;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, tile.getBlockState().getBlock());
    }
}
