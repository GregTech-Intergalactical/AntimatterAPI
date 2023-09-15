package muramasa.antimatter.capability;

import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface IInteractHandler<T extends BlockEntity> {

    boolean onInteract(Player player, InteractionHand hand, Direction side, @Nullable AntimatterToolType type);

    T getTile();
}
