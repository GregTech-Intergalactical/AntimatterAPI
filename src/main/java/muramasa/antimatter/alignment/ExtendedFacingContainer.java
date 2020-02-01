package muramasa.antimatter.alignment;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

import static muramasa.antimatter.alignment.ExtendedFacing.*;

public class ExtendedFacingContainer implements IExtendedFacing{
    private ExtendedFacing extendedFacing;

    public ExtendedFacingContainer(){
        extendedFacing= ExtendedFacing.defaultValue();
    }

    public ExtendedFacingContainer(@Nonnull Direction direction, @Nonnull Flip flip, @Nonnull Rotation rotation) {
        extendedFacing=of(direction, rotation, flip);
    }

    @Override
    public void setDirection(@Nonnull Direction direction) {
        extendedFacing=extendedFacing.with(direction);
    }

    @Override
    public void setRotation(@Nonnull Rotation rotation) {
        extendedFacing=extendedFacing.with(rotation);
    }

    @Override
    public void setFlip(@Nonnull Flip flip) {
        extendedFacing=extendedFacing.with(flip);
    }

    @Override
    public void setExtendedFacing(@Nonnull ExtendedFacing alignment) {
        extendedFacing=ExtendedFacing.of(alignment);
    }

    @Override
    public Direction getDirection() {
        return extendedFacing.getDirection();
    }

    @Override
    public Rotation getRotation() {
        return extendedFacing.getRotation();
    }

    @Override
    public Flip getFlip() {
        return extendedFacing.getFlip();
    }
}
