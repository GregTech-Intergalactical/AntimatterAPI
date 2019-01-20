package muramasa.itech.api.util;

import net.minecraft.util.EnumFacing;

public enum Dir {

    UP(),
    DOWN(),
    LEFT(),
    RIGHT(),
    FORWARD(),
    BACK();

    public EnumFacing getRotatedFacing(EnumFacing facing) {
        switch (this) {
            case UP: return EnumFacing.UP;
            case DOWN: return EnumFacing.DOWN;
            case LEFT: return facing.rotateYCCW();
            case RIGHT: return facing.rotateY();
            case FORWARD: return facing;
            case BACK: return facing.getOpposite();
            default: return facing;
        }
    }
}
