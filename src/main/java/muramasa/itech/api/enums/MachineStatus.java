package muramasa.itech.api.enums;

public enum MachineStatus {

    INVALID_STRUCTURE,
    INVALID_RECIPE,
    NO_POWER,
    IDLE,
    POWER_LOSS,
    DISABLED,
    ACTIVE
}

/*
- power loss while operating
- output full can not continue
- no recipe for input
- powered but stopped by command (redstone/interface)
- no power
- recipe tier requirement not met
- ready idle (no input)
 */