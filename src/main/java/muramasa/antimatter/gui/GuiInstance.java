package muramasa.antimatter.gui;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.core.RTree;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.gui.widget.ButtonWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.mixin.IContainerListeners;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class GuiInstance implements ICanSyncData {

    private int buttonCounter = 0;
    public final IGuiHandler handler;
    public final Container container;
    public final boolean isRemote;
    private final List<SyncHolder> syncData = new ObjectArrayList<>();
    private int indexCounter = 0;

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public AntimatterContainerScreen<?> screen;

    private final List<WidgetSupplier> builders = new ObjectArrayList<>();
    private final RTree<Widget> widgetLookup = new RTree<>();
    private final Set<Widget> widgets = new ObjectOpenHashSet<>();

    //TODO:
    private IGuiElement focus;

    public GuiInstance(IGuiHandler handler, Container container, boolean isRemote) {
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
    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
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

    public void sendPacket(Object pkt) {
        Antimatter.NETWORK.sendToServer(pkt);
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

    @OnlyIn(Dist.CLIENT)
    public ItemStack getHeldItem() {
        return Minecraft.getInstance().player.inventory.getItemStack();
    }

    @Nullable
    public IGuiElement getFocus() {
        return focus;
    }

    public void receivePacket(GuiSyncPacket packet, SyncDirection dir) {
        ByteBuf data = packet.clientData;
        PacketBuffer buf = new PacketBuffer(data);
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
        for (IContainerListener listener : ((IContainerListeners) container).getListeners()) {
            if (listener instanceof ServerPlayerEntity) {
                Antimatter.NETWORK.sendTo(pkt, (ServerPlayerEntity) listener);
            }
        }
    }

    private void writeToServer(final List<SyncHolder> data) {
        GuiSyncPacket pkt = new GuiSyncPacket(data);
        Antimatter.NETWORK.sendToServer(pkt);
    }

    @Override
    public <T> void bind(Supplier<T> supplier, Consumer<T> consumer, Function<PacketBuffer, T> reader, BiConsumer<PacketBuffer, T> writer, BiFunction<Object, Object, Boolean> equality, SyncDirection direction) {
        syncData.add(new SyncHolder(supplier, consumer, reader, writer, indexCounter++, equality, direction));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class SyncHolder {
        public final Supplier source;
        public final Consumer sink;
        public Object current;
        public final Function<PacketBuffer, Object> reader;
        public final BiConsumer<PacketBuffer, Object> writer;
        public final int index;
        public BiFunction<Object, Object, Boolean> equality;
        public final SyncDirection direction;

        public SyncHolder(Supplier<?> source, Consumer<?> sink, Function<PacketBuffer, ?> reader, BiConsumer<PacketBuffer, ?> writer, int index, BiFunction<Object, Object, Boolean> equality, SyncDirection direction) {
            this.source = source;
            this.index = index;
            this.sink = sink;
            this.reader = (Function<PacketBuffer, Object>) reader;
            this.writer = (BiConsumer<PacketBuffer, Object>) writer;
            this.equality = equality;
            this.direction = direction;
        }
    }
}
