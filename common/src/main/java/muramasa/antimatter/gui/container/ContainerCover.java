package muramasa.antimatter.gui.container;

import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.MenuHandlerCover;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class ContainerCover extends AntimatterContainer {
    protected BlockEntity onEntity;
    private final ICover c;
    private final MenuHandlerCover<?> m;

    public ContainerCover(ICover on, Inventory playerInv, MenuHandlerCover<?> menuHandler, int windowId) {
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
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(onEntity.getLevel(), onEntity.getBlockPos()), playerIn, onEntity.getBlockState().getBlock());
    }
}
