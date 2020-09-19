package muramasa.antimatter.gui.container;

import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.gui.MenuHandlerCover;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ContainerCover extends AntimatterContainer {
    protected TileEntity onEntity;
    private CoverInstance<?> c;
    private MenuHandlerCover<?, ?> m;

    public ContainerCover(CoverInstance<?> on, PlayerInventory playerInv, MenuHandlerCover<?, ?> menuHandler, int windowId) {
        super(menuHandler.getContainerType(), windowId, playerInv, 0);
        this.c =  on;
        this.m = menuHandler;
        if (c.getCover().getGui().enablePlayerSlots()) addPlayerSlots();
        this.onEntity = Objects.requireNonNull(c.getTile());
    }

    public CoverInstance<?> getInstance() {
        return c;
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(onEntity.getWorld(), onEntity.getPos()), playerIn, onEntity.getBlockState().getBlock());
    }
}
