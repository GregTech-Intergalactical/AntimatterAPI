package muramasa.antimatter.gui.container;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.slot.AbstractSlot;
import muramasa.antimatter.network.packets.FluidStackPacket;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IWorldPosCallable;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerMachine<T extends TileEntityMachine<T>> extends AntimatterContainer {

    protected final T tile;
    protected List<ServerPlayerEntity> listeners = new ArrayList<>();

    public ContainerMachine(T tile, PlayerInventory playerInv, MenuHandlerMachine<T, ContainerMachine<T>> menuHandler, int windowId) {
        super(menuHandler.getContainerType(), windowId, playerInv, tile.getMachineType().getGui().getSlots(tile.getMachineTier()).size());
        this.tile = tile;
        addSlots(tile);
        if (tile.getMachineType().getGui().enablePlayerSlots()) addPlayerSlots();
        tile.recipeHandler.ifPresent(r -> trackIntArray(r.getProgressData()));
        //Ugly hack but syncing is broken otherwise.
        if (!(playerInv.player instanceof ServerPlayerEntity)) {
            tile.recipeHandler.ifPresent(t -> t.setClientProgress(0));
        }
        tile.addOpenContainer(this);
    }

    public T getTile() {
        return tile;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        if (listener instanceof ServerPlayerEntity) listeners.add((ServerPlayerEntity)listener);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        tile.onContainerClose(this);
    }

    @Override
    public void removeListener(IContainerListener listener) {
        super.removeListener(listener);
        if (listener instanceof ServerPlayerEntity) listeners.remove(listener);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        detectAndSendLiquidChanges();
    }

    public void detectAndSendLiquidChanges() {
        tile.fluidHandler.ifPresent(fh -> {
            if (!fh.isDirty()) return;

            FluidStackPacket pkt = new FluidStackPacket(tile.getPos(),fh.getInputs(), fh.getOutputs());
            listeners.forEach(t -> Antimatter.NETWORK.sendTo(pkt,t));

            fh.markSynced();
        });
    }



    protected void addSlots(TileEntityMachine tile) {
        Object2IntMap<String> slotIndexMap = new Object2IntOpenHashMap<>();
        for (SlotData slot : tile.getMachineType().getGui().getSlots(tile.getMachineTier())){
            slotIndexMap.computeIntIfAbsent(slot.getType().getId(), k -> 0);
            Slot supplier = slot.getType().getSlotSupplier().get(slot.getType(), tile, slotIndexMap.getInt(slot.getType().getId()), slot);
            addSlot(supplier);
            slotIndexMap.compute(slot.getType().getId(), (k, v) -> v + 1);
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, tile.getBlockState().getBlock());
    }
}
