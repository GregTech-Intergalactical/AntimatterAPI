package muramasa.antimatter.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;

import javax.annotation.Nonnull;

/**
 * Created By Muramasa -  https://github.com/Muramasa-
 * Allows easily stepping in directions given a Direction
 */
public class int3 extends BlockPos.MutableBlockPos {

    public Direction side = Direction.NORTH; //Used for moving in a direction

    public int3() {
    }

    public int3(int x, int y, int z) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    public int3(int x, int y, int z, Direction side) {
        this(x, y, z);
        this.side = side;
    }

    public int3(Direction side) {
        this.side = side;
    }

    public int3(BlockPos pos, Direction side) {
        setX(pos.getX());
        setY(pos.getY());
        setZ(pos.getZ());
        this.side = side;
    }

    public int3 set(BlockPos pos) {
        setX(pos.getX());
        setY(pos.getY());
        setZ(pos.getZ());
        return this;
    }

    public void set(Direction side) {
        this.side = side;
    }

    public int3 right(int n) {
        if (side.getAxis() != Axis.Y) return offset(n, side.getClockWise());
        return offset(n, Direction.NORTH.getClockWise());
    }

    public int3 left(int n) {
        if (side.getAxis() != Axis.Y) return offset(n, side.getCounterClockWise());
        return offset(n, Direction.NORTH.getCounterClockWise());
    }

    public int3 forward(int n) {
        return offset(n, side);
    }

    public int3 back(int n) {
        return offset(n, side.getOpposite());
    }

    @Nonnull
    public int3 above(int n) {
        if (side.getAxis() != Axis.Y) return offset(n, Direction.UP);
        return offset(n, Direction.NORTH.getOpposite());
    }

    @Nonnull
    public int3 below(int n) {
        if (side.getAxis() != Axis.Y) return offset(n, Direction.DOWN);
        return offset(n, Direction.NORTH);
    }

    @Nonnull
    @Override
    public BlockPos relative(Direction side) {
        return offset(1, side);
    }

    @Nonnull
    @Override
    public BlockPos relative(Direction side, int n) {
        return offset(n, side);
    }

    public int3 offset(int n, Direction side) {
        if (n == 0 || side == null) return this;
        set(getX() + side.getStepX() * n, getY() + side.getStepY() * n, getZ() + side.getStepZ() * n);
        return this;
    }

    public int3 offset(int2 n, Dir... directions) {
        if (side != null && directions.length >= 2) {
            if (side.getAxis() != Axis.Y) {
                offset(n.x, directions[0].getRotatedFacing(side));
                offset(n.y, directions[1].getRotatedFacing(side));
            } else {
                offset(n.x, directions[0].getRotatedFacing(side, Direction.NORTH));
                offset(n.y, directions[1].getRotatedFacing(side, Direction.NORTH));
            }
        }
        return this;
    }

    public int3 offset(int3 n, Dir... directions) {
        if (side != null && directions.length >= 3) {
            if (side.getAxis() != Axis.Y) {
                offset(n.getX(), directions[0].getRotatedFacing(side));
                offset(n.getY(), directions[1].getRotatedFacing(side));
                offset(n.getZ(), directions[2].getRotatedFacing(side));
            } else {
                offset(n.getX(), directions[0].getRotatedFacing(side, Direction.NORTH));
                offset(n.getY(), directions[1].getRotatedFacing(side, Direction.NORTH));
                offset(n.getZ(), directions[2].getRotatedFacing(side, Direction.NORTH));
            }
        }
        return this;
    }

    public int3 offset(int3 n, Direction... facings) {
        if (facings.length >= 3) {
            offset(n.getX(), facings[0]);
            offset(n.getY(), facings[1]);
            offset(n.getZ(), facings[2]);
        }
        return this;
    }

    @Nonnull
    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ", " + getZ() + ")";
    }
}
