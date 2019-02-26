package muramasa.gregtech.proxy;

import muramasa.gregtech.api.gui.client.GuiBasicMachine;
import muramasa.gregtech.api.gui.client.GuiHatch;
import muramasa.gregtech.api.gui.client.GuiMultiMachine;
import muramasa.gregtech.api.gui.server.ContainerBasicMachine;
import muramasa.gregtech.api.gui.server.ContainerHatch;
import muramasa.gregtech.api.gui.server.ContainerMultiMachine;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.gregtech.common.utils.Ref;
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
                return new ContainerHatch((TileEntityMachine) tile, player.inventory);
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
                return new GuiHatch((TileEntityMachine) tile, new ContainerHatch((TileEntityMachine) tile, player.inventory));
            }
        }
        return null;
    }
}
