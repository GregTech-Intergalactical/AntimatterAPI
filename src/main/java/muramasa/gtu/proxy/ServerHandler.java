package muramasa.gtu.proxy;

import muramasa.antimatter.util.SoundType;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ServerHandler implements IProxyHandler {

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("cannot call on server!");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("cannot call on server!");
    }

    @Override
    public ModelBakery getModelBakery() {
        throw new IllegalStateException("cannot call on server!");
    }

    @Override
    public void playSound(SoundType type) {
        //NOOP
    }

    @Override
    public void sendDiggingPacket(BlockPos pos) {
        //NOOP
    }
}
