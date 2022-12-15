package muramasa.antimatter.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class AntimatterCapUtils {

    @ExpectPlatform
    public static Optional<ICoverHandler<?>> getCoverHandler(BlockEntity blockEntity, Direction side){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Optional<IComponentHandler> getComponentHandler(BlockEntity blockEntity, Direction side){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Optional<MachineRecipeHandler<?>> getRecipeHandler(BlockEntity blockEntity, Direction side){
        throw new AssertionError();
    }
}
