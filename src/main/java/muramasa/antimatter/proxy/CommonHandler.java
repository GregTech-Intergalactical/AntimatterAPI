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
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, CommonHandler::reload);
    }

    public static void reload(AddReloadListenerEvent ev) {
        ev.addListener(new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(ResourceManager p_10796_, ProfilerFiller p_10797_) {
                AntimatterDynamics.onResourceReload(true);
                return null;
            }


            @Override
            protected void apply(Void objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
              
            }
        });
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
