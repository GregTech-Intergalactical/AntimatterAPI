package muramasa.antimatter.gui.event;

public enum GuiEvent implements IGuiEvent {
    ITEM_EJECT,
    FLUID_EJECT,
    MACHINE_BUTTON, // When button which added thought addButton() pressed
    MACHINE_SWITCH, // When button which added thought addSwitch() toggled
    COVER_BUTTON, // When button which added thought addButton() pressed
    COVER_SWITCH; // When button which added thought addSwitch() toggled
}
