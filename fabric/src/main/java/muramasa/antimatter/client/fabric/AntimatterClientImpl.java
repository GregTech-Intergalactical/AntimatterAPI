package muramasa.antimatter.client.fabric;

import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import io.github.fabricators_of_create.porting_lib.event.client.ColorHandlersCallback;
import io.github.fabricators_of_create.porting_lib.event.client.ModelLoadCallback;
import io.github.fabricators_of_create.porting_lib.event.client.TextureStitchCallback;
import io.github.fabricators_of_create.porting_lib.event.common.RecipesUpdatedCallback;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.common.event.CommonEvents;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.client.model.loader.AntimatterModelLoader;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.client.SoundHelper;
import muramasa.antimatter.client.event.ClientEvents;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.mixin.fabric.client.MinecraftAccessor;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.registration.RegistrationEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import static muramasa.antimatter.Antimatter.LOGGER;

public class AntimatterClientImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientHandler.setup();
        AntimatterDynamics.runDataProvidersDynamically();
        TextureStitchCallback.PRE.register(AntimatterTextureStitcher::onTextureStitch);
        ColorHandlersCallback.BLOCK.register(ClientHandler::onBlockColorHandler);
        ColorHandlersCallback.ITEM.register((i, b) -> ClientHandler.onItemColorHandler(i));
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.beforeMouseClick(screen).register((screen1, mouseX, mouseY, button) -> ClientEvents.onGuiMouseClickPre(button));
            ScreenMouseEvents.beforeMouseRelease(screen).register((screen1, mouseX, mouseY, button) -> ClientEvents.onGuiMouseClickPre(button));
            ScreenMouseEvents.beforeMouseScroll(screen).register(((screen1, mouseX, mouseY, horizontalAmount, verticalAmount) -> ClientEvents.onGuiMouseScrollPre(horizontalAmount)));
        });
        ClientWorldEvents.UNLOAD.register(((client, world) -> SoundHelper.worldUnload(world)));
        AntimatterAPI.onRegistration(RegistrationEvent.CLIENT_DATA_INIT);
        AntimatterDynamics.runAssetProvidersDynamically();
        ModelLoadCallback.EVENT.register(((manager, colors, profiler, mipLevel) -> {
            AntimatterAPI.all(AntimatterModelLoader.class).forEach(l -> ClientHandler.registerLoader(l.getLoc(), l));
        }));

        ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
            MaterialType.addTooltip(stack, lines, Minecraft.getInstance().player, context);
            //TODO is this needed?
            ClientEvents.onItemTooltip(context, lines);
        }));
        RecipesUpdatedCallback.EVENT.register((CommonEvents::recipeEvent));
        //TODO fix this
        WorldRenderEvents.BLOCK_OUTLINE.register(((worldRenderContext, blockOutlineContext) -> {
            Minecraft mc = Minecraft.getInstance();
            HitResult result = mc.hitResult;
            float partialTick = mc.isPaused() ? ((MinecraftAccessor)mc).getPausePartialTick() : ((MinecraftAccessor)mc).getTimer().partialTick;
            if (result instanceof BlockHitResult blockHitResult){
                return !ClientEvents.onBlockHighlight(worldRenderContext.worldRenderer(), worldRenderContext.camera(),  blockHitResult, partialTick, worldRenderContext.matrixStack(), worldRenderContext.consumers());
            }
            return true;
        }));
        //TODO figure this out
        //WorldRenderEvents.BEFORE_DEBUG_RENDER.register((context -> ClientEvents.onRenderDebugInfo(context.)));
        AntimatterAPI.getCommonDeferredQueue().ifPresent(t -> {
            for (Runnable r : t) {
                try {
                    r.run();
                } catch (Exception ex) {
                    LOGGER.warn("Caught error during common setup: " + ex.getMessage());
                }
            }
        });
        AntimatterAPI.getClientDeferredQueue().ifPresent(t -> {
            for (Runnable r : t) {
                try {
                    r.run();
                } catch (Exception ex) {
                    LOGGER.warn("Caught error during client setup: " + ex.getMessage());
                }
            }
        });
    }
}
