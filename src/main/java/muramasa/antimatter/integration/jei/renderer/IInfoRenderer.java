package muramasa.antimatter.integration.jei.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IInfoRenderer<T extends InfoRenderWidget<T>> {
    @OnlyIn(Dist.CLIENT)
    void drawInfo(T instance, MatrixStack stack, FontRenderer renderer, int left, int top);
}
