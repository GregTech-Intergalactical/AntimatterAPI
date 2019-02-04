package muramasa.gregtech.api.gui.slot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class SlotRenderFluid extends SlotRender {

    private TextureAtlasSprite fluidTexture;

    public SlotRenderFluid(FluidStack fluidStack, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        if (fluidStack != null && fluidStack.getFluid() != null) {
            fluidTexture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluidStack.getFluid().getStill().toString());
        }
    }

    @Nullable
    @Override
    public TextureAtlasSprite getBackgroundSprite() {
        return fluidTexture; //null is handled by super
    }
}
