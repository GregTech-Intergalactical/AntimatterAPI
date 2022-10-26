package muramasa.antimatter.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IProxyHandler {

    Level getClientWorld();

    Player getClientPlayer();
}
