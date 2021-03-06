package muramasa.antimatter.capability;

import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;

public interface IInteractHandler<T extends TileEntity> {

    boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type);

    T getTile();
}
