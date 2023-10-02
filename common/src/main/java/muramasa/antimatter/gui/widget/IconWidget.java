package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IconWidget extends Widget {
    ResourceLocation texture;
    protected IconWidget(@NotNull GuiInstance gui, @Nullable IGuiElement parent, ResourceLocation texture) {
        super(gui, parent);
        this.texture = texture;
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        drawTexture(matrixStack, texture, realX(), realY(), 0, 0, getW(), getH(), getW(), getH());
    }

    public static WidgetSupplier build(ResourceLocation texture, int x, int y, int width, int height) {
        return builder((a, b) -> new IconWidget(a, b, texture)).setSize(x,y, width, height);
    }
}
