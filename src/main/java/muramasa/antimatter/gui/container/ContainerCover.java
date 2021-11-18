package muramasa.antimatter.gui.container;

import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.MenuHandlerCover;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

import java.util.Objects;

public class ContainerCover extends AntimatterContainer {
    protected TileEntity onEntity;
    private final ICover c;
    private final MenuHandlerCover<?> m;

    public ContainerCover(ICover on, PlayerInventory playerInv, MenuHandlerCover<?> menuHandler, int windowId) {
        super(on, menuHandler.getContainerType(), windowId, playerInv, 0);
        this.c = on;
        this.m = menuHandler;
        if (c.getGui().enablePlayerSlots()) addPlayerSlots();
        this.onEntity = Objects.requireNonNull(on.source().getTile());
    }

    public ICover getCover() {
        return c;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return stillValid(IWorldPosCallable.create(onEntity.getLevel(), onEntity.getBlockPos()), playerIn, onEntity.getBlockState().getBlock());
    }
}
