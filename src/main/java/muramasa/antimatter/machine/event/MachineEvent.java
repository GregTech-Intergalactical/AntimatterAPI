package muramasa.antimatter.machine.event;

public enum MachineEvent implements IMachineEvent {

    ITEMS_OUTPUTTED, //When all items for a recipe cycle has been deposited in the output slot
    FLUIDS_OUTPUTTED // When all fluids for a recipe cycle has been deposited in the output slot
}
