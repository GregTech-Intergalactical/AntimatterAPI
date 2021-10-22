package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Function;

public class TextWidget extends Widget {

    private final Function<TextWidget, String> getter;
    private final int color;

    protected TextWidget(GuiInstance gui, IGuiElement parent, String text, int color) {
        super(gui, parent);
        this.getter = a -> text;
        this.color = color;
    }

    public static WidgetSupplier build(String text, int color) {
        return builder((a, b) -> new TextWidget(a, b, text, color)).clientSide();
    }

    @Override
    public void render(MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        this.drawText(matrixStack, new StringTextComponent(getter.apply(this)), realX(), realY(), color);
    }
}
