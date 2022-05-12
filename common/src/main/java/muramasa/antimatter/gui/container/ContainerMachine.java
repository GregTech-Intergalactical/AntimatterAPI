package muramasa.antimatter.gui.container;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;

public abstract class ContainerMachine<T extends TileEntityMachine<T>> extends AntimatterContainer {

    protected final T tile;

    public ContainerMachine(T tile, Inventory playerInv, MenuHandlerMachine<T, ContainerMachine<T>> menuHandler, int windowId) {
        super(tile, menuHandler.getContainerType(), windowId, playerInv, tile.getMachineType().getSlots(tile.getMachineTier()).size());
        this.tile = tile;
        addSlots(tile);
        if (tile.getMachineType().getGui().enablePlayerSlots()) addPlayerSlots();
        //Ugly hack but syncing is broken otherwise.
        //if (!(playerInv.player instanceof ServerPlayerEntity)) {
        //    tile.recipeHandler.ifPresent(t -> t.setClientProgress(0));
        //}
        tile.addOpenContainer(this);
    }

    public T getTile() {
        return tile;
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        tile.onContainerClose(this);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
    }

    protected void addSlots(TileEntityMachine<?> tile) {
        Object2IntMap<String> slotIndexMap = new Object2IntOpenHashMap<>();
        for (SlotData<?> slot : tile.getMachineType().getSlots(tile.getMachineTier())) {
            slotIndexMap.computeIntIfAbsent(slot.getType().getId(), k -> 0);
            Slot supplier = slot.getType().getSlotSupplier().get((SlotType) slot.getType(), tile, tile.itemHandler.map(MachineItemHandler::getAll).orElse(null), slotIndexMap.getInt(slot.getType().getId()), (SlotData) slot);
            addSlot(supplier);
            slotIndexMap.computeInt(slot.getType().getId(), (a, b) -> {
                if (b == null) return 0;
                return b + 1;
            });
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), player, tile.getBlockState().getBlock());
    }
}
