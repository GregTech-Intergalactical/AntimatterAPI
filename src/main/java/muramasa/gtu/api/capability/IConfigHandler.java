package muramasa.gtu.api.capability;

import muramasa.gtu.api.tools.GregTechToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;

public interface IConfigHandler {

    boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable GregTechToolType type);

    TileEntity getTile();
}
