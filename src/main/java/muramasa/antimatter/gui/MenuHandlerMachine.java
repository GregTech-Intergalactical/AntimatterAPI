package muramasa.antimatter.gui;

import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.network.packets.FluidStackPacket;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

public abstract class MenuHandlerMachine<T extends ContainerMachine> extends MenuHandler<T> {

    public MenuHandlerMachine(String domain, String id) {
        super(domain, id);
    }

    @Override
    public T onContainerCreate(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileEntity tile = Utils.getTileFromBuf(data);
        boolean isMachine = tile instanceof TileEntityMachine;
        if (isMachine) {
            TileEntityMachine machine = (TileEntityMachine) tile;
            machine.coverHandler.ifPresent(ch -> {
                //TODO This better
                CoverStack<?> stack = ch.get(ch.getOutputFacing());
                if (stack.getCover() instanceof CoverOutput) {
                    CoverOutput co = (CoverOutput) stack.getCover();
                    co.setEjects(stack, data.readBoolean(), data.readBoolean());
                }
            });
            FluidStackPacket.decode(data).executePacket();
            return getMenu(tile, inv, windowId);
        }
        return null;
    }
}
