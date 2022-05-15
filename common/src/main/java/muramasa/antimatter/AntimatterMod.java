package muramasa.antimatter;

import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.data.DataGenerator;
;

public abstract class AntimatterMod implements IAntimatterRegistrar {

    public AntimatterMod() {
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, this::onGatherData);
        onInitialize();
    }

    @Override
    public void onInitialize() {
        AntimatterAPI.addRegistrar(this);
    }

    /*public void onGatherData(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();
        if (e.includeClient()) AntimatterDynamics.onProviderInit(e.getModContainer().getNamespace(), gen, Dist.CLIENT);
        if (e.includeServer())
            AntimatterDynamics.onProviderInit(e.getModContainer().getNamespace(), gen, Dist.DEDICATED_SERVER);
    }*/
}
