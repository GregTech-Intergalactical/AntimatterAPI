package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;

import java.util.Optional;

public interface ITickablePipe {
    Optional<PipeCoverHandler<?>> getCoverHandler();

    default void tick() {
        getCoverHandler().ifPresent(CoverHandler::onUpdate);
    }

}
