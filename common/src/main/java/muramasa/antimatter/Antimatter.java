package muramasa.antimatter;

import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.datagen.providers.AntimatterLanguageProvider;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.item.interaction.CauldronInteractions;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.material.SubTag;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.proxy.IProxyHandler;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.RecipeBuilders;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;


public class Antimatter extends AntimatterMod {

    public static Antimatter INSTANCE;
    public static final AntimatterNetwork NETWORK = new AntimatterNetwork();
    public static final Logger LOGGER = LogManager.getLogger(Ref.ID);
    public static IProxyHandler PROXY;

    static {
        // AntimatterAPI.runBackgroundProviders();
    }

    public Antimatter() {
        super();
        LOGGER.info("Loading Antimatter");
        INSTANCE = this;


        AntimatterDynamics.clientProvider(Ref.ID,
                g -> new AntimatterBlockStateProvider(Ref.ID, Ref.NAME.concat(" BlockStates"), g));
        AntimatterDynamics.clientProvider(Ref.ID,
                g -> new AntimatterItemModelProvider(Ref.ID, Ref.NAME.concat(" Item Models"), g));
        AntimatterDynamics.clientProvider(Ref.SHARED_ID,
                g -> new AntimatterBlockStateProvider(Ref.SHARED_ID, "Antimatter Shared BlockStates", g));
        AntimatterDynamics.clientProvider(Ref.SHARED_ID,
                g -> new AntimatterItemModelProvider(Ref.SHARED_ID, "Antimatter Shared Item Models", g));
        AntimatterDynamics.clientProvider(Ref.ID,
                g -> new AntimatterLanguageProvider(Ref.ID, Ref.NAME.concat(" en_us Localization"), "en_us", g));
        AntimatterDynamics.clientProvider(Ref.SHARED_ID,
                g -> new AntimatterLanguageProvider(Ref.SHARED_ID, Ref.NAME.concat(" en_us Localization (Shared)"), "en_us", g));
        AntimatterAPI.init();

        //if (AntimatterAPI.isModLoaded(Ref.MOD_KJS))
            //new KubeJSRegistrar();
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event, Side side) {
        if (event == RegistrationEvent.DATA_INIT) {
            Recipe.init();

            SlotType.init();
            RecipeBuilders.init();
            MachineState.init();
            ICover.init();
            Data.init(side);
            SubTag.init();
            AntimatterWorldGenerator.preinit();
            GuiEvents.init();
        } else if (event == RegistrationEvent.WORLDGEN_INIT) {
            AntimatterWorldGenerator.init();
        } else if (event == RegistrationEvent.DATA_READY) {
            CauldronInteractions.init();
        }
    }

    @Override
    public String getId() {
        return Ref.ID;
    }
}
