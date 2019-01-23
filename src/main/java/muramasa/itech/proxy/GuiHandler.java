package muramasa.itech.proxy;

import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.gui.GuiMultiMachine;
import muramasa.itech.api.gui.container.ContainerMultiMachine;
import muramasa.itech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.itech.common.tileentities.base.multi.TileEntityMultiMachine;
import muramasa.itech.common.utils.Ref;
import muramasa.itech.api.gui.GuiMachine;
import muramasa.itech.api.gui.container.ContainerMachine;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
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
        if (tile instanceof TileEntityMachine && ID == Ref.MACHINE_ID) {
            System.out.println("IS MACHINE");
            if (((TileEntityMachine) tile).hasFlag(MachineFlag.GUI)) {
                System.out.println("OPENING MACHINE");
                return new ContainerMachine((TileEntityMachine) tile, player.inventory);
            }
        } else if (tile instanceof TileEntityMultiMachine && ID == Ref.MULTI_MACHINE_ID) {
            return new ContainerMultiMachine((TileEntityMultiMachine) tile, player.inventory);
        } else if (tile instanceof TileEntityHatch && ID == Ref.HATCH_ID) {

        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile instanceof TileEntityMachine && ID == Ref.MACHINE_ID) {
            if (((TileEntityMachine) tile).hasFlag(MachineFlag.GUI)) {
                return new GuiMachine((TileEntityMachine) tile, new ContainerMachine((TileEntityMachine) tile, player.inventory));
            }
        } else if (tile instanceof TileEntityMultiMachine && ID == Ref.MULTI_MACHINE_ID) {
            return new GuiMultiMachine((TileEntityMultiMachine) tile, new ContainerMultiMachine((TileEntityMultiMachine) tile, player.inventory));
        } else if (tile instanceof TileEntityHatch && ID == Ref.HATCH_ID) {

        }
        return null;
    }
}
