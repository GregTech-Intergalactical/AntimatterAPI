package muramasa.antimatter.client.scene;

import net.minecraft.client.renderer.RenderType;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: KilaBash
 * @Date: 2021/08/25
 * @Description: Scene Render State hooks.
 * This is where you decide whether or not this group of pos should be rendered. What other requirements do you have for rendering.
 */
public interface ISceneRenderHook {
    void apply(boolean isTESR, RenderType layer);
}
