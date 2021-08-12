package muramasa.antimatter.gui;

import com.google.common.collect.Iterators;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.mixin.IContainerListeners;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.*;

public class GuiInstance implements ICanSyncData {

    public final IGuiHandler handler;
    public final boolean isRemote;
    private final List<SyncHolder> syncData = new ObjectArrayList<>();
    private int indexCounter = 0;
    private final Map<Class<? extends Widget>, List<Widget>> widgets = new Object2ObjectLinkedOpenHashMap<>();
    private Supplier<List<IContainerListener>> listeners;

    public GuiInstance(IGuiHandler handler, boolean isRemote) {
        this.handler = handler;
        this.isRemote = isRemote;
    }

    public void root(IGuiElement parent) {
        widgets().forEach(t -> t.parent = parent);
    }

    public void init(Container source) {
        listeners = ((IContainerListeners) source)::getListeners;
        GuiData data = handler.getStatic();
        data.createWidgets(this);
        handler.addWidgets(this);
        for (List<Widget> value : this.widgets.values()) {
            value.forEach(t -> t.init(handler, this, source));
        }
    }

    public void addWidget(Widget widget) {
        Class<? extends Widget> clazz = widget.getClass();
        widgets.computeIfAbsent(clazz, a -> new ObjectArrayList<>()).add(widget);
    }

    public void addWidget(WidgetSupplier.WidgetProvider provider) {
        addWidget(provider.get(this));
    }

    public Iterable<Widget> widgets() {
        return widgets.size() > 0 ? () -> widgets.values().stream().map(List::iterator).reduce(Iterators::concat).get() : Collections::emptyIterator;
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
        List<IContainerListener> list = listeners.get();
        GuiSyncPacket pkt = new GuiSyncPacket(data);
        for (IContainerListener listener : list) {
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
