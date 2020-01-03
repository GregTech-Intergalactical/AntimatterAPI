package muramasa.antimatter.util;

import net.minecraft.util.Direction;

public enum Dir {

    UP(),
    DOWN(),
    LEFT(),
    RIGHT(),
    FORWARD(),
    BACK();

    public Direction getRotatedFacing(Direction side) {
        switch (this) {
            case UP: return Direction.UP;
            case DOWN: return Direction.DOWN;
            case LEFT: return side.rotateYCCW();
            case RIGHT: return side.rotateY();
            case FORWARD: return side;
            case BACK: return side.getOpposite();
            default: return side;
        }
    }
}
