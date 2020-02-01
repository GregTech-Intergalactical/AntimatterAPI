package muramasa.antimatter.alignment;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface IFacing extends IFacingImmutable {
    default boolean isNewDirectionValid(@Nonnull Direction direction){
        return true;
    }

    void setDirection(@Nonnull Direction direction);

    default boolean checkedSetDirection(@Nonnull Direction direction){
        if (isNewDirectionValid(direction)){
            setDirection(direction);
            return true;
        }
        return false;
    }

    default boolean isValid(){
        return isNewDirectionValid(getDirection());
    }
}
