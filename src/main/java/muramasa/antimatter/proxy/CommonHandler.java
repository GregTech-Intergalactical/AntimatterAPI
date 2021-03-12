package muramasa.antimatter.proxy;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonHandler implements IProxyHandler {

    public CommonHandler() { }

    @SuppressWarnings("unused")
    public static void setup(FMLCommonSetupEvent e) {
        AntimatterCaps.register();
        AntimatterWorldGenerator.setup();
    }

    @Override
    public World getClientWorld() {
        return null;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return null;
    }
}
