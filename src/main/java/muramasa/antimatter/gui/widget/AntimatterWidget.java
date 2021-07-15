package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Consumer;

public abstract class AntimatterWidget<T extends AntimatterContainer> extends Widget {
    private final AntimatterContainerScreen<? extends T> screen;
    private ResourceLocation guiLoc;

    public AntimatterWidget(AntimatterContainerScreen<? extends T> screen, int x, int y, int width, int height, ITextComponent title) {
        super(x, y, width, height, title);
        this.screen = screen;
    }

    public AntimatterWidget(AntimatterContainerScreen<? extends T> screen, int x, int y, int width, int height) {
        this(screen, x, y, width, height, StringTextComponent.EMPTY);
    }

    public AntimatterWidget(AntimatterContainerScreen<? extends T> screen) {
        super(0, 0, 0,0, StringTextComponent.EMPTY);
        this.screen = screen;
    }

    protected T container() {
        return screen.getContainer();
    }

    protected AntimatterContainerScreen<? extends T> screen() {
        return screen;
    }

    public AntimatterWidget<T> setGuiLoc(ResourceLocation loc) {
        this.guiLoc = loc;
        return this;
    }

    protected void drawTexture(MatrixStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.color4f(1, 1, 1, 1);
        Minecraft.getInstance().textureManager.bindTexture(loc);
        blit(stack, left, top, x, y, sizeX, sizeY);
    }

    public static <T extends AntimatterContainer> WidgetSupplier<T> builder(GuiData.WidgetProvider<T> source) {
        return new WidgetSupplier<>(source);
    }

    public static class WidgetSupplier<T extends AntimatterContainer> {

        private final GuiData.WidgetProvider<T> source;

        private Consumer<AntimatterWidget<T>> root;

        public WidgetSupplier(GuiData.WidgetProvider<T> source) {
            this.source = source;
            this.root = a -> {};
        }

        public WidgetSupplier<T> setPos(int x, int y) {
            this.root = a -> {
                a.x = x;
                a.y = y;
                this.root.accept(a);
            };
            return this;
        }

        public WidgetSupplier<T> setWH(int w, int h) {
            this.root = a -> {
                a.setWidth(w);
                a.setHeight(h);
                this.root.accept(a);
            };
            return this;
        }

        public GuiData.WidgetProvider<T> build() {
            return screen -> {
                AntimatterWidget<T> widget = this.source.get(screen);
                root.accept(widget);
                return widget;
            };
        }
    }

}
