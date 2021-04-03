package muramasa.antimatter.proxy;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonHandler implements IProxyHandler {

    public CommonHandler() { }

    @SuppressWarnings("unused")
    public static void setup(FMLCommonSetupEvent e) {
        AntimatterCaps.register();
        AntimatterWorldGenerator.setup();
        MinecraftForge.EVENT_BUS.addListener(CommonHandler::resourceReload);
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void resourceReload(AddReloadListenerEvent event) {
        event.addListener(new ReloadListener<Void>() {
            @Override
            protected Void prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
                return null;
            }
            //The reason for applying the event here and not at the end of recipe manager
            //is that it will run after both KubeJS and CraftTweaker. KubeJS runs its recipe system
            //at the end of RecipeManager.apply but CraftTweaker applies it as a resourcereload with priority
            //low. Hence, lowest! To ensure proxies are loaded fine.
            @Override
            protected void apply(Void objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
                AntimatterAPI.all(RecipeMap.class, rm -> rm.compile(event.getDataPackRegistries().getRecipeManager()));
            }
        });

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
