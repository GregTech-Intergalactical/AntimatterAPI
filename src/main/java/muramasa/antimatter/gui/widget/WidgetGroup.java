package muramasa.antimatter.gui.widget;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class WidgetGroup extends Widget {

    private final List<Widget> children = new ObjectArrayList<>();

    protected WidgetGroup(@Nonnull GuiInstance gui, @Nullable IGuiElement parent) {
        super(gui, parent);
    }

    public List<Widget> getChildren() {
        return children;
    }

    @Override
    public void setDepth(int depth) {
        super.setDepth(depth);
        for (Widget child : children) {
            child.setDepth(depth+1);
        }
    }

    protected void addWidget(WidgetSupplier w) {
        Widget wid = w.buildAndAdd(this.gui, this);
        wid.setDepth(depth()+1);
        this.children.add(wid);
    }
}
