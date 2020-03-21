package muramasa.antimatter.capability;

import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;

//TODO rename to IInteractHandler?
public interface IConfigHandler {

    boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type);

    TileEntity getTile();
}
