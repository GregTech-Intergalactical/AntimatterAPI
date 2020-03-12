package muramasa.antimatter.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class int3 extends BlockPos.Mutable {
    protected Direction side = Direction.NORTH;

    public int3() {
        super();
    }

    public int3(int x, int y, int z) {
        super(x, y, z);
    }

    public int3(BlockPos pos, Direction side) {
        super(pos);
        this.side = side;
    }

    public int3 setPos(int3 pos) {
        this.setDir(pos.side);
        return (int3) setPos(pos.x, pos.y, pos.z); // not calls toImmutable
    }

    public void setDir(Direction side) {
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

    @Override
    public int3 up(int n) {
        return offset(n, Direction.UP);
    }

    @Override
    public int3 down(int n) {
        return offset(n, Direction.DOWN);
    }

    public int3 offset(int n, Direction direction) {
        int3 pos = (int3) offset(direction, n); // calls toImmutable
        pos.setDir(side);
        return pos;
    }

    public int3 offset(int2 n, Dir... directions) {
        if (directions.length < 2) return this;
        return offset(n.x, directions[0].getRotatedFacing(side)).offset(n.x, directions[1].getRotatedFacing(side));
    }

    public int3 offset(int3 n, Dir... directions) {
        if (directions.length < 3) return this;
        return offset(n.x, directions[0].getRotatedFacing(side)).offset(n.y, directions[1].getRotatedFacing(side)).offset(n.z, directions[2].getRotatedFacing(side));
    }

    public int3 offset(int3 n, Direction... facings) {
        if (facings.length < 3) return this;
        return offset(n.x, facings[0]).offset(n.y, facings[1]).offset(n.z, facings[2]);
    }
}
