package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandlerPipe;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class ContainerPipe<T extends TileEntityPipe<?>> extends AntimatterContainer {

    protected T tile;

    public ContainerPipe(T tile, Inventory playerInv, MenuHandlerPipe<?> menuHandler, int windowId) {
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
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), player, tile.getBlockState().getBlock());
    }
}
