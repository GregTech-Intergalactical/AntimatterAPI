package muramasa.antimatter.gui.container;

import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IWorldPosCallable;
import speiger.src.collections.objects.maps.impl.hash.Object2IntOpenHashMap;
import speiger.src.collections.objects.maps.interfaces.Object2IntMap;

import java.util.Collections;

public abstract class ContainerMachine<T extends TileEntityMachine<T>> extends AntimatterContainer {

    protected final T tile;

    public ContainerMachine(T tile, PlayerInventory playerInv, MenuHandlerMachine<T, ContainerMachine<T>> menuHandler, int windowId) {
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
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        tile.onContainerClose(this);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    protected void addSlots(TileEntityMachine<?> tile) {
        Object2IntMap<String> slotIndexMap = new Object2IntOpenHashMap<>();
        for (SlotData<?> slot : tile.getMachineType().getSlots(tile.getMachineTier())){
            slotIndexMap.computeIntIfAbsent(slot.getType().getId(), k -> 0);
            Slot supplier = slot.getType().getSlotSupplier().get((SlotType) slot.getType(), tile, tile.itemHandler.map(MachineItemHandler::getAll).orElse(null), slotIndexMap.getInt(slot.getType().getId()), (SlotData) slot);
            addSlot(supplier);
            slotIndexMap.compute(slot.getType().getId(), (k, v) -> v + 1);
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, tile.getBlockState().getBlock());
    }
}
