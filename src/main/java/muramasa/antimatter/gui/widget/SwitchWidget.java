package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.gui.ButtonBody;
import muramasa.antimatter.gui.ButtonOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SwitchWidget extends AbstractButton {

    private ResourceLocation res;
    private ButtonBody on, off;
    private ButtonOverlay body;
    private boolean state;

    protected final SwitchWidget.ISwitchable onSwitch;

    public SwitchWidget(ResourceLocation res, int x, int y, int w, int h, ButtonBody on, ButtonBody off, ISwitchable onSwitch) {
        super(x, y, w, h, new StringTextComponent(""));
        this.res = res;
        this.on = on;
        this.off = off;
        this.onSwitch = onSwitch;
    }

    public SwitchWidget(ResourceLocation res, int x, int y, int w, int h, ButtonOverlay body, ISwitchable onSwitch, boolean defaultState) {
        super(x, y, w, h, new StringTextComponent(""));
        this.res = res;
        this.body = body;
        this.onSwitch = onSwitch;
        this.state = defaultState;
    }

    public SwitchWidget(ResourceLocation res, int x, int y, int w, int h, ButtonOverlay body, String text, ISwitchable onSwitch) {
        super(x, y, w, h, new StringTextComponent(text));
        this.res = res;
        this.body = body;
        this.onSwitch = onSwitch;
    }

    @Override
    public void renderButton(MatrixStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
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
            ScreenWidget.blit(stack, x, y, width, height, xTex, yTex, body.getW(), body.getH(), 256, 256);
        } else {
            int xTex = body.getX();
            int yTex = body.getY();
            float f = isSwitched() ? 1.0F : isHovered() ? 0.75F : 0.5F;
            RenderSystem.color4f(f, f, f, 1.0F);
            ScreenWidget.blit(stack, x, y, width, height, xTex, yTex, body.getW(), body.getH(), 256, 256);
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
        void onSwitch(SwitchWidget button, boolean state);
    }
}
