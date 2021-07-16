package muramasa.antimatter.gui.widget;

import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

public class WidgetSupplier<T extends AntimatterContainer> {

    @FunctionalInterface
    public interface WidgetProvider<T extends AntimatterContainer> {
        Widget get(AntimatterContainerScreen<? extends T> screen);
    }

    private final WidgetProvider<T> source;

    private Consumer<Widget> root;

    public WidgetSupplier(WidgetProvider<T> source) {
        this.source = source;
        this.root = a -> {};
    }

    public WidgetSupplier<T> setPos(int x, int y) {
        Consumer<Widget> old = this.root;
        this.root = a -> {
            a.x = x;
            a.y = y;
            old.accept(a);
        };
        return this;
    }

    public WidgetSupplier<T> setWH(int w, int h) {
        Consumer<Widget> old = this.root;
        this.root = a -> {
            a.setWidth(w);
            a.setHeight(h);
            old.accept(a);
        };
        return this;
    }

    public <U extends AntimatterContainer> WidgetProvider<U> cast() {
        return screen -> {
            Widget widget = this.source.get((AntimatterContainerScreen<? extends T>) screen);
            root.accept(widget);
            return widget;
        };
    }
}
