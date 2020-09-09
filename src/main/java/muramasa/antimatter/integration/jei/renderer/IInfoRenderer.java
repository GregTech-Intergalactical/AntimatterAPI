package muramasa.antimatter.integration.jei.renderer;

import net.minecraft.client.gui.FontRenderer;

public interface IInfoRenderer {

    default void drawInfo(FontRenderer renderer, int left, int top) {
        //NOOP
    }
}
