package muramasa.antimatter.alignment;

import muramasa.antimatter.alignment.enumerable.ExtendedFacing;
import muramasa.antimatter.alignment.enumerable.Flip;
import muramasa.antimatter.alignment.enumerable.Rotation;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import static muramasa.antimatter.alignment.IAlignment.STATES_COUNT;
import static muramasa.antimatter.alignment.IAlignment.getAlignmentIndex;

public class AlignmentLimits implements IAlignmentLimits {

    protected final boolean[] validStates=new boolean[STATES_COUNT];

    public AlignmentLimits() {
        allowAll();
    }

    AlignmentLimits allowAll(){
        Arrays.fill(validStates,true);
        return this;
    }

    AlignmentLimits denyAll(){
        Arrays.fill(validStates,false);
        return this;
    }

    AlignmentLimits randomAll(@Nonnull Random random){
        for (int i = 0; i < validStates.length; i++) {
            validStates[i]=random.nextBoolean();
        }
        return this;
    }

    AlignmentLimits deny(ExtendedFacing... deny){
        if(deny!=null){
            for (ExtendedFacing extendedFacing : deny) {
                validStates[extendedFacing.getExtendedFacingIndex()]=false;
            }
        }
        return this;
    }

    AlignmentLimits allow(ExtendedFacing... allow){
        if(allow!=null){
            for (ExtendedFacing extendedFacing : allow) {
                validStates[extendedFacing.getExtendedFacingIndex()]=false;
            }
        }
        return this;
    }

    AlignmentLimits deny(Direction... deny){
        if(deny!=null){
            for (ExtendedFacing value : ExtendedFacing.values()) {
                for (Direction direction : deny) {
                    if (value.getDirection() == direction) {
                        validStates[value.getExtendedFacingIndex()] = false;
                        break;
                    }
                }
            }
        }
        return this;
    }

    AlignmentLimits allow(Direction... allow){
        if(allow!=null){
            for (ExtendedFacing value : ExtendedFacing.values()) {
                for (Direction direction : allow) {
                    if (value.getDirection() == direction) {
                        validStates[value.getExtendedFacingIndex()] = true;
                        break;
                    }
                }
            }
        }
        return this;
    }

    AlignmentLimits deny(Rotation... deny){
        if(deny!=null){
            for (ExtendedFacing value : ExtendedFacing.values()) {
                for (Rotation rotation : deny) {
                    if (value.getRotation() == rotation) {
                        validStates[value.getExtendedFacingIndex()] = false;
                        break;
                    }
                }
            }
        }
        return this;
    }

    AlignmentLimits allow(Rotation... allow){
        if(allow!=null){
            for (ExtendedFacing value : ExtendedFacing.values()) {
                for (Rotation rotation : allow) {
                    if (value.getRotation() == rotation) {
                        validStates[value.getExtendedFacingIndex()] = true;
                        break;
                    }
                }
            }
        }
        return this;
    }

    AlignmentLimits deny(Flip... deny){
        if(deny!=null){
            for (ExtendedFacing value : ExtendedFacing.values()) {
                for (Flip flip : deny) {
                    if (value.getFlip() == flip) {
                        validStates[value.getExtendedFacingIndex()] = false;
                        break;
                    }
                }
            }
        }
        return this;
    }

    AlignmentLimits allow(Flip... allow){
        if(allow!=null){
            for (ExtendedFacing value : ExtendedFacing.values()) {
                for (Flip flip : allow) {
                    if (value.getFlip() == flip) {
                        validStates[value.getExtendedFacingIndex()] = true;
                        break;
                    }
                }
            }
        }
        return this;
    }

    public interface Predicate extends Function<ExtendedFacing,Optional<Boolean>> {}

    AlignmentLimits predicateApply(@Nonnull Predicate predicate){
        for (ExtendedFacing value : ExtendedFacing.values()) {
            predicate.apply(value).ifPresent(bool->validStates[value.getExtendedFacingIndex()]=bool);
        }
        return this;
    }

    AlignmentLimits ensureDuplicatesAreDenied(){
        for (ExtendedFacing value : ExtendedFacing.values()) {
            if(!validStates[value.getExtendedFacingIndex()]){
                validStates[value.getDuplicate().getExtendedFacingIndex()]=false;
            }
        }
        return this;
    }

    AlignmentLimits ensureDuplicatesAreAllowed(){
        for (ExtendedFacing value : ExtendedFacing.values()) {
            if(validStates[value.getExtendedFacingIndex()]){
                validStates[value.getDuplicate().getExtendedFacingIndex()]=true;
            }
        }
        return this;
    }

    /**
     * Prefers rotation over flip, so both flip will get translated to opposite rotation and no flip
     * @param flip the preferred flip to be used Horizontal or vertical
     * @return this
     */
    AlignmentLimits ensureNoDuplicates(@Nonnull Flip flip){
        if(flip==Flip.BOTH||flip==Flip.NONE){
            throw new IllegalArgumentException("Preffered Flip must be Horizontal or Vertical");
        }
        flip=flip.getOpposite();
        for (ExtendedFacing value : ExtendedFacing.values()) {
            if(validStates[value.getExtendedFacingIndex()]){
                if(value.getFlip()==Flip.BOTH || value.getFlip()==flip){
                    validStates[value.getExtendedFacingIndex()]=false;
                    validStates[value.getDuplicate().getExtendedFacingIndex()]=true;
                }
            }
        }
        return this;
    }

    @Override
    public boolean isNewExtendedFacingValid(@Nonnull Direction direction, @Nonnull Rotation rotation, @Nonnull Flip flip) {
        return validStates[getAlignmentIndex(direction,rotation,flip)];
    }
}
