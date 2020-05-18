package muramasa.antimatter.gui.container;

import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.gui.MenuHandlerCover;
import muramasa.antimatter.gui.MenuHandlerMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

public class ContainerCover extends AntimatterContainer {
    protected TileEntity onEntity;
    private Cover c;
    private MenuHandlerMachine m;
    public ContainerCover(Cover on, PlayerInventory playerInv, MenuHandlerCover menuHandler, int windowId) {
        super(menuHandler.getContainerType(), windowId, playerInv, 0);
        this.c = on;
        this.onEntity = on.getTileOn();
    }

    public Cover getCover() {
        return c;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(onEntity.getWorld(), onEntity.getPos()), playerIn, onEntity.getBlockState().getBlock());
    }
}
