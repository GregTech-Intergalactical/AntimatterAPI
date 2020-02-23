package muramasa.antimatter.proxy;

import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IProxyHandler {

    World getClientWorld();

    PlayerEntity getClientPlayer();

    ModelBakery getModelBakery();

    void sendDiggingPacket(BlockPos pos);
}
