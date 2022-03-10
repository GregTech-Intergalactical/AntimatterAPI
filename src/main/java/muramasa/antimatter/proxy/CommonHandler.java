package muramasa.antimatter.proxy;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonHandler implements IProxyHandler {

    public CommonHandler() {
    }

    @SuppressWarnings("unused")
    public static void setup(FMLCommonSetupEvent e) {
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
