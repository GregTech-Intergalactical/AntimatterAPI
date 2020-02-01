package muramasa.antimatter.alignment;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface IAlignmentLimits {

    default boolean isNewExtendedFacingValid(@Nonnull Direction direction, @Nonnull Rotation rotation, @Nonnull Flip flip){
        return true;
    }

    default boolean isNewExtendedFacingValid(@Nonnull ExtendedFacing alignment){
        return isNewExtendedFacingValid(
                alignment.getDirection(),
                alignment.getRotation(),
                alignment.getFlip());
    }
}
