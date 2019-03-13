package muramasa.gregtech.api.capability;

import muramasa.gregtech.api.enums.ToolType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public interface IConfigHandler {

    boolean onInteract(EnumFacing side, ToolType type);

    TileEntity getTile();
}
