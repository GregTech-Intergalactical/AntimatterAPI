package muramasa.antimatter.alignment;

import net.minecraft.util.Direction;

interface IFacingImmutable {

    default Direction getDirection(){
        return Direction.NORTH;
    }
}
