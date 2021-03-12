package muramasa.antimatter;

import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public abstract class AntimatterMod implements IAntimatterRegistrar {

    public AntimatterMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, this::onGatherData);
        AntimatterAPI.addRegistrar(this);
    }

    public void onGatherData(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();
        if (e.includeClient()) AntimatterAPI.onProviderInit(e.getModContainer().getNamespace(), gen, Dist.CLIENT);
        if (e.includeServer()) AntimatterAPI.onProviderInit(e.getModContainer().getNamespace(), gen, Dist.DEDICATED_SERVER);
    }
}
