package muramasa.antimatter.alignment;

import muramasa.antimatter.alignment.enumerable.ExtendedFacing;
import muramasa.antimatter.alignment.enumerable.Flip;
import muramasa.antimatter.alignment.enumerable.Rotation;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface IAlignmentLimits {

    IAlignmentLimits UNLIMITED= (direction, rotation, flip) -> true;

    boolean isNewExtendedFacingValid(@Nonnull Direction direction, @Nonnull Rotation rotation, @Nonnull Flip flip);

    default boolean isNewExtendedFacingValid(@Nonnull ExtendedFacing alignment){
        return isNewExtendedFacingValid(
                alignment.getDirection(),
                alignment.getRotation(),
                alignment.getFlip());
    }
}
