package muramasa.antimatter.alignment;

import javax.annotation.Nonnull;

public class AlignmentContainer implements IAlignment {

    private final IAlignmentLimits alignmentLimits;
    private final ExtendedFacing extendedFacing;

    public AlignmentContainer(@Nonnull IAlignmentLimits alignmentLimits, @Nonnull ExtendedFacing extendedFacing) {
        this.alignmentLimits = alignmentLimits;
        this.extendedFacing = extendedFacing;
    }

    @Override
    public IAlignmentLimits getAlignmentsLimits() {
        return alignmentLimits;
    }

    @Override
    public ExtendedFacing getExtendedFacing() {
        return extendedFacing;
    }
}
