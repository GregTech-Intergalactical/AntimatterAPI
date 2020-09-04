package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class ButtonWidget extends Button {
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int xDiffText;
    private final int yDiffText;

    public ButtonWidget(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int xDiffTextIn, int yDiffTextIn, ResourceLocation resourceLocationIn, Button.IPressable onPressIn) {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, xDiffTextIn, yDiffTextIn, resourceLocationIn, onPressIn, "");
    }

    public ButtonWidget(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int xDiffTextIn, int yDiffTextIn, ResourceLocation resourceLocationIn, Button.IPressable onPressIn, String textIn) {
        super(xIn, yIn, widthIn, heightIn, textIn, onPressIn);
        this.xTexStart = xTexStartIn;
        this.yTexStart = yTexStartIn;
        this.xDiffText = xDiffTextIn;
        this.yDiffText = yDiffTextIn;
        this.resourceLocation = resourceLocationIn;
    }

    public void setPosition(int xIn, int yIn) {
        this.x = xIn;
        this.y = yIn;
    }

    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int x = this.xTexStart;
        int y = this.yTexStart;
        if (this.isHovered()) {
            x += this.xDiffText;
            y += this.yDiffText;
        }
        blit(this.x, this.y, (float)x, (float)y, this.width, this.height, 256, 256);
        RenderSystem.enableDepthTest();
    }
}