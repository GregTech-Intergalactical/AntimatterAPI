package muramasa.antimatter.alignment;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface IExtendedFacing extends IExtendedFacingImmutable, IFacing, IAlignmentLimits {

    void setRotation(@Nonnull Rotation rotation);

    void setFlip(@Nonnull Flip flip);

    void setExtendedFacing(@Nonnull ExtendedFacing alignment);

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
}