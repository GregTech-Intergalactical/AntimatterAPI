package muramasa.antimatter.integration.jei.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.GuiInstance;
import net.minecraft.client.gui.FontRenderer;

public interface IInfoRenderer {

    default void drawInfo(GuiInstance instance, MatrixStack stack, FontRenderer renderer, int left, int top) {

    }
}
