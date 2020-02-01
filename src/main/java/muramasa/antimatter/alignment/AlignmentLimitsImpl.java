package muramasa.antimatter.alignment;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static muramasa.antimatter.alignment.IExtendedFacing.STATES_COUNT;
import static muramasa.antimatter.alignment.IExtendedFacing.getExtendedFacingIndex;

public class AlignmentLimitsImpl implements IAlignmentLimits {

    private final boolean[] validStates=new boolean[STATES_COUNT];

    private AlignmentLimitsImpl(ExtendedFacing... disabled) {
        Arrays.fill(validStates,true);
        if(disabled!=null){
            for (ExtendedFacing extendedFacing : disabled) {
                validStates[extendedFacing.getExtendedFacingIndex()]=false;
            }
        }
    }

    @Override
    public boolean isNewExtendedFacingValid(@Nonnull Direction direction, @Nonnull Rotation rotation, @Nonnull Flip flip) {
        return validStates[getExtendedFacingIndex(direction,rotation,flip)];
    }
}
