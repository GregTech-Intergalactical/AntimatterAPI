package muramasa.gregtech.api.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Created By Muramasa -  https://github.com/Muramasa-
 * Allows easily stepping in directions given a EnumFacing
 */
public class int3 {

    public int x, y, z;
    public EnumFacing facing = EnumFacing.NORTH; //Used for moving in a direction

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

    public int3(int3 pos, EnumFacing facing) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.facing = facing;
    }

    public int3(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public int3(BlockPos pos, EnumFacing facing) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.facing = facing;
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
        this.facing = pos.facing;
        return this;
    }

    public int3 set(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        return this;
    }

    public void set(EnumFacing facing) {
        this.facing = facing;
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
        return offset(n, facing.rotateY());
    }

    public int3 left(int n) {
        return offset(n, facing.rotateYCCW());
    }

    public int3 forward(int n) {
        return offset(n, facing);
    }

    public int3 back(int n) {
        return offset(n, facing.getOpposite());
    }

    public int3 up(int n) {
        return offset(n, EnumFacing.UP);
    }

    public int3 offset(int n, EnumFacing facing) {
        if (n == 0 || facing == null) return this;
        return set(x + facing.getFrontOffsetX() * n, y + facing.getFrontOffsetY() * n, z + facing.getFrontOffsetZ() * n);
    }

    public int3 offset(int2 n, Dir... directions) {
        if (facing != null && directions.length >= 2) {
            offset(n.x, directions[0].getRotatedFacing(facing));
            offset(n.y, directions[1].getRotatedFacing(facing));
        }
        return this;
    }

    public int3 offset(int3 n, Dir... directions) {
        if (facing != null && directions.length >= 3) {
            offset(n.x, directions[0].getRotatedFacing(facing));
            offset(n.y, directions[1].getRotatedFacing(facing));
            offset(n.z, directions[2].getRotatedFacing(facing));
        }
        return this;
    }

    public int3 offset(int3 n, EnumFacing... facings) {
        if (facings.length >= 3) {
            offset(n.x, facings[0]);
            offset(n.y, facings[1]);
            offset(n.z, facings[2]);
        }
        return this;
    }

    public BlockPos asBlockPos() {
        return new BlockPos(x, y, z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
