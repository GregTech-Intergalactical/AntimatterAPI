package muramasa.antimatter;

import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public abstract class AntimatterMod implements IAntimatterRegistrar {

    public AntimatterMod() {
        AntimatterAPI.addRegistrar(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, this::onGatherData);
    }

    public void onGatherData(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();
        if (e.includeClient()) AntimatterAPI.onProviderInit("antimatter", gen, Dist.CLIENT);
        if (e.includeServer()) AntimatterAPI.onProviderInit("antimatter", gen, Dist.DEDICATED_SERVER);
    }
}
