package muramasa.antimatter.gui.event;

public enum GuiEvent implements IGuiEvent {
    ITEM_EJECT,
    FLUID_EJECT,
    EXTRA_BUTTON, // When button which added thought addButton() pressed
    EXTRA_SWITCH; // When button which added thought addSwitch() toggled
}
