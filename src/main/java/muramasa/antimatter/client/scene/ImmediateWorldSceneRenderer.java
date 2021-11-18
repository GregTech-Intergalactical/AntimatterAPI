package muramasa.antimatter.client.scene;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.world.IBlockDisplayReader;
import org.lwjgl.opengl.GL11;

public class ImmediateWorldSceneRenderer extends WorldSceneRenderer {

    public ImmediateWorldSceneRenderer(IBlockDisplayReader world) {
        super(world);
    }

    @Override
    protected int[] getPositionedRect(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getInstance();

        MainWindow window = mc.getWindow();
        //compute window size from scaled width & height
        int windowWidth = (int) (width / (window.getGuiScaledWidth() * 1.0) * window.getWidth());
        int windowHeight = (int) (height / (window.getGuiScaledHeight() * 1.0) * window.getHeight());
        //translate gui coordinates to window's ones (y is inverted)
        int windowX = (int) (x / (window.getGuiScaledWidth() * 1.0) * window.getWidth());
        int windowY = window.getHeight() - (int) (y / (window.getGuiScaledHeight() * 1.0) * window.getHeight()) - windowHeight;

        return super.getPositionedRect(windowX, windowY, windowWidth, windowHeight);
    }


    @Override
    protected void clearView(int x, int y, int width, int height) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x, y, width, height);
        super.clearView(x, y, width, height);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
