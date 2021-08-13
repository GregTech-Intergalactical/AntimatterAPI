package muramasa.antimatter.gui;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.*;

public class GuiInstance implements ICanSyncData {

    private int buttonCounter = 0;
    public final IGuiHandler handler;
    public final Container container;
    public final boolean isRemote;
    private final List<SyncHolder> syncData = new ObjectArrayList<>();
    private int indexCounter = 0;
    private final Map<Class<? extends Widget>, List<Widget>> widgets = new Object2ObjectLinkedOpenHashMap<>();
    public final ITextComponent title;

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public AntimatterContainerScreen<?> screen;

    private final PriorityQueue<Widget> sortedWidgetSet = new PriorityQueue<>((a,b) -> Integer.compare(b.depth(), a.depth()));
    private final Rectangle mutableRectangle = new Rectangle();

    public GuiInstance(IGuiHandler handler, Container container, boolean isRemote) {
        this.handler = handler;
        this.isRemote = isRemote;
        this.container = container;
        if (handler instanceof INamedContainerProvider) {
            this.title = ((INamedContainerProvider)handler).getDisplayName();
        } else {
            this.title = StringTextComponent.EMPTY;
        }
    }

    public void setRootElement(IGuiElement parent) {
        for (Widget mutableWidget : mutableWidgets()) {
            if (mutableWidget.parent == null || mutableWidget.parent == parent) mutableWidget.setParent(parent);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void setScreen(AntimatterContainerScreen<?> screen) {
        this.screen = screen;
    }

    public boolean isOnTop(Widget wid, int mouseX, int mouseY) {
        Rectangle r = new Rectangle(wid.realX(), wid.realY(), wid.getW(), wid.getH());

        for (Widget widget : sortedWidgetSet) {
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

    private void initWidgets() {
        handler.addWidgets(this);
        for (List<Widget> value : this.widgets.values()) {
            value.forEach(Widget::init);
        }
    }

    public void init() {
        initWidgets();
    }

    public void recomputeDepth(Widget widget) {
        if (sortedWidgetSet.remove(widget)) {
            sortedWidgetSet.add(widget);
        }
    }

    public GuiInstance addWidget(Widget widget) {
        Class<? extends Widget> clazz = widget.getClass();
        widgets.computeIfAbsent(clazz, a -> new ObjectArrayList<>()).add(widget);
        sortedWidgetSet.add(widget);
        return this;
    }

    public GuiInstance addWidget(WidgetSupplier.WidgetProvider provider) {
        addWidget(provider.get(this));
        return this;
    }


    public GuiInstance addWidget(WidgetSupplier provider) {
        addWidget(provider.get());
        return this;
    }

    public GuiInstance addButton(int x, int y, int w, int h, ButtonBody body) {
        addWidget(i -> ButtonWidget.build(new ResourceLocation(i.handler.getDomain(), "textures/gui/button/gui_buttons.png"), body, null, GuiEvent.EXTRA_BUTTON, buttonCounter++).setSize(x,y,w,h).get().get(i));
        return this;
    }

    public Iterable<Widget> widgets() {
        return sortedWidgetSet;
    }

    public Iterable<Widget> mutableWidgets() {
        return new ArrayList<>(this.sortedWidgetSet);
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
