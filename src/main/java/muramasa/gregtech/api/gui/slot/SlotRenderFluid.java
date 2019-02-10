package muramasa.gregtech.api.gui.slot;

import muramasa.gregtech.client.render.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class SlotRenderFluid extends SlotRender {

    private TextureAtlasSprite fluidTexture;

    public SlotRenderFluid(IItemHandler itemHandler, int index, int x, int y) {
        super(itemHandler, index, x, y);
    }

    @Nullable
    @Override
    public TextureAtlasSprite getBackgroundSprite() {
        return fluidTexture; //null is handled by super
    }

    @Nullable
    @Override
    public String getSlotTexture() {
        return super.getSlotTexture();
    }

    public void setFluid(Fluid fluid) {
        fluidTexture = fluid != null ? RenderHelper.getSprite(fluid) : null;
    }
}
