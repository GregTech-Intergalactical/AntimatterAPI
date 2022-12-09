package muramasa.antimatter.util.fabric;

import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.fabric.AntimatterLookups;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class AntimatterCapUtilsImpl {

    public static Optional<ICoverHandler<?>> getCoverHandler(BlockEntity blockEntity, Direction side){
        return Optional.ofNullable((ICoverHandler<?>) AntimatterLookups.COVER_HANDLER_SIDED.find(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, side));
    }

    public static Optional<IComponentHandler> getComponentHandler(BlockEntity blockEntity, Direction side){
        return Optional.empty();
    }

    public static Optional<MachineRecipeHandler<?>> getRecipeHandler(BlockEntity blockEntity, Direction side){
        return Optional.empty();
    }
}
