package muramasa.antimatter.client;

import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import io.github.fabricators_of_create.porting_lib.event.client.ColorHandlersCallback;
import io.github.fabricators_of_create.porting_lib.event.client.TextureStitchCallback;
import muramasa.antimatter.proxy.ClientHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

public class AntimatterClientImpl implements ClientModInitializer {
    public static boolean leftDown;
    public static boolean rightDown;
    public static boolean middleDown;
    public static double lastDelta;
    @Override
    public void onInitializeClient() {
        ClientHandler.setup();
        TextureStitchCallback.PRE.register(AntimatterTextureStitcher::onTextureStitch);
        ColorHandlersCallback.BLOCK.register(ClientHandler::onBlockColorHandler);
        ColorHandlersCallback.ITEM.register((i, b) -> ClientHandler.onItemColorHandler(i));
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.beforeMouseClick(screen).register((screen1, mouseX, mouseY, button) -> {
                switch (button){
                    case 0 -> leftDown = true;
                    case 1 -> rightDown = true;
                    case 2 -> middleDown = true;
                }
            });
            ScreenMouseEvents.beforeMouseRelease(screen).register((screen1, mouseX, mouseY, button) -> {
                switch (button){
                    case 0 -> leftDown = false;
                    case 1 -> rightDown = false;
                    case 2 -> middleDown = false;
                }
            });
            ScreenMouseEvents.beforeMouseScroll(screen).register(((screen1, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
                lastDelta = horizontalAmount;
            }));
        });
        ClientWorldEvents.UNLOAD.register(((client, world) -> SoundHelper.worldUnload(world)));
    }
}
