package muramasa.antimatter.machine.event;

public enum ContentEvent implements IMachineEvent {

    ITEM_INPUT_CHANGED,
    ITEM_OUTPUT_CHANGED,
    ITEM_CELL_CHANGED,
    FLUID_INPUT_CHANGED,
    FLUID_OUTPUT_CHANGED,
    ENERGY_CHANGED
}
