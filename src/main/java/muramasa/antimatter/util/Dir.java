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
            case UP:
                return Direction.UP;
            case DOWN:
                return Direction.DOWN;
            case LEFT:
                return side.getCounterClockWise();
            case RIGHT:
                return side.getClockWise();
            case FORWARD:
                return side;
            case BACK:
                return side.getOpposite();
            default:
                return side;
        }
    }

    public Direction getRotatedFacing(Direction side, Direction hSide) {
        switch (this) {
            case UP:
                return hSide.getOpposite();
            case DOWN:
                return hSide;
            case LEFT:
                return hSide.getCounterClockWise();
            case RIGHT:
                return hSide.getClockWise();
            case FORWARD:
                return side;
            case BACK:
                return side.getOpposite();
            default:
                return side;
        }
    }
    /*
    //DOWN -> EAST -> UP -> WEST for CCW
    public static Direction rotateZCCW(Direction dir) {
        switch (dir) {
            case WEST:
                return Direction.UP;
            case UP:
                return Direction.EAST;
            case EAST:
                return Direction.DOWN;
            case DOWN:
                return Direction.WEST;
        }
        return null;
    }
    //WEST -> UP -> EAST -> DOWN
    public static Direction rotateZ(Direction dir) {
        switch (dir) {
            case WEST:
                return Direction.DOWN;
            case DOWN:
                return Direction.EAST;
            case EAST:
                return Direction.UP;
            case UP:
                return Direction.WEST;
        }
        return null;
    }

    //SOUTH -> WEST -> NORTH -> EAST
    public static Direction rotateXCCW(Direction dir) {
        switch (dir) {
            case DOWN:
                return Direction.NORTH;
            case NORTH:
                return Direction.UP;
            case UP:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.DOWN;
        }
        return null;
    }
    public static Direction rotateX(Direction dir) {
        switch (dir) {
            case DOWN:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.UP;
            case UP:
                return Direction.NORTH;
            case NORTH:
                return Direction.DOWN;
        }
        return null;
    }*/
}
