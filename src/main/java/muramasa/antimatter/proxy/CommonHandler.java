package muramasa.antimatter.proxy;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class CommonHandler implements IProxyHandler {

    public CommonHandler() { }

    @SuppressWarnings("unused")
    public static void setup(FMLCommonSetupEvent e) {
        AntimatterWorldGenerator.init();
        AntimatterCaps.register();
    }

    @Override
    public World getClientWorld() {
        if (FMLEnvironment.dist.isDedicatedServer() || EffectiveSide.get().isServer()) {
            throw new IllegalStateException("cannot call on server!");
        }
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        if (FMLEnvironment.dist.isDedicatedServer() || EffectiveSide.get().isServer()) {
            throw new IllegalStateException("cannot call on server!");
        }
        return Minecraft.getInstance().player;
    }
}
