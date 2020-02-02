package muramasa.antimatter.alignment;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface IFacing {

    boolean isNewDirectionValid(@Nonnull Direction direction);

    void setDirection(@Nonnull Direction direction);

    Direction getDirection();

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
