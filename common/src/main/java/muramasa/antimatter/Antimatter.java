package muramasa.antimatter;

import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.datagen.loaders.MaterialRecipes;
import muramasa.antimatter.datagen.loaders.Pipes;
import muramasa.antimatter.datagen.loaders.Tools;
import muramasa.antimatter.datagen.providers.*;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
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
    public static final AntimatterNetwork NETWORK = AntimatterNetwork.createAntimatterNetwork();
    public static final Logger LOGGER = LogManager.getLogger(Ref.ID);
    public static IProxyHandler PROXY;

    static {
        // AntimatterAPI.runBackgroundProviders();
    }

    public Antimatter() {
        super();
    }

    @Override
    public void onRegistrarInit() {
        super.onRegistrarInit();
        LOGGER.info("Loading Antimatter");
        INSTANCE = this;
        if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)){
            new KubeJSRegistrar();
        }
        AntimatterDynamics.clientProvider(Ref.ID,
                g -> new AntimatterBlockStateProvider(Ref.ID, Ref.NAME.concat(" BlockStates"), g));
        AntimatterDynamics.clientProvider(Ref.ID,
                g -> new AntimatterItemModelProvider(Ref.ID, Ref.NAME.concat(" Item Models")));
        AntimatterDynamics.clientProvider(Ref.SHARED_ID,
                g -> new AntimatterBlockStateProvider(Ref.SHARED_ID, "Antimatter Shared BlockStates", g));
        AntimatterDynamics.clientProvider(Ref.SHARED_ID,
                g -> new AntimatterItemModelProvider(Ref.SHARED_ID, "Antimatter Shared Item Models"));
        AntimatterDynamics.clientProvider(Ref.ID,
                g -> new AntimatterLanguageProvider(Ref.ID, Ref.NAME.concat(" en_us Localization"), "en_us"));
        AntimatterDynamics.clientProvider(Ref.SHARED_ID,
                g -> new AntimatterLanguageProvider(Ref.SHARED_ID, Ref.NAME.concat(" en_us Localization (Shared)"), "en_us"));
        AntimatterAPI.init();
    }

    public void addCraftingLoaders(CraftingEvent ev) {
        ev.addLoader(MaterialRecipes::init);
        ev.addLoader(Pipes::loadRecipes);
        ev.addLoader(Tools::init);
    }

    public void providers(ProvidersEvent ev) {
        if (ev.getSide() == Side.CLIENT) {

        } else {
            final AntimatterBlockTagProvider[] p = new AntimatterBlockTagProvider[1];
            ev.addProvider(Ref.ID, g -> {
                p[0] = new AntimatterBlockTagProvider(Ref.ID, Ref.NAME.concat(" Block Tags"), false, g);
                return p[0];
            });
            ev.addProvider(Ref.SHARED_ID, g -> new AntimatterFluidTagProvider(Ref.SHARED_ID,
                    "Antimatter Shared Fluid Tags", false, g));
            ev.addProvider(Ref.ID, g -> new AntimatterItemTagProvider(Ref.ID, Ref.NAME.concat(" Item Tags"),
                    false, g, p[0]));
            ev.addProvider(Ref.ID,
                    g -> new AntimatterBlockLootProvider(Ref.ID, Ref.NAME.concat(" Loot generator"), g));
        }
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
        } else if (event == RegistrationEvent.CLIENT_DATA_INIT && side == Side.CLIENT){
            AntimatterModelManager.init();
        }
    }

    @Override
    public String getId() {
        return Ref.ID;
    }
}
