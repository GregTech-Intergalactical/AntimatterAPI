package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.ButtonBody;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractSwitchWidget extends ButtonWidget {

    private final ResourceLocation res;
    private ButtonBody on, off;
    private ButtonOverlay body;
    private boolean state;

    protected final ISwitchable onSwitch;

    protected AbstractSwitchWidget(GuiInstance instance, IGuiElement parent, ResourceLocation res, ButtonBody on, ButtonBody off, ISwitchable onSwitch) {
        super(instance, parent, res, on, off, null, null);
        this.res = res;
        this.on = on;
        this.off = off;
        this.onSwitch = onSwitch;
        this.setClick(b -> onPress());
    }

    protected AbstractSwitchWidget(GuiInstance instance, IGuiElement parent, ResourceLocation res, ButtonOverlay body, ISwitchable onSwitch, boolean defaultState) {
        super(instance, parent, res, null, body, null, null);
        this.res = res;
        this.body = body;
        this.onSwitch = onSwitch;
        this.state = defaultState;
        this.setClick(b -> onPress());
    }

    protected AbstractSwitchWidget(GuiInstance instance, IGuiElement parent, ResourceLocation res, ButtonOverlay body, String text, ISwitchable onSwitch) {
        super(instance, parent, res, null, body, null, null);
        this.res = res;
        this.body = body;
        this.onSwitch = onSwitch;
        this.setClick(b -> onPress());
    }

    protected boolean state() {
        return state;
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, res);
        RenderSystem.disableDepthTest();
        boolean mouseOver = isInside(mouseX, mouseY);
        if (body == null) {
            ButtonBody body = isSwitched() ? on : off;
            int xTex = 0;
            int yTex = 0;
            if (mouseOver) {;
                yTex += body.getH();
            }
            ScreenWidget.blit(matrixStack, realX(), realY(), getW(), getH(), xTex, yTex, body.getW(), body.getH(), body.getW(), body.getH() * 2);
        } else {
            int xTex = 0;
            int yTex = 0;
            float f = isSwitched() ? 1.0F : mouseOver ? 0.75F : 0.5F;
            RenderSystem.setShaderColor(f, f, f, 1.0F);
            ScreenWidget.blit(matrixStack, realX(), realY(), getW(), getH(), xTex, yTex, body.getW(), body.getH(), body.getW(), body.getH());
        }
        RenderSystem.enableDepthTest();
    }

    public boolean isSwitched() {
        return state;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
    }

    public void onPress() {
        this.state = !this.state;
        this.onSwitch.onSwitch(this, isSwitched());
    }

    public interface ISwitchable {
        void onSwitch(AbstractSwitchWidget button, boolean state);
    }
}
