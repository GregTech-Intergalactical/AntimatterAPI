package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.util.LazyOptional;

public interface ITickablePipe extends ITickableTileEntity {
    LazyOptional<PipeCoverHandler<?>> getCoverHandler();

    @Override
    default void tick() {
        getCoverHandler().ifPresent(CoverHandler::onUpdate);
    }

}
