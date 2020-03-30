package muramasa.antimatter.machine;

import muramasa.antimatter.registration.IAntimatterObject;

import java.util.Locale;

public enum MachineState implements IAntimatterObject {

    //OverlayID: 0(Idle), 1(Active), 2(Error)

    IDLE(0, "Idle", true, false, true), //ready idle (no input)
    DISABLED(0, "Disabled", false, false, true), //powered but stopped by command (redstone/interface)
    //TODO rename to active or processing?
    ACTIVE(1, "Active", false, false, true), //operating normally
    INVALID_STRUCTURE(2, "Structure Invalid", false, false, true), //structure check failed
    //TODO needed? same as "idle"
//    INVALID_RECIPE(2, "Recipe Invalid", true, false), //no recipe for input
    INVALID_TIER(2, "Recipe Tier Invalid", true, false, false), //recipe tier requirement not met
    OUTPUT_FULL(2, "Output Full", false, true, false), //output full can not continue
    NO_POWER(2, "No Power", true, false, false), //no power
    POWER_LOSS(2, "Power Loss", false, false, false); //power loss while operating

    public static MachineState[] VALUES;

    static {
        VALUES = values().clone();
    }

    private int overlayId;
    String displayName;
    private boolean recipeCheck, loopTick, renderUpdate;

    MachineState(int overlayId, String displayName, boolean recipeCheck, boolean loopTick, boolean renderUpdate) {
        this.overlayId = overlayId;
        this.displayName = displayName;
        this.recipeCheck = recipeCheck;
        this.loopTick = loopTick;
        this.renderUpdate = renderUpdate;
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

    public boolean allowRenderUpdate() {
        return renderUpdate;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getId() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}