package muramasa.antimatter.capability;

import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface IInteractHandler {
    //ParsedSide represents the side as parsed by Utils.getInteractSide
    boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nonnull Direction parsedSide, @Nullable AntimatterToolType type);

    @Nonnull
    TileEntity getTile();
}
