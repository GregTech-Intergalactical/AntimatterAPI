package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IWorldPosCallable;

public class ContainerPipe extends AntimatterContainer {

    protected TileEntityPipe tile;
    private int lastState = -1;

    public ContainerPipe(TileEntityPipe tile, PlayerInventory playerInv, MenuHandlerMachine<?, ?> menuHandler, int windowId) {
        super(menuHandler.getContainerType(), windowId, playerInv, 0);
        //super(menuHandler.getContainerType(), windowId, playerInv, tile.getMachineType().getGui().getSlots(tile.getMachineTier()).size());
        //addSlots(tile);
        //if (tile.getMachineType().getGui().enablePlayerSlots()) addPlayerSlots();
        this.tile = tile;
    }

    public TileEntityPipe getTile() {
        return tile;
    }

    protected void addSlots(TileEntityMachine tile) {
        /*tile.itemHandler.ifPresent(h -> {
            int inputIndex = 0, outputIndex = 0, cellIndex = 0;
            for (SlotData slot : tile.getMachineType().getGui().getSlots(tile.getMachineTier())) {
                switch (slot.type) {
                    case IT_IN:
                        addSlot(new SlotInput(h.getInputWrapper(), inputIndex++, slot.x, slot.y));
                        break;
                    case IT_OUT:
                        addSlot(new SlotOutput(h.getOutputWrapper(), outputIndex++, slot.x, slot.y));
                        break;
                    case CELL_IN:
                        addSlot(new SlotInput(h.getCellWrapper(), cellIndex++, slot.x, slot.y));
                        break;
                    case CELL_OUT:
                        addSlot(new SlotOutput(h.getCellWrapper(), cellIndex++, slot.x, slot.y));
                        break;
                }
            }
        });*/
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, tile.getBlockState().getBlock());
    }
}
