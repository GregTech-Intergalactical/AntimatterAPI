package muramasa.antimatter.proxy;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CommonHandler implements IProxyHandler {

    public CommonHandler() {
    }

    @SuppressWarnings("unused")
    public static void setup() {
        AntimatterConfiguredFeatures.init();
        AntimatterAPI.all(StoneType.class, StoneType::initSuppliedState);
        AntimatterWorldGenerator.setup();
    }

    @Override
    public Level getClientWorld() {
        return null;
    }

    @Override
    public Player getClientPlayer() {
        return null;
    }
}
