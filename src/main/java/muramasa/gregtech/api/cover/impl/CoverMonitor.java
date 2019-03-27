package muramasa.gregtech.api.cover.impl;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.tools.ToolType;
import muramasa.gregtech.api.gui.GuiData;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.interfaces.IComponent;
import muramasa.gregtech.api.tileentities.multi.TileEntityMultiMachine;
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
