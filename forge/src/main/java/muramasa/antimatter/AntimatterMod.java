package muramasa.antimatter;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public abstract class AntimatterMod implements IAntimatterRegistrar {

    public AntimatterMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, this::onGatherData);
        AntimatterAPI.addRegistrar(this);
    }

    public void onGatherData(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();
        if (e.includeClient()) AntimatterDynamics.onProviderInit(e.getModContainer().getNamespace(), gen, Dist.CLIENT);
        if (e.includeServer())
            AntimatterDynamics.onProviderInit(e.getModContainer().getNamespace(), gen, Dist.DEDICATED_SERVER);
    }
}
