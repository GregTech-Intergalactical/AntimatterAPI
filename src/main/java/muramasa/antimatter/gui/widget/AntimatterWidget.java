package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.util.int4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public abstract class AntimatterWidget<T extends Container> extends Widget {
    private final AntimatterContainerScreen<? extends T> screen;
    private final IGuiHandler handler;
    protected int4 uv;
    private ResourceLocation guiLoc;

    public AntimatterWidget(AntimatterContainerScreen<? extends T> screen, IGuiHandler handler, int x, int y, int width, int height, ITextComponent title) {
        super(x, y, width, height, title);
        this.screen = screen;
        this.handler = handler;
    }

    public AntimatterWidget(AntimatterContainerScreen<? extends T> screen, IGuiHandler handler, int x, int y, int width, int height) {
        this(screen, handler, x, y, width, height, StringTextComponent.EMPTY);
    }

    public AntimatterWidget(AntimatterContainerScreen<? extends T> screen, IGuiHandler handler) {
        super(0, 0, 0,0, StringTextComponent.EMPTY);
        this.screen = screen;
        this.handler = handler;
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

    protected IGuiHandler handler() {
        return handler;
    }

    protected void drawTexture(MatrixStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.color4f(1, 1, 1, 1);
        Minecraft.getInstance().textureManager.bindTexture(loc);
        blit(stack, left, top, x, y, sizeX, sizeY);
    }

    public static <T extends Container> WidgetSupplier builder(WidgetSupplier.WidgetProvider source) {
        return new WidgetSupplier(source);
    }
}
