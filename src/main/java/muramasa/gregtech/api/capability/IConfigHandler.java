package muramasa.gregtech.api.capability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public interface IConfigHandler {

    boolean onWrench(EnumFacing side);

    boolean onCrowbar(EnumFacing side);

    boolean onScrewdriver(EnumFacing side);

    TileEntity getTile();
}
