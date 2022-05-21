package muramasa.antimatter.machine;

import java.util.Locale;

public enum MachineState implements IAntimatterObject {

    //OverlayID: 0(Idle), 1(Active), 2(Error)

    IDLE(0, "Idle", true, false, true), // Ready/Idle - No operations are being performed
    DISABLED(0, "Disabled", false, false, true), // Powered but stopped on command either via Redstone or Cover
    ACTIVE(1, "Active", true, false, true), // Operating, subjected to recipe checking
    INVALID_STRUCTURE(2, "Structure is Invalid", false, false, true), // Multiblock structure check has failed
    INVALID_TIER(2, "Recipe Tier is Invalid", true, false, false), // Machine tier did not meet recipe's tier requirement
    OUTPUT_FULL(2, "Output Slots are Full", false, true, false), // Output is full
    NO_POWER(2, "No Power", false, false, false), // No power to carry out operation
    POWER_LOSS(2, "Power Loss", true, false, false); // ACTIVE, but there is a power loss with no gain

    public static final MachineState[] VALUES = values();

    private final int overlayId;
    private final String displayName;
    private final boolean recipeCheck, loopTick, renderUpdate;

    // TODO translation keys
    MachineState(int overlayId, String displayName, boolean recipeCheck, boolean loopTick, boolean renderUpdate) {
        this.overlayId = overlayId;
        this.displayName = displayName;
        this.recipeCheck = recipeCheck;
        this.loopTick = loopTick;
        this.renderUpdate = renderUpdate;
        AntimatterAPI.register(MachineState.class, this);
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

    public MachineState getTextureState() {
        switch (this) {
            case ACTIVE:
            case INVALID_STRUCTURE:
                return this;
        }
        return IDLE;
    }

    public static void init() {
        
    }
}