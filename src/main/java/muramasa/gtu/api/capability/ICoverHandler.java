package muramasa.gtu.api.capability;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.machines.MachineEvent;
import muramasa.gtu.api.tools.GregTechToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public interface ICoverHandler {

    void update();

    boolean set(Direction side, Cover cover);

    Cover get(Direction side);

    boolean onInteract(PlayerEntity player, Hand hand, Direction side, GregTechToolType type);

    void onMachineEvent(MachineEvent event);

    Cover[] getAll();

    boolean hasCover(Direction side, Cover cover);

    boolean isValid(Direction side, Cover existing, Cover replacement);

    Direction getTileFacing();

    TileEntity getTile();
}
