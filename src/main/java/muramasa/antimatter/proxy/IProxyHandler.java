package muramasa.antimatter.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxyHandler {

    World getClientWorld();

    PlayerEntity getClientPlayer();
}
