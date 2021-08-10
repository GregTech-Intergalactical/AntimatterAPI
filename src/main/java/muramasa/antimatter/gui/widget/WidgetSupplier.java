package muramasa.antimatter.gui.widget;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.inventory.container.Container;

import java.util.function.Consumer;

public class WidgetSupplier<T extends Container> {

    @FunctionalInterface
    public interface WidgetProvider<T extends Container> {
        Widget get(AntimatterContainerScreen<? extends T> screen, IGuiHandler handler);
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

    public WidgetSupplier<T> setSize(int x, int y, int width, int height) {
        return setPos(x,y).setWH(width, height);
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

    public <U extends Container> WidgetProvider<U> cast() {
        return (screen, handler) -> {
            Widget widget = this.source.get((AntimatterContainerScreen<? extends T>) screen, handler);
            root.accept(widget);
            return widget;
        };
    }
}
