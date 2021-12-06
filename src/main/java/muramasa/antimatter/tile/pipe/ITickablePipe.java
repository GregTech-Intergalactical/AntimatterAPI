package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import net.minecraftforge.common.util.LazyOptional;

public interface ITickablePipe {
    LazyOptional<PipeCoverHandler<?>> getCoverHandler();

    default void tick() {
        getCoverHandler().ifPresent(CoverHandler::onUpdate);
    }

}
