package muramasa.antimatter.alignment;

public interface IAlignment extends IAlignmentLimitsProvider,IExtendedFacingProvider {

    default boolean isNewExtendedFacingValid(ExtendedFacing extendedFacing){
        return getAlignmentsLimits().isNewExtendedFacingValid(extendedFacing);
    }

    default boolean isExtendedFacingValid(){
        return isNewExtendedFacingValid(getExtendedFacing());
    }
}
