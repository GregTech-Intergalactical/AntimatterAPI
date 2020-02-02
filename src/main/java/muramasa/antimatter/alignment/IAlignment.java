package muramasa.antimatter.alignment;

import muramasa.antimatter.alignment.enumerable.ExtendedFacing;
import muramasa.antimatter.alignment.enumerable.Flip;
import muramasa.antimatter.alignment.enumerable.Rotation;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface IAlignment extends IFacing, IAlignmentLimits {
    int DIRECTIONS_COUNT=Direction.values().length;
    int ROTATIONS_COUNT= Rotation.values().length;
    int FLIPS_COUNT= Flip.values().length;
    int STATES_COUNT = ExtendedFacing.values().length;

    static int getAlignmentIndex(@Nonnull Direction direction, @Nonnull Rotation rotation, @Nonnull Flip flip){
        return (direction.getIndex()*ROTATIONS_COUNT+rotation.getIndex())*FLIPS_COUNT+flip.getIndex();
    }

    Rotation getRotation();

    Flip getFlip();

    void setRotation(@Nonnull Rotation rotation);

    void setFlip(@Nonnull Flip flip);

    void setExtendedFacing(@Nonnull ExtendedFacing alignment);

    void setAlignmentLimits(@Nonnull IAlignmentLimits limits);

    default boolean checkedSetRotation(@Nonnull Rotation rotation){
        if (isNewRotationValid(rotation)){
            setRotation(rotation);
            return true;
        }
        return false;
    }

    default boolean checkedSetFlip(@Nonnull Flip flip){
        if (isNewFlipValid(flip)){
            setFlip(flip);
            return true;
        }
        return false;
    }

    default boolean checkedSetExtendedFacing(@Nonnull ExtendedFacing alignment){
        if (isNewExtendedFacingValid(alignment)){
            setExtendedFacing(alignment);
            return true;
        }
        return false;
    }

    @Override
    default boolean isNewDirectionValid(@Nonnull Direction direction) {
        return isNewExtendedFacingValid(direction,getRotation(),getFlip());
    }

    default boolean isNewRotationValid(@Nonnull Rotation rotation){
        return isNewExtendedFacingValid(getDirection(),rotation,getFlip());
    }

    default boolean isNewFlipValid(@Nonnull Flip flip){
        return isNewExtendedFacingValid(getDirection(),getRotation(),flip);
    }

    @Override
    default boolean isValid() {
        return isNewExtendedFacingValid(getDirection(),getRotation(),getFlip());
    }

    default int getAlignmentIndex(){
        return getAlignmentIndex(getDirection(),getRotation(),getFlip());
    }
}