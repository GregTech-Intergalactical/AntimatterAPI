package muramasa.antimatter.gui.widget;

import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class WidgetSupplier {

    private final BiFunction<GuiInstance, IGuiElement, Widget> builder;
    private boolean clientOnly = false;

    private Consumer<Widget> root = a -> {};

    public WidgetSupplier(BiFunction<GuiInstance, IGuiElement, Widget> source) {
        this.builder = source;
    }

    public static WidgetSupplier build(BiFunction<GuiInstance, IGuiElement, Widget> b) {
        return new WidgetSupplier(b);
    }

    public WidgetSupplier setPos(int x, int y) {
        this.root = this.root.andThen(a -> {
            a.setX(x);
            a.setY(y);
        });
        return this;
    }

    public WidgetSupplier clientSide() {
        clientOnly = true;
        return this;
    }

    public WidgetSupplier setSize(int x, int y, int width, int height) {
        return setPos(x,y).setWH(width, height);
    }

    public WidgetSupplier setWH(int w, int h) {
        this.root = this.root.andThen(a -> {
            a.setW(w);
            a.setH(h);
        });
        return this;
    }

    public boolean shouldAdd(GuiInstance instance) {
        if (instance.isRemote) return true;
        return !clientOnly;
    }

    public Widget get(GuiInstance instance, IGuiElement parent) {
        return get().apply(instance, parent);
    }

    public BiFunction<GuiInstance, IGuiElement, Widget> get() {
        return (a,b) -> {
            Widget w = this.builder.apply(a,b);
            root.accept(w);
            return w;
        };
    }
}
