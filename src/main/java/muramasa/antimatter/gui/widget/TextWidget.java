package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.Widget;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Function;

public class TextWidget extends Widget {

    private final Function<TextWidget, String> getter;
    private final int color;

    protected TextWidget(GuiInstance gui, String text, int color) {
        super(gui);
        this.getter = a -> text;
        this.color = color;
    }

    public static WidgetSupplier build(String text, int color) {
        return builder(gui -> new TextWidget(gui, text, color)).clientSide();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.drawText(matrixStack, new StringTextComponent(getter.apply(this)), realX(), realY(), color);
    }
}
