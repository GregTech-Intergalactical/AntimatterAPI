package muramasa.antimatter;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.Side;
import net.minecraft.data.DataGenerator;
import tesseract.TesseractPlatformUtils;

;
import java.util.Map;

public abstract class AntimatterMod implements IAntimatterRegistrar {
    @FunctionalInterface
    public interface DataEvent{
        void onGatherData(DataGenerator gen, String namespace, boolean includeClient, boolean includeServer);
    }

    //TODO data gen event in fabricated forge api
    private static final Map<String, DataEvent> DATA_EVENTS = new Object2ObjectOpenHashMap<>();

    public AntimatterMod() {
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, this::onGatherData);
        if (TesseractPlatformUtils.isForge()){
            onRegistrarInit();
        }
    }

    @Override
    public void onRegistrarInit() {
        DATA_EVENTS.put(this.getDomain(), this::onGatherData);
        AntimatterAPI.addRegistrar(this);
    }

    public void onGatherData(DataGenerator gen, String namespace, boolean includeClient, boolean includeServer){
        if (includeClient) AntimatterDynamics.onProviderInit(namespace, gen, Side.CLIENT);
        if (includeServer)
            AntimatterDynamics.onProviderInit(namespace, gen, Side.SERVER);
    }

    public static void onGatherData(DataGenerator gen, boolean includeClient, boolean includeServer){
        DATA_EVENTS.forEach((s, d) -> {
            d.onGatherData(gen, s, includeClient, includeServer);
        });
    }

    /*public void onGatherData(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();
        if (e.includeClient()) AntimatterDynamics.onProviderInit(e.getModContainer().getNamespace(), gen, Dist.CLIENT);
        if (e.includeServer())
            AntimatterDynamics.onProviderInit(e.getModContainer().getNamespace(), gen, Dist.DEDICATED_SERVER);
    }*/
}
