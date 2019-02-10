package muramasa.gregtech.proxy;

import muramasa.gregtech.api.gui.GuiBasicMachine;
import muramasa.gregtech.api.gui.GuiHatch;
import muramasa.gregtech.api.gui.GuiMultiMachine;
import muramasa.gregtech.api.gui.container.ContainerBasicMachine;
import muramasa.gregtech.api.gui.container.ContainerHatch;
import muramasa.gregtech.api.gui.container.ContainerMultiMachine;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
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
            Machine machine = ((TileEntityMachine) tile).getMachineType();
            if (!machine.hasFlag(MachineFlag.GUI)) return null;
            if (machine.getGuiId() == Ref.MACHINE_ID) {
                return new ContainerBasicMachine((TileEntityBasicMachine) tile, player.inventory);
            } else if (machine.getGuiId() == Ref.MULTI_MACHINE_ID) {
                return new ContainerMultiMachine((TileEntityMachine) tile, player.inventory);
            } else if (machine.getGuiId() == Ref.HATCH_ID) {
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
            Machine machine = ((TileEntityMachine) tile).getMachineType();
            if (!machine.hasFlag(MachineFlag.GUI)) return null;
            if (machine.getGuiId() == Ref.MACHINE_ID) {
                return new GuiBasicMachine((TileEntityBasicMachine) tile, new ContainerBasicMachine((TileEntityBasicMachine) tile, player.inventory));
            } else if (machine.getGuiId() == Ref.MULTI_MACHINE_ID) {
                return new GuiMultiMachine((TileEntityMachine) tile, new ContainerMultiMachine((TileEntityMachine) tile, player.inventory));
            } else if (machine.getGuiId() == Ref.HATCH_ID) {
                return new GuiHatch((TileEntityMachine) tile, new ContainerHatch((TileEntityMachine) tile, player.inventory));
            }
        }
        return null;
    }
}
