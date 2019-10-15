package muramasa.gtu.api.container;

import muramasa.gtu.api.data.Guis;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;

public class ContainerMachine extends Container {

    protected TileEntityMachine tile;
    protected PlayerInventory playerInv;

    public ContainerMachine(int windowId, TileEntityMachine tile, PlayerInventory playerInv) {
        super(/*tile.getMachineType().getGui().getContainerType()*/Guis.CONTAINER_MACHINE, windowId);
        this.tile = tile;
        this.playerInv = playerInv;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, tile.getMachineType().getBlock(tile.getTier()));
    }

    public static ContainerMachine fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
        return new ContainerMachine(windowId, (TileEntityMachine) Utils.getTileFromBuf(data), inv);
    }
}
