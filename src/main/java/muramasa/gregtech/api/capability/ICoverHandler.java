package muramasa.gregtech.api.capability;

import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.enums.ToolType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public interface ICoverHandler {

    void tick();

    boolean set(EnumFacing side, Cover cover);

    Cover get(EnumFacing side);

    boolean onInteract(EnumFacing side, ToolType type);

    Cover[] getAll();

    boolean hasCover(EnumFacing side, Cover cover);

    boolean isValid(EnumFacing side, Cover cover);

    EnumFacing getTileFacing();

    TileEntity getTile();
}
