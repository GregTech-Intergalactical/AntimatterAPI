package muramasa.antimatter.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * Created By Muramasa -  https://github.com/Muramasa-
 * Allows easily stepping in directions given a Direction
 */
public class int3 {

    //TODO change this class to wrap a MutableBlockPos

    public int x, y, z;
    public Direction side = Direction.NORTH; //Used for moving in a direction

    public int3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public int3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int3(int3 pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    public int3(int3 pos, Direction side) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.side = side;
    }

    public int3(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public int3(BlockPos pos, Direction side) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.side = side;
    }

    public int3 set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public int3 set(int3 pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.side = pos.side;
        return this;
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

    public int3 add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public int3 add(int3 pos) {
        this.x += pos.x;
        this.y += pos.y;
        this.z += pos.z;
        return this;
    }

    public int3 sub(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public int3 sub(int3 pos) {
        this.x -= pos.x;
        this.y -= pos.y;
        this.z -= pos.z;
        return this;
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

    public int3 up(int n) {
        return offset(n, Direction.UP);
    }

    public int3 down(int n) {
        return offset(n, Direction.DOWN);
    }

    public int3 offset(int n, Direction side) {
        if (n == 0 || side == null) return this;
        return set(x + side.getXOffset() * n, y + side.getYOffset() * n, z + side.getZOffset() * n);
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

    public BlockPos asBP() {
        return new BlockPos(x, y, z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
