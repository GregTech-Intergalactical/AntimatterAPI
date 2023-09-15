package muramasa.antimatter.capability;

import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractHandler<T extends BlockEntity> implements IInteractHandler<T> {

    private T tile;

    public InteractHandler(T tile) {
        this.tile = tile;
    }

    @Override
    public boolean onInteract(Player player, InteractionHand hand, Direction side, @Nullable AntimatterToolType type) {
        return false;
    }

    @NotNull
    @Override
    public T getTile() {
        if (tile == null) throw new NullPointerException("InteractHandler cannot have a null tile");
        return tile;
    }
}
