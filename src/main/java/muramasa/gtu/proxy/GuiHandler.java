package muramasa.gtu.proxy;

import muramasa.gtu.Ref;
import muramasa.gtu.api.gui.client.GuiBasicMachine;
import muramasa.gtu.api.gui.client.GuiHatch;
import muramasa.gtu.api.gui.client.GuiMultiMachine;
import muramasa.gtu.api.gui.server.ContainerBasicMachine;
import muramasa.gtu.api.gui.server.ContainerHatch;
import muramasa.gtu.api.gui.server.ContainerMultiMachine;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.tileentities.TileEntityBasicMachine;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile instanceof TileEntityMachine) {
            Machine machine = ((TileEntityMachine) tile).getType();
            if (!machine.hasFlag(MachineFlag.GUI)) return null;
            if (machine.getGui().getId() == Ref.MACHINE_ID) {
                return new ContainerBasicMachine((TileEntityBasicMachine) tile, player.inventory);
            } else if (machine.getGui().getId() == Ref.MULTI_MACHINE_ID) {
                return new ContainerMultiMachine((TileEntityMultiMachine) tile, player.inventory);
            } else if (machine.getGui().getId() == Ref.HATCH_ID) {
                return new ContainerHatch((TileEntityHatch) tile, player.inventory);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile instanceof TileEntityMachine) {
            Machine machine = ((TileEntityMachine) tile).getType();
            if (!machine.hasFlag(MachineFlag.GUI)) return null;
            if (machine.getGui().getId() == Ref.MACHINE_ID) {
                return new GuiBasicMachine((TileEntityBasicMachine) tile, new ContainerBasicMachine((TileEntityBasicMachine) tile, player.inventory));
            } else if (machine.getGui().getId() == Ref.MULTI_MACHINE_ID) {
                return new GuiMultiMachine((TileEntityMultiMachine) tile, new ContainerMultiMachine((TileEntityMultiMachine) tile, player.inventory));
            } else if (machine.getGui().getId() == Ref.HATCH_ID) {
                return new GuiHatch((TileEntityHatch) tile, new ContainerHatch((TileEntityHatch) tile, player.inventory));
            }
        }
        return null;
    }
}
