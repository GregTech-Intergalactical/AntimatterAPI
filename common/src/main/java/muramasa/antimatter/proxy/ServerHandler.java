package muramasa.antimatter.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ServerHandler implements IProxyHandler {

    public ServerHandler() {
    }

    @SuppressWarnings("unused")
    public static void setup() {

    }

    @Override
    public Level getClientWorld() {
        throw new IllegalStateException("Cannot call on server!");
    }

    @Override
    public Player getClientPlayer() {
        throw new IllegalStateException("Cannot call on server!");
    }

}
