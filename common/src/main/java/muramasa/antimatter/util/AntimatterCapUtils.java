package muramasa.antimatter.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.capability.ICoverHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class AntimatterCapUtils {

    @ExpectPlatform
    public static Optional<ICoverHandler<?>> getCoverHandler(BlockEntity blockEntity, Direction side){
        throw new AssertionError();
    }
}
