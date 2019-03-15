package muramasa.gregtech.api.capability;

import muramasa.gregtech.api.enums.ToolType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public interface IConfigHandler {

    boolean onInteract(EntityPlayer player, EnumFacing side, @Nullable ToolType type);

    TileEntity getTile();
}
