package muramasa.antimatter.gui.container;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

public abstract class ContainerMachine extends AntimatterContainer {

    protected final TileEntityMachine tile;

    public ContainerMachine(TileEntityMachine tile, PlayerInventory playerInv, MenuHandlerMachine<?, ?> menuHandler, int windowId) {
        super(menuHandler.getContainerType(), windowId, playerInv, tile.getMachineType().getGui().getSlots(tile.getMachineTier()).size());
        this.tile = tile;
        addSlots(tile);
        if (tile.getMachineType().getGui().enablePlayerSlots()) addPlayerSlots();
        // tile.setClientProgress(0);
        if (tile.getMachineType().has(MachineFlag.RECIPE)) {
            trackIntArray(((TileEntityRecipeMachine) tile).getProgressData());
        }
    }

    public TileEntityMachine getTile() {
        return tile;
    }

    /*@Override
    public void detectAndSendChanges() {
        //int curState = tile.getMachineState().ordinal();
        super.detectAndSendChanges();
        tile.fluidHandler.ifPresent(h -> {
            if ((h.getInputWrapper() != null && h.getInputWrapper().dirty) || (h.getOutputWrapper() != null && h.getOutputWrapper().dirty)) {
                if (h.getInputWrapper() != null) h.getInputWrapper().dirty = false;
                if (h.getOutputWrapper() != null) h.getOutputWrapper().dirty = false;
                GregTechNetwork.syncMachineTanks(tile);
            }
        });
    }*/

  /*  @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        if (id == GuiEvent.MACHINE_STATE.ordinal()) {
            tile.setMachineState(MachineState.VALUES[data]);
        }
    }*/

    protected void addSlots(TileEntityMachine tile) {
        Object2IntMap<String> slotIndexMap = new Object2IntOpenHashMap<>();
        for (SlotData slot : tile.getMachineType().getGui().getSlots(tile.getMachineTier())) {
            slotIndexMap.computeIntIfAbsent(slot.getType().getId(), k -> 0);
            Optional<SlotItemHandler> supplier = slot.getType().getSlotSupplier().get(tile, slotIndexMap.getInt(slot.getType().getId()), slot);
            supplier.ifPresent(handler -> {
                addSlot(handler);
                slotIndexMap.compute(slot.getType().getId(), (k, v) -> v + 1);
            });
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, tile.getBlockState().getBlock());
    }
}
