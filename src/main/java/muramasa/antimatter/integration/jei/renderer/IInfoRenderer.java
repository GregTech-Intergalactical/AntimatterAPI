package muramasa.antimatter.integration.jei.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IInfoRenderer<T extends InfoRenderWidget<T>> {
    /**
     * @param instance
     * @param stack
     * @param renderer
     * @param left
     * @param top
     * @return offset that was rendered.
     */
    @OnlyIn(Dist.CLIENT)
    int drawInfo(T instance, PoseStack stack, Font renderer, int left, int top);
}
