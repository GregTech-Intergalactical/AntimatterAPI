package muramasa.gregtech.api.cover.impl;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.api.gui.GuiData;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public class CoverScreen extends Cover {

    @Override
    public String getName() {
        return "screen";
    }

    @Override
    public boolean onInteract(EntityPlayer player, TileEntity tile, EnumFacing side, @Nullable ToolType type) {
        if (type == null && tile.hasCapability(GTCapabilities.COMPONENT, null)) {
            TileEntityMultiMachine controller = tile.getCapability(GTCapabilities.COMPONENT, null).getFirstController();
            if (controller == null) return false;
            if (!controller.getType().hasFlag(MachineFlag.GUI)) return false;
            GuiData gui = controller.getType().getGui();
            player.openGui(GregTech.INSTANCE, gui.getId(), player.getEntityWorld(), controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ());
        }
        return true;
    }
}
