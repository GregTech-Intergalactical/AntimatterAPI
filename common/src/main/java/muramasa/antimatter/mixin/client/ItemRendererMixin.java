package muramasa.antimatter.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    protected abstract void fillRect(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    private void injectRenderGuiItemDecorations(Font fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci){
        if (stack.getItem() instanceof IAntimatterTool tool && tool.getAntimatterToolType().isPowered() && tool.isPoweredBarVisible(stack)) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            int i = tool.getPoweredBarWidth(stack);
            int j = tool.getPoweredBarColor(stack);
            this.fillRect(bufferbuilder, xPosition + 2, yPosition + 11, 13, 2, 0, 0, 0, 255);
            this.fillRect(bufferbuilder, xPosition + 2, yPosition + 11, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }

    }
}
