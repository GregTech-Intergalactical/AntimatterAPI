package muramasa.gtu.proxy;

import muramasa.gtu.api.util.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
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
    public void playSound(SoundType type) {
        //NOOP
    }

    @Override
    public void sendDiggingPacket(BlockPos pos) {
        //NOOP
    }

    @Override
    public String trans(String unlocalized) { //TODO server side localization?
        return new TranslationTextComponent(unlocalized).toString();
    }
}
