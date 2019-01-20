package muramasa.itech.api.capability;

import muramasa.itech.common.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public interface IComponent {

    String getId();

    BlockPos getPos();

    ArrayList<BlockPos> getLinkedControllers();

    void linkController(TileEntityMultiMachine tile);

    void unlinkController(TileEntityMultiMachine tile);
}
