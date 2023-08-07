package muramasa.antimatter.gui.container;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.MenuHandlerCover;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.Objects;

public class ContainerCover extends AntimatterContainer {
    protected BlockEntity onEntity;
    private final ICover c;
    private final MenuHandlerCover<?> m;

    public ContainerCover(ICover on, Inventory playerInv, MenuHandlerCover<?> menuHandler, int windowId) {
        super(on, menuHandler.getContainerType(), windowId, playerInv, 0);
        this.c = on;
        this.m = menuHandler;
        addSlots(c);
        if (c.getGui().enablePlayerSlots()) addPlayerSlots();
        this.onEntity = Objects.requireNonNull(on.source().getTile());
    }

    protected void addSlots(ICover cover) {
        Object2IntMap<String> slotIndexMap = new Object2IntOpenHashMap<>();
        if (cover.getGui().getSlots() != null){
            List<SlotData<?>> slots = cover.getTier() == null ? cover.getGui().getSlots().getAnySlots() : cover.getGui().getSlots().getSlots(cover.getTier());
            for (SlotData<?> slot : slots) {
                slotIndexMap.computeIntIfAbsent(slot.getType().getId(), k -> 0);
                Slot supplier = slot.getType().getSlotSupplier().get((SlotType) slot.getType(), cover, cover.getAll(), slotIndexMap.getInt(slot.getType().getId()), (SlotData) slot);
                addSlot(supplier);
                slotIndexMap.computeInt(slot.getType().getId(), (a, b) -> {
                    if (b == null) return 0;
                    return b + 1;
                });
            }
        }

    }

    public ICover getCover() {
        return c;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(onEntity.getLevel(), onEntity.getBlockPos()), playerIn, onEntity.getBlockState().getBlock());
    }
}
