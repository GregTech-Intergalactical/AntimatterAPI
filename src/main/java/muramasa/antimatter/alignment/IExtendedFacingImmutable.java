package muramasa.antimatter.alignment;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

interface IExtendedFacingImmutable extends IFacingImmutable {
    int DIRECTIONS_COUNT=Direction.values().length;
    int ROTATIONS_COUNT=Rotation.values().length;
    int FLIPS_COUNT=Flip.values().length;
    int STATES_COUNT = ExtendedFacing.values().length;

    static int getExtendedFacingIndex(@Nonnull Direction direction, @Nonnull Rotation rotation, @Nonnull Flip flip){
        return (direction.getIndex()*ROTATIONS_COUNT+rotation.getIndex())*FLIPS_COUNT+flip.getIndex();
    }

    default Rotation getRotation(){
        return Rotation.NORMAL;
    }

    default Flip getFlip() {
        return Flip.NONE;
    }

    default int getExtendedFacingIndex(){
        return getExtendedFacingIndex(getDirection(),getRotation(),getFlip());
    }
}