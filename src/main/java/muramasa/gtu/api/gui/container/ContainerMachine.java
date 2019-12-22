package muramasa.gtu.api.gui.container;

import muramasa.gtu.api.gui.MenuHandler;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;

public abstract class ContainerMachine extends Container {

    protected TileEntityMachine tile;
    protected PlayerInventory playerInv;

    public ContainerMachine(int windowId) {
        super(null, windowId);
    }

    public ContainerMachine(TileEntityMachine tile, PlayerInventory playerInv, MenuHandler menuHandler, int windowId) {
        super(menuHandler.getContainerType(), windowId);
        this.tile = tile;
        this.playerInv = playerInv;
    }

    public TileEntityMachine getTile() {
        return tile;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, tile.getMachineType().getBlock(tile.getTier()));
    }
}
