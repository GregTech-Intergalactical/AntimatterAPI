package muramasa.gtu.proxy;

import muramasa.gtu.api.util.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IProxyHandler {

    World getClientWorld();

    PlayerEntity getClientPlayer();

    void playSound(SoundType type);

    void sendDiggingPacket(BlockPos pos);

    String trans(String unlocalized);
}
