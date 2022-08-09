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
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
import muramasa.antimatter.recipe.fabric.RecipeConditions;
import muramasa.antimatter.registration.IAntimatterRegistrarInitializer;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.registration.fabric.AntimatterRegistration;
import muramasa.antimatter.structure.StructureCache;
import net.devtech.arrp.api.RRPCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class AntimatterImpl implements ModInitializer {
    @Override
    public void onInitialize() {
        AntimatterAPI.setSIDE(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Side.CLIENT : Side.SERVER);
        EntrypointUtils.invoke("antimatter", IAntimatterRegistrarInitializer.class, IAntimatterRegistrarInitializer::onRegistrarInit);
        if (!AntimatterAPI.isModLoaded(Ref.MOD_KJS)){
            AntimatterRegistration.onRegister();
            AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        }
        RecipeConditions.init();
        CraftingEvents.CRAFTING.register(Antimatter.INSTANCE::addCraftingLoaders);
        ProviderEvents.PROVIDERS.register(this::providers);
        ModConfigEvent.LOADING.register(AntimatterConfig::onModConfigEvent);
        ModConfigEvent.RELOADING.register(AntimatterConfig::onModConfigEvent);
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
        RRPCallback.AFTER_VANILLA.register(resources -> resources.add(AntimatterDynamics.DYNAMIC_RESOURCE_PACK));
        Antimatter.LOGGER.info("initializing");
        ServerLifecycleEvents.SERVER_STARTING.register(server -> Antimatter.LOGGER.info("server starting"));
    }

    private void providers(ProvidersEvent ev) {
        Antimatter.INSTANCE.providers(ev);
        KubeJSRegistrar.providerEvent(ev);
    }
}
