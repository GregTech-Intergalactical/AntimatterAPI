package muramasa.antimatter.util.forge;

import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.forge.AntimatterCaps;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class AntimatterCapUtilsImpl {

    public static Optional<ICoverHandler<?>> getCoverHandler(BlockEntity blockEntity, Direction side){
        return blockEntity.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY, side).map(r -> r);
    }
}
