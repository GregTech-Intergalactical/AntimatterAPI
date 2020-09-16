package muramasa.antimatter.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * Created By Muramasa -  https://github.com/Muramasa-
 * Allows easily stepping in directions given a Direction
 */
public class int3 extends BlockPos.Mutable {

    public Direction side = Direction.NORTH; //Used for moving in a direction

    public int3() {
    }

    public int3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int3(int x, int y, int z, Direction side) {
        this(x, y, z);
        this.side = side;
    }

    public int3(Direction side) {
        this.side = side;
    }

    public int3(BlockPos pos, Direction side) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.side = side;
    }

    public int3 set(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        return this;
    }

    public void set(Direction side) {
        this.side = side;
    }

    public int3 right(int n) {
        return offset(n, side.rotateY());
    }

    public int3 left(int n) {
        return offset(n, side.rotateYCCW());
    }

    public int3 forward(int n) {
        return offset(n, side);
    }

    public int3 back(int n) {
        return offset(n, side.getOpposite());
    }

    @Nonnull
    public int3 up(int n) {
        return offset(n, Direction.UP);
    }

    @Nonnull
    public int3 down(int n) {
        return offset(n, Direction.DOWN);
    }

    @Nonnull
    @Override
    public BlockPos offset(Direction side) {
        return offset(1, side);
    }

    @Nonnull
    @Override
    public BlockPos offset(Direction side, int n) {
        return offset(n, side);
    }

    public int3 offset(int n, Direction side) {
        if (n == 0 || side == null) return this;
        setPos(x + side.getXOffset() * n, y + side.getYOffset() * n, z + side.getZOffset() * n);
        return this;
    }

    public int3 offset(int2 n, Dir... directions) {
        if (side != null && directions.length >= 2) {
            offset(n.x, directions[0].getRotatedFacing(side));
            offset(n.y, directions[1].getRotatedFacing(side));
        }
        return this;
    }

    public int3 offset(int3 n, Dir... directions) {
        if (side != null && directions.length >= 3) {
            offset(n.x, directions[0].getRotatedFacing(side));
            offset(n.y, directions[1].getRotatedFacing(side));
            offset(n.z, directions[2].getRotatedFacing(side));
        }
        return this;
    }

    public int3 offset(int3 n, Direction... facings) {
        if (facings.length >= 3) {
            offset(n.x, facings[0]);
            offset(n.y, facings[1]);
            offset(n.z, facings[2]);
        }
        return this;
    }

    @Nonnull
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
