package muramasa.antimatter.gui;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.core.RTree;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.gui.widget.ButtonWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import trinsdar.networkapi.api.INetwork;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class GuiInstance implements ICanSyncData {

    private int buttonCounter = 0;
    public final IGuiHandler handler;
    public final AbstractContainerMenu container;
    public final boolean isRemote;
    private final List<SyncHolder> syncData = new ObjectArrayList<>();
    private int indexCounter = 0;

    @Environment(EnvType.CLIENT)
    @Nullable
    public AntimatterContainerScreen<?> screen;

    private final List<WidgetSupplier> builders = new ObjectArrayList<>();
    private final RTree<Widget> widgetLookup = new RTree<>();
    private final Set<Widget> widgets = new ObjectOpenHashSet<>();

    //TODO:
    private IGuiElement focus;

    public GuiInstance(IGuiHandler handler, AbstractContainerMenu container, boolean isRemote) {
        this.handler = handler;
        this.isRemote = isRemote;
        this.container = container;
    }

    /**
     * Rescales the GUI window, sets all root widgets.
     *
     * @param root top level widget, e.g. screen.
     */
    public void rescale(IGuiElement root) {
        for (Widget w : unsortedWidgets()) {
            if (w.parent == root) w.updateSize();
        }
    }

    /**
     * Returns all widgets under the mouse.
     *
     * @param mouseX x position
     * @param mouseY y position
     * @return iterable widget list
     */
    public Iterable<Widget> getWidgets(double mouseX, double mouseY) {
        return () -> {
            Stream<Widget> stream = this.widgetLookup.search(new float[]{(float) mouseX, (float) mouseY}, new float[]{0f, 0f}).stream();
            return stream.sorted((a, b) -> Integer.compare(b.depth(), a.depth())).iterator();
        };
    }

    public Optional<Widget> getTopLevelWidget(double mouseX, double mouseY) {
        Iterator<Widget> iterator = getWidgets(mouseX, mouseY).iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    /**
     * Returns all widgets available in reverse depth order (for e.g. rendering).
     *
     * @return iterable widget list
     */
    @Environment(EnvType.CLIENT)
    public Iterable<Widget> widgetsToRender() {
        return () -> this.widgets.stream().filter(t -> t.parent == this.screen).sorted(Comparator.comparing(Widget::depth)).iterator();
    }

    /**
     * Is the widget top level widget at this mouse position?
     *
     * @param wid    widget to check
     * @param mouseX mouse X
     * @param mouseY mouse Y
     * @return if it is on top.
     */
    public boolean isOnTop(Widget wid, double mouseX, double mouseY) {
        return this.getWidgets(mouseX, mouseY).iterator().next() == wid;
    }

    /**
     * Notifies the instance that a widget has rescaled.
     *
     * @param wid  the widget
     * @param oldX oldX
     * @param oldY oldY
     * @param oldW oldW
     * @param oldH oldH
     */
    public void rescaleWidget(Widget wid, int oldX, int oldY, int oldW, int oldH) {
        if (!wid.isEnabled()) return;
        if (!widgets.contains(wid)) return;
        float x = (float) oldX;
        float y = (float) oldY;
        float w = (float) oldW;
        float h = (float) oldH;
        if (widgetLookup.delete(new float[]{x, y}, new float[]{w, h}, wid)) {
            widgetLookup.insert(wid);
        }
    }

    public void updateWidgetStatus(Widget wid) {
        if (wid.isEnabled()) {
            widgetLookup.insert(wid);
        } else {
            widgetLookup.delete(wid);
        }
    }

    private void initWidgets(IGuiElement parent) {
        handler.addWidgets(this, parent);
        for (WidgetSupplier builder : builders) {
            if (!builder.shouldAdd(this)) continue;
            builder.buildAndAdd(this, parent);
        }
    }

    private void putWidget(Widget w) {
        this.widgets.add(w);
        updateWidgetStatus(w);
        w.init();
    }

    public void init() {
        initWidgets(null);
    }

    @Environment(EnvType.CLIENT)
    public void initClient(AntimatterContainerScreen<?> parent) {
        this.screen = parent;
        initWidgets(parent);
        for (Widget mut : unsortedWidgets()) {
            if (mut.parent == null) mut.setParent(parent);
        }
    }

    /**
     * Adds a widget to this instance. If the widget's parent == screen
     * the widget will be automatically rendered by the GUI.
     * However, all widgets will receive events like mouse click.
     *
     * @param widget te widget to add.
     * @return this
     */
    public GuiInstance addWidget(Widget widget) {
        putWidget(widget);
        return this;
    }

    public GuiInstance addWidget(WidgetSupplier provider) {
        builders.add(provider);
        return this;
    }

    public GuiInstance addButton(int x, int y, int w, int h, ButtonBody body) {
        addWidget(ButtonWidget.build("textures/gui/button/gui_buttons.png", body, null, GuiEvents.EXTRA_BUTTON, buttonCounter++).setSize(x, y, w, h));
        return this;
    }

    public GuiInstance addButton(int x, int y, int w, int h, ButtonBody body, String tooltipKey) {
        addWidget(ButtonWidget.build("textures/gui/button/gui_buttons.png", body, null, GuiEvents.EXTRA_BUTTON, buttonCounter++, tooltipKey).setSize(x, y, w, h));
        return this;
    }

    public Iterable<Widget> unsortedWidgets() {
        return widgets;
    }

    /**
     * Called on the client to update.
     */
    public void update(double mouseX, double mouseY) {
        getTopLevelWidget(mouseX, mouseY).ifPresent(t -> this.focus = t);
        unsortedWidgets().forEach(t -> t.update(mouseX, mouseY));
        List<SyncHolder> toSync = new ObjectArrayList<>();
        for (SyncHolder sync : this.syncData) {
            if (sync.direction == SyncDirection.SERVER_TO_CLIENT) continue;
            Object value = sync.source.get();
            if (!sync.equality.apply(value, sync.current)) {
                sync.current = value;
                toSync.add(sync);
            }
        }
        if (toSync.size() > 0)
            writeToServer(toSync);
    }

    public void sendPacket(AbstractGuiEventPacket pkt) {
        INetwork.getInstance().sendToServer(pkt.getChannelId(), pkt);
    }

    /**
     * Called on the server to update.
     */
    public void update() {
        List<SyncHolder> toSync = new ObjectArrayList<>();
        for (SyncHolder sync : this.syncData) {
            Object value = sync.source.get();
            if (!sync.equality.apply(value, sync.current)) {
                sync.current = value;
                toSync.add(sync);
            }
        }
        if (toSync.size() > 0)
            write(toSync);
    }

    public ItemStack getHeldItem() {
        return this.container.getCarried();
    }

    @Nullable
    public IGuiElement getFocus() {
        return focus;
    }

    public void receivePacket(GuiSyncPacket packet, SyncDirection dir) {
        ByteBuf data = packet.clientData;
        FriendlyByteBuf buf = new FriendlyByteBuf(data);
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            int offset = buf.readVarInt();
            Object o = this.syncData.get(offset).reader.apply(buf);
            SyncHolder holder = this.syncData.get(offset);
            holder.current = o;
            holder.sink.accept(o);
        }
    }

    private void write(final List<SyncHolder> data) {
        GuiSyncPacket pkt = new GuiSyncPacket(data);
        for (ServerPlayer listener : ((IAntimatterContainer)container).listeners()) {
            INetwork.getInstance().sendToClient(AntimatterNetwork.GUI_SYNC_PACKET_ID, pkt, listener);
        }
    }

    private void writeToServer(final List<SyncHolder> data) {
        GuiSyncPacket pkt = new GuiSyncPacket(data);
        INetwork.getInstance().sendToServer(AntimatterNetwork.GUI_SYNC_PACKET_ID, pkt);
    }

    @Override
    public <T> void bind(Supplier<T> supplier, Consumer<T> consumer, Function<FriendlyByteBuf, T> reader, BiConsumer<FriendlyByteBuf, T> writer, BiFunction<Object, Object, Boolean> equality, SyncDirection direction) {
        syncData.add(new SyncHolder(supplier, consumer, reader, writer, indexCounter++, equality, direction));
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class SyncHolder {
        public final Supplier source;
        public final Consumer sink;
        public Object current;
        public final Function<FriendlyByteBuf, Object> reader;
        public final BiConsumer<FriendlyByteBuf, Object> writer;
        public final int index;
        public BiFunction<Object, Object, Boolean> equality;
        public final SyncDirection direction;

        public SyncHolder(Supplier<?> source, Consumer<?> sink, Function<FriendlyByteBuf, ?> reader, BiConsumer<FriendlyByteBuf, ?> writer, int index, BiFunction<Object, Object, Boolean> equality, SyncDirection direction) {
            this.source = source;
            this.index = index;
            this.sink = sink;
            this.reader = (Function<FriendlyByteBuf, Object>) reader;
            this.writer = (BiConsumer<FriendlyByteBuf, Object>) writer;
            this.equality = equality;
            this.direction = direction;
        }
    }
}
