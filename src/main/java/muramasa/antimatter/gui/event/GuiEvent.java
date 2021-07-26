package muramasa.antimatter.gui.event;


public enum GuiEvent implements IGuiEvent {

    ITEM_EJECT("ie"),
    FLUID_EJECT("fe"),
    EXTRA_BUTTON("eb"), // When button which added thought addButton() pressed
    EXTRA_SWITCH("es"); // When button which added thought addSwitch() toggled

    private final String id;

    GuiEvent(String id) {
        this.id = id;
        register();
    }

    @Override
    public String getId() {
        return id;
    }
}
