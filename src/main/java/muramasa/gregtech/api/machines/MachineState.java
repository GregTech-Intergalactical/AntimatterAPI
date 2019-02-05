package muramasa.gregtech.api.machines;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum MachineState implements IStringSerializable {

    //OverlayID: 0(Idle), 1(Active), 2(Error)

    IDLE(0, 0, "Idle", true, false), //ready idle (no input)
    DISABLED(1, 0, "Disabled", false, false), //powered but stopped by command (redstone/interface)
    FOUND_RECIPE(2, 1, "Processing", false, false), //operating normally
    INVALID_STRUCTURE(3, 2, "Structure Invalid", false, false), //structure check failed
    INVALID_RECIPE(4, 2, "Recipe Invalid", true, false), //no recipe for input
    INVALID_TIER(5, 2, "Recipe Tier Invalid", true, false), //recipe tier requirement not met
    OUTPUT_FULL(6, 2, "Output Full", false, true), //output full can not continue
    NO_POWER(7, 2, "No Power", false, false), //no power
    POWER_LOSS(8, 2, "Power Loss", false, false); //power loss while operating

    public static MachineState[] VALUES;

    static {
        VALUES = values().clone();
    }

    private int id, overlayId;
    String displayName;
    private boolean allowRecipeCheck, allowRecipeTickOnContentUpdate;

    MachineState(int id, int overlayId, String displayName, boolean allowRecipeCheck, boolean allowRecipeTickOnContentUpdate) {
        this.id = id;
        this.overlayId = overlayId;
        this.displayName = displayName;
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

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}