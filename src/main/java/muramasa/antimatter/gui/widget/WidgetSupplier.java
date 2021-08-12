package muramasa.antimatter.gui.widget;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import net.minecraft.inventory.container.Container;

import java.util.function.Consumer;

public class WidgetSupplier {

    @FunctionalInterface
    public interface WidgetProvider {
        Widget get(final GuiInstance gui);
    }

    private final WidgetProvider source;

    private Consumer<Widget> root;

    public WidgetSupplier(WidgetProvider source) {
        this.source = source;
        this.root = a -> {};
    }

    public WidgetSupplier setPos(int x, int y) {
        Consumer<Widget> old = this.root;
        this.root = a -> {
            a.setX(x);
            a.setY(y);
            old.accept(a);
        };
        return this;
    }

    public WidgetSupplier setSize(int x, int y, int width, int height) {
        return setPos(x,y).setWH(width, height);
    }

    public WidgetSupplier setWH(int w, int h) {
        Consumer<Widget> old = this.root;
        this.root = a -> {
            a.setW(w);
            a.setH(h);
            old.accept(a);
        };
        return this;
    }

    public WidgetProvider get() {
        return a -> {
            Widget w = this.source.get(a);
            root.accept(w);
            return w;
        };
    }
}
