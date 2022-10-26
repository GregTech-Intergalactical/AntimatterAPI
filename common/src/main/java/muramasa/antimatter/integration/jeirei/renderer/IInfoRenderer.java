package muramasa.antimatter.integration.jeirei.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
;
;

public interface IInfoRenderer<T extends InfoRenderWidget<T>> {
    /**
     * @param instance
     * @param stack
     * @param renderer
     * @param left
     * @param top
     * @return offset that was rendered.
     */
    @Environment(EnvType.CLIENT)
    int drawInfo(T instance, PoseStack stack, Font renderer, int left, int top);
}
