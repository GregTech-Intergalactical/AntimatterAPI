package muramasa.antimatter.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public interface ICoverHandlerProvider<T extends BlockEntity> {
    Optional<ICoverHandler<T>> getCoverHandler();
}
