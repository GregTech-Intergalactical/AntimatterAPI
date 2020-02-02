package muramasa.antimatter.alignment;

import muramasa.antimatter.alignment.enumerable.ExtendedFacing;
import muramasa.antimatter.alignment.enumerable.Flip;
import muramasa.antimatter.alignment.enumerable.Rotation;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static muramasa.antimatter.alignment.IAlignment.STATES_COUNT;
import static muramasa.antimatter.alignment.IAlignment.getAlignmentIndex;

public class AlignmentLimits implements IAlignmentLimits {

    protected final boolean[] validStates=new boolean[STATES_COUNT];

    private AlignmentLimits(ExtendedFacing... disabled) {
        Arrays.fill(validStates,true);
        if(disabled!=null){
            for (ExtendedFacing extendedFacing : disabled) {
                validStates[extendedFacing.getExtendedFacingIndex()]=false;
            }
        }
    }

    @Override
    public boolean isNewExtendedFacingValid(@Nonnull Direction direction, @Nonnull Rotation rotation, @Nonnull Flip flip) {
        return validStates[getAlignmentIndex(direction,rotation,flip)];
    }
}
