package muramasa.antimatter.client.fabric;

import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import io.github.fabricators_of_create.porting_lib.event.client.ColorHandlersCallback;
import io.github.fabricators_of_create.porting_lib.event.client.TextureStitchCallback;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.client.SoundHelper;
import muramasa.antimatter.client.event.ClientEvents;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.proxy.ClientHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;

public class AntimatterClientImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientHandler.setup();
        TextureStitchCallback.PRE.register(AntimatterTextureStitcher::onTextureStitch);
        ColorHandlersCallback.BLOCK.register(ClientHandler::onBlockColorHandler);
        ColorHandlersCallback.ITEM.register((i, b) -> ClientHandler.onItemColorHandler(i));
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.beforeMouseClick(screen).register((screen1, mouseX, mouseY, button) -> ClientEvents.onGuiMouseClickPre(button));
            ScreenMouseEvents.beforeMouseRelease(screen).register((screen1, mouseX, mouseY, button) -> ClientEvents.onGuiMouseClickPre(button));
            ScreenMouseEvents.beforeMouseScroll(screen).register(((screen1, mouseX, mouseY, horizontalAmount, verticalAmount) -> ClientEvents.onGuiMouseScrollPre(horizontalAmount)));
        });
        ClientWorldEvents.UNLOAD.register(((client, world) -> SoundHelper.worldUnload(world)));
        ClientHandler.preResourceRegistration();
        ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
            MaterialType.addTooltip(stack, lines, Minecraft.getInstance().player, context);
            //TODO is this needed?
            ClientEvents.onItemTooltip(context, lines);
        }));
        //TODO fix this
        //WorldRenderEvents.BLOCK_OUTLINE.register(((worldRenderContext, blockOutlineContext) -> ClientEvents.onBlockHighlight(worldRenderContext.worldRenderer(), worldRenderContext.camera(), worldRenderContext, worldRenderContext, worldRenderContext.matrixStack(), worldRenderContext.consumers())));
        //TODO figure this out
        //WorldRenderEvents.BEFORE_DEBUG_RENDER.register((context -> ClientEvents.onRenderDebugInfo(context.)));
    }
}
