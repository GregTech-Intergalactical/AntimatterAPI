package muramasa.antimatter.gui.event;


import net.minecraft.network.PacketBuffer;

import java.util.function.BiFunction;

public enum GuiEvents implements IGuiEvent.IGuiEventFactory {

    ITEM_EJECT("ie", GuiEvent::new),
    FLUID_EJECT("fe", GuiEvent::new),
    EXTRA_BUTTON("eb", GuiEvent::new), // When button which added thought addButton() pressed
    EXTRA_SWITCH("es", GuiEvent::new); // When button which added thought addSwitch() toggled

    private final String id;
    private final BiFunction<IGuiEvent.IGuiEventFactory, PacketBuffer, IGuiEvent> supplier;

    GuiEvents(String id, BiFunction<IGuiEvent.IGuiEventFactory, PacketBuffer, IGuiEvent> supplier) {
        this.id = id;
        this.supplier = supplier;
        register();
    }

    public static void init() {
        SlotClickEvent.init();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public BiFunction<IGuiEvent.IGuiEventFactory, PacketBuffer, IGuiEvent> factory() {
        return supplier;
    }

    public static class GuiEvent implements IGuiEvent {
        public final int[] data;
        public final IGuiEventFactory factory;

        GuiEvent(IGuiEventFactory factory, PacketBuffer buffer) {
            this.data = buffer.readVarIntArray();
            this.factory = factory;
        }

        public GuiEvent(IGuiEventFactory factory, int... data) {
            this.factory = factory;
            this.data = data;
        }

        @Override
        public void write(PacketBuffer buffer) {
            buffer.writeVarIntArray(data);
        }

        @Override
        public IGuiEventFactory getFactory() {
            return factory;
        }
    }
}
