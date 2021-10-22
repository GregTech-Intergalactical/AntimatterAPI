package muramasa.antimatter.gui.widget;

import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WidgetSupplier {

    private final BiFunction<GuiInstance, IGuiElement, Widget> builder;
    private boolean clientOnly = false;

    private Consumer<Widget> root = a -> {
    };
    private Predicate<GuiInstance> validator = a -> true;

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

    public WidgetSupplier onlyIf(Predicate<GuiInstance> predicate) {
        this.validator = this.validator.and(predicate);
        return this;
    }

    public WidgetSupplier setSize(int x, int y, int width, int height) {
        return setPos(x, y).setWH(width, height);
    }

    public WidgetSupplier setWH(int w, int h) {
        this.root = this.root.andThen(a -> {
            a.setW(w);
            a.setH(h);
        });
        return this;
    }

    public boolean shouldAdd(GuiInstance instance) {
        if (!validator.test(instance)) return false;
        if (instance.isRemote) return true;
        return !clientOnly;
    }

    public Widget buildAndAdd(GuiInstance instance, IGuiElement parent) {
        Widget wid = build().apply(instance, parent);
        instance.addWidget(wid);
        return wid;
    }

    public Widget build(GuiInstance instance, IGuiElement parent) {
        return build().apply(instance, parent);
    }

    private BiFunction<GuiInstance, IGuiElement, Widget> build() {
        return (a, b) -> {
            Widget w = this.builder.apply(a, b);
            root.accept(w);
            return w;
        };
    }
}
