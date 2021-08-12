package muramasa.antimatter.gui.container;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.gui.MenuHandlerCover;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

import java.util.Objects;

public class ContainerCover extends AntimatterContainer {
    protected TileEntity onEntity;
    private CoverStack<?> c;
    private MenuHandlerCover<?> m;

    public ContainerCover(CoverStack<?> on, PlayerInventory playerInv, MenuHandlerCover<?> menuHandler, int windowId) {
        super(on, menuHandler.getContainerType(), windowId, playerInv, 0);
        this.c =  on;
        this.m = menuHandler;
        if (c.getCover().getGui().enablePlayerSlots()) addPlayerSlots();
        this.onEntity = Objects.requireNonNull(c.getTile());
    }

    public CoverStack<?> getCover() {
        return c;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(onEntity.getWorld(), onEntity.getPos()), playerIn, onEntity.getBlockState().getBlock());
    }
}
