package muramasa.antimatter.gui;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.gui.widget.ButtonWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.mixin.IContainerListeners;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.*;

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
    private final TreeMap<Integer, Set<Widget>> sortedWidgetSet = new TreeMap<>(Comparator.reverseOrder());
    private final Rectangle mutableRectangle = new Rectangle();

    public GuiInstance(IGuiHandler handler, Container container, boolean isRemote) {
        this.handler = handler;
        this.isRemote = isRemote;
        this.container = container;
    }

    public void setRootElement(IGuiElement parent) {
        for (Widget mutableWidget : mutableWidgets()) {
            if (mutableWidget.parent == null || mutableWidget.parent == parent) mutableWidget.setParent(parent);
        }
    }

    public void rescale(IGuiElement root) {
        for (Widget w : mutableWidgets()) {
            if (w.parent == root) w.updateSize();
        }
    }

    public boolean isOnTop(Widget wid, int mouseX, int mouseY) {
        Rectangle r = new Rectangle(wid.realX(), wid.realY(), wid.getW(), wid.getH());
        for (Iterator<Widget> w = this.sortedWidgetSet.headMap(wid.depth()).values().stream().flatMap(t -> t.stream()).iterator(); w.hasNext(); ) {
            Widget widget = w.next();
            if (widget.isEnabled() && widget.isVisible() && widget.depth() > wid.depth()) {
                mutableRectangle.setBounds(widget.realX(), widget.realY(), widget.getW(), widget.getH());
                if (r.intersects(mutableRectangle)) {
                    if (widget.isInside(mouseX, mouseY)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void initWidgets(IGuiElement parent) {
        handler.addWidgets(this, parent);
        for (WidgetSupplier builder : builders) {
            if (!builder.shouldAdd(this)) continue;
            Widget w = builder.get(this, parent);
            putWidget(w);
            w.init();
        }
    }

    private void putWidget(Widget w) {
        this.sortedWidgetSet.computeIfAbsent(w.depth(), t -> new ObjectOpenHashSet<>()).add(w);
    }

    public void init() {
        initWidgets(null);
    }

    @OnlyIn(Dist.CLIENT)
    public void initClient(AntimatterContainerScreen<?> parent) {
        this.screen = parent;
        initWidgets(parent);
        for (Widget mut : widgets()) {
            if (mut.parent == null) mut.setParent(parent);
        }
    }

    public void recomputeDepth(int old, Widget widget) {
        if (this.sortedWidgetSet.getOrDefault(old, Collections.emptySet()).remove(widget)) {
            putWidget(widget);
        }
    }

    public GuiInstance addWidget(Widget widget) {
        putWidget(widget);
        return this;
    }

    public GuiInstance addWidget(WidgetSupplier provider) {
        builders.add(provider);
        return this;
    }

    public GuiInstance addButton(int x, int y, int w, int h, ButtonBody body) {
        addWidget(ButtonWidget.build("textures/gui/button/gui_buttons.png", body, null, GuiEvent.EXTRA_BUTTON, buttonCounter++).setSize(x,y,w,h));
        return this;
    }

    public Iterable<Widget> widgets() {
        return () -> sortedWidgetSet.values().stream().flatMap(Collection::stream).iterator();
    }

    public Iterable<Widget> reverseWidgets() {
        return () -> sortedWidgetSet.descendingMap().values().stream().flatMap(Collection::stream).iterator();
    }
    //It says mutableWidgets, but it means that you can modify the actual map of widgets during iteration.
    public Iterable<Widget> mutableWidgets() {
        return ImmutableList.copyOf(widgets());
    }

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

    public void receivePacket(GuiSyncPacket packet) {
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

    @Override
    public <T> void bind(Supplier<T> supplier, Consumer<T> consumer, Function<PacketBuffer, T> reader, BiConsumer<PacketBuffer, T> writer, BiFunction<Object, Object, Boolean> equality) {
        syncData.add(new SyncHolder(supplier, consumer, reader, writer, indexCounter++, equality));
    }

    public static class SyncHolder {
        public final Supplier source;
        public final Consumer sink;
        public Object current;
        public final Function<PacketBuffer, Object> reader;
        public final BiConsumer<PacketBuffer, Object> writer;
        public final int index;
        public BiFunction<Object, Object, Boolean> equality;
        public SyncHolder(Supplier<?> source, Consumer<?> sink, Function<PacketBuffer, ?> reader, BiConsumer<PacketBuffer, ?> writer, int index, BiFunction<Object, Object, Boolean> equality) {
            this.source = source;
            this.index = index;
            this.sink = sink;
            this.reader = (Function<PacketBuffer, Object>) reader;
            this.writer = (BiConsumer<PacketBuffer, Object>) writer;
            this.equality = equality;
        }
    }
}
