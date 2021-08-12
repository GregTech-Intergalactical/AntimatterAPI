package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.ButtonBody;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public abstract class AbstractSwitchWidget extends AbstractButton {

    private final ResourceLocation res;
    private ButtonBody on, off;
    private ButtonOverlay body;
    private boolean state;
    public final AntimatterContainerScreen<?> screen;
    private final IGuiHandler handler;

    protected final AbstractSwitchWidget.ISwitchable onSwitch;

    protected AbstractSwitchWidget(AntimatterContainerScreen<?> screen, IGuiHandler handler, ResourceLocation res, ButtonBody on, ButtonBody off, ISwitchable onSwitch) {
        super(0,0,0,0, new StringTextComponent(""));
        this.res = res;
        this.on = on;
        this.off = off;
        this.onSwitch = onSwitch;
        this.screen = screen;
        this.handler = handler;
    }

    protected AbstractSwitchWidget(AntimatterContainerScreen<?> screen, IGuiHandler handler, ResourceLocation res, ButtonOverlay body, ISwitchable onSwitch, boolean defaultState) {
        super(0,0,0,0, new StringTextComponent(""));
        this.res = res;
        this.body = body;
        this.onSwitch = onSwitch;
        this.state = defaultState;
        this.screen = screen;
        this.handler = handler;
    }

    protected AbstractSwitchWidget(AntimatterContainerScreen<?> screen, IGuiHandler handler, ResourceLocation res, ButtonOverlay body, String text, ISwitchable onSwitch) {
        super(0,0,0,0, new StringTextComponent(text));
        this.res = res;
        this.body = body;
        this.onSwitch = onSwitch;
        this.screen = screen;
        this.handler = handler;
    }

    protected boolean state() {
        return state;
    }

    @Override
    public void renderWidget(@Nonnull MatrixStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(res);
        RenderSystem.disableDepthTest();
        if (body == null) {
            ButtonBody body = isSwitched() ? on : off;
            int xTex = body.getX();
            int yTex = body.getY();
            if (isHovered()) {
                xTex += body.getX2();
                yTex += body.getY2();
            }
            ScreenWidget.blit(stack, screen.getGuiLeft() + x, screen.getGuiTop() + y, width, height, xTex, yTex, body.getW(), body.getH(), 256, 256);
        } else {
            int xTex = body.getX();
            int yTex = body.getY();
            float f = isSwitched() ? 1.0F : isHovered() ? 0.75F : 0.5F;
            RenderSystem.color4f(f, f, f, 1.0F);
            ScreenWidget.blit(stack, screen.getGuiLeft() + this.x, screen.getGuiTop() + this.y, width, height, xTex, yTex, body.getW(), body.getH(), 256, 256);
        }
        RenderSystem.enableDepthTest();
        String text = getMessage().getString();
        if (!text.isEmpty()) drawCenteredString(stack, minecraft.fontRenderer, text, x + width / 2, y + (height - 8) / 2, getFGColor() | MathHelper.ceil(alpha * 255.0F) << 24);
    }

    public boolean isSwitched() {
        return state;
    }

    @Override
    public void onPress() {
        this.state = !this.state;
        this.onSwitch.onSwitch(this, isSwitched());
    }

    @OnlyIn(Dist.CLIENT)
    public interface ISwitchable {
        void onSwitch(AbstractSwitchWidget button, boolean state);
    }
}
