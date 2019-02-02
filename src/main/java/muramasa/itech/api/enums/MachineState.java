package muramasa.itech.api.enums;

import net.minecraft.util.IStringSerializable;

public enum MachineState implements IStringSerializable {

    //OverlayID: 0(Idle), 1(Active), 2(Error)

    IDLE(0, 0, true, false), //ready idle (no input)
    DISABLED(1, 0, false, false), //powered but stopped by command (redstone/interface)
    FOUND_RECIPE(2, 1, false, false), //operating normally
    INVALID_STRUCTURE(3, 2, false, false), //structure check failed
    INVALID_RECIPE(4, 2, true, false), //no recipe for input
    INVALID_TIER(5, 2, true, false), //recipe tier requirement not met
    OUTPUT_FULL(6, 2, false, true), //output full can not continue
    NO_POWER(7, 2, false, false), //no power
    POWER_LOSS(8, 2, false, false); //power loss while operating

    public static MachineState[] VALUES;

    static {
        VALUES = values().clone();
    }

    private int id, overlayId;
    private boolean allowRecipeCheck, allowRecipeTickOnContentUpdate;

    MachineState(int id, int overlayId, boolean allowRecipeCheck, boolean allowRecipeTickOnContentUpdate) {
        this.id = id;
        this.overlayId = overlayId;
        this.allowRecipeCheck = allowRecipeCheck;
        this.allowRecipeTickOnContentUpdate = allowRecipeTickOnContentUpdate;
    }

    public int getId() {
        return id;
    }

    public int getOverlayId() {
        return overlayId;
    }

    public boolean allowRecipeCheck() {
        return allowRecipeCheck;
    }

    public boolean allowRecipeTickOnContentUpdate() {
        return allowRecipeTickOnContentUpdate;
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}