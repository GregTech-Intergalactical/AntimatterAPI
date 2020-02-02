package muramasa.antimatter.alignment;

import muramasa.antimatter.alignment.enumerable.ExtendedFacing;
import muramasa.antimatter.alignment.enumerable.Flip;
import muramasa.antimatter.alignment.enumerable.Rotation;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public class Alignment implements IAlignment {

    private IAlignmentLimits alignmentLimits;
    private ExtendedFacing extendedFacing;

    public Alignment(@Nonnull IAlignmentLimits alignmentLimits) {
        this(alignmentLimits,ExtendedFacing.DEFAULT);
    }

    public Alignment(@Nonnull ExtendedFacing extendedFacing) {
        this(IAlignmentLimits.UNLIMITED,extendedFacing);
    }

    public Alignment(@Nonnull IAlignmentLimits alignmentLimits, @Nonnull ExtendedFacing extendedFacing) {
        this.alignmentLimits = alignmentLimits;
        this.extendedFacing = extendedFacing;
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
    public ExtendedFacing getExtendedFacing() {
        return extendedFacing;
    }

    @Override
    public void setExtendedFacing(@Nonnull ExtendedFacing extendedFacing) {
        this.extendedFacing=extendedFacing;
    }

    @Override
    public IAlignmentLimits getAlignmentLimits() {
        return alignmentLimits;
    }

    @Override
    public void setAlignmentLimits(@Nonnull IAlignmentLimits alignmentLimits) {
        this.alignmentLimits = alignmentLimits;
    }

    @Override
    public boolean isNewExtendedFacingValid(@Nonnull Direction direction, @Nonnull Rotation rotation, @Nonnull Flip flip) {
        return alignmentLimits.isNewExtendedFacingValid(direction, rotation, flip);
    }
}
