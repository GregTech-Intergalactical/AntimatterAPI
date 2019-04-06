package muramasa.gtu.api.machines;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum MachineState implements IStringSerializable {

    //OverlayID: 0(Idle), 1(Active), 2(Error)

    IDLE(0, "Idle", true, false), //ready idle (no input)
    DISABLED(0, "Disabled", false, false), //powered but stopped by command (redstone/interface)
    //TODO rename to active or processing?
    ACTIVE(1, "Active", false, false), //operating normally
    INVALID_STRUCTURE(2, "Structure Invalid", false, false), //structure check failed
    //TODO needed? same as "idle"
//    INVALID_RECIPE(2, "Recipe Invalid", true, false), //no recipe for input
    INVALID_TIER(2, "Recipe Tier Invalid", true, false), //recipe tier requirement not met
    OUTPUT_FULL(2, "Output Full", false, true), //output full can not continue
    NO_POWER(2, "No Power", false, false), //no power
    POWER_LOSS(2, "Power Loss", false, false); //power loss while operating

    public static MachineState[] VALUES;

    static {
        VALUES = values().clone();
    }

    private int overlayId;
    String displayName;
    private boolean recipeCheck, loopTick;

    MachineState(int overlayId, String displayName, boolean recipeCheck, boolean loopTick) {
        this.overlayId = overlayId;
        this.displayName = displayName;
        this.recipeCheck = recipeCheck;
        this.loopTick = loopTick;
    }

    public int getId() {
        return ordinal();
    }

    public int getOverlayId() {
        return overlayId;
    }

    public boolean allowRecipeCheck() {
        return recipeCheck;
    }

    public boolean allowLoopTick() {
        return loopTick;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}