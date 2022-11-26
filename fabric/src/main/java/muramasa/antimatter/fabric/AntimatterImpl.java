package muramasa.antimatter.fabric;

import io.github.fabricators_of_create.porting_lib.event.common.BlockPlaceCallback;
import io.github.fabricators_of_create.porting_lib.event.common.ItemCraftedCallback;
import io.github.fabricators_of_create.porting_lib.event.common.RecipesUpdatedCallback;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.fabric.AntimatterCapsImpl;
import muramasa.antimatter.common.event.CommonEvents;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.fabric.CraftingEvents;
import muramasa.antimatter.event.fabric.ProviderEvents;
import muramasa.antimatter.integration.Integrations;
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
import muramasa.antimatter.mixin.BiomeAccessor;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.recipe.fabric.RecipeConditions;
import muramasa.antimatter.registration.IAntimatterRegistrarInitializer;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.registration.fabric.AntimatterRegistration;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.fabric.AntimatterFabricWorldgen;
import net.devtech.arrp.api.RRPCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import static muramasa.antimatter.Ref.ID;

public class AntimatterImpl implements ModInitializer {
    @Override
    public void onInitialize() {
        initialize(!AntimatterAPI.isModLoaded(Ref.MOD_KJS));
    }

    public void initialize(boolean run){
        if (run){
            ModConfigEvent.LOADING.register(AntimatterConfig::onModConfigEvent);
            ModConfigEvent.RELOADING.register(AntimatterConfig::onModConfigEvent);
            ModLoadingContext.registerConfig(ID, ModConfig.Type.COMMON, AntimatterConfig.COMMON_SPEC);
            ModLoadingContext.registerConfig(ID, ModConfig.Type.CLIENT, AntimatterConfig.CLIENT_SPEC);
            EntrypointUtils.invoke("antimatter", IAntimatterRegistrarInitializer.class, IAntimatterRegistrarInitializer::onRegistrarInit);
            AntimatterRegistration.onRegister();
            AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
            CommonHandler.setup();
            AntimatterFabricWorldgen.init();
            RecipeConditions.init();
            CraftingEvents.CRAFTING.register(Antimatter.INSTANCE::addCraftingLoaders);
            ProviderEvents.PROVIDERS.register(this::providers);
            ServerWorldEvents.UNLOAD.register((server, world) -> StructureCache.onWorldUnload(world));
            RegisterCapabilitiesEvent.REGISTER_CAPS.register(AntimatterCapsImpl::register);
            ItemCraftedCallback.EVENT.register(((player, crafted, container) -> CommonEvents.onItemCrafted(container, player)));
            CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> CommonEvents.tagsEvent());
            //TODO figure out variables to insert
            BlockPlaceCallback.EVENT.register(context -> {
                BlockPos placedOffPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
                BlockState placedOff = context.getLevel().getBlockState(placedOffPos);
                CommonEvents.placeBlock(placedOff, context.getPlayer(), context.getLevel(), context.getClickedPos(), context.getLevel().getBlockState(context.getClickedPos()));
                return InteractionResult.PASS;
            });
            RRPCallback.AFTER_VANILLA.register(resources -> AntimatterDynamics.addResourcePacks(resources::add));
            Antimatter.LOGGER.info("initializing");
            ServerLifecycleEvents.SERVER_STARTING.register(server -> Antimatter.LOGGER.info("server starting"));
            Integrations.setupIntegrations();
        }
    }

    private void providers(ProvidersEvent ev) {
        Antimatter.INSTANCE.providers(ev);
        KubeJSRegistrar.providerEvent(ev);
    }
}
