package muramasa.gtu.api.cover.impl;

import muramasa.gtu.GregTech;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.gui.GuiData;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.interfaces.IComponent;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

public class CoverMonitor extends Cover {

    @Override
    public String getName() {
        return "monitor";
    }

    @Override
    public boolean onInteract(TileEntity tile, EntityPlayer player, EnumHand hand, EnumFacing side, @Nullable ToolType type) {
        if (type == null && tile instanceof IComponent) {
            TileEntityMultiMachine controller = ((IComponent) tile).getComponentHandler().getFirstController();
            if (controller == null) return false;
            if (!controller.getType().hasFlag(MachineFlag.GUI)) return false;
            GuiData gui = controller.getType().getGui();
            player.openGui(GregTech.INSTANCE, gui.getId(), player.getEntityWorld(), controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ());
        }
        return true;
    }
}
