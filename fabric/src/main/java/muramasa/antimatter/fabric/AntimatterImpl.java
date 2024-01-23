package muramasa.antimatter.fabric;

import earth.terrarium.botarium.fabric.energy.FabricBlockEnergyContainer;
import earth.terrarium.botarium.fabric.fluid.storage.FabricBlockFluidContainer;
import io.github.fabricators_of_create.porting_lib.event.common.BlockPlaceCallback;
import io.github.fabricators_of_create.porting_lib.event.common.ItemCraftedCallback;
import io.github.fabricators_of_create.porting_lib.event.common.PlayerTickEvents;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockFakeTile;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.common.event.CommonEvents;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.cover.CoverEnergy;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.fabric.CraftingEvents;
import muramasa.antimatter.event.fabric.ProviderEvents;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.fluid.fabric.FluidAttributesVariantWrapper;
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
import muramasa.antimatter.pipe.TileTicker;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.recipe.fabric.RecipeConditions;
import muramasa.antimatter.registration.IAntimatterRegistrarInitializer;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.fabric.AntimatterRegistration;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.worldgen.fabric.AntimatterFabricWorldgen;
import net.devtech.arrp.api.RRPCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import team.reborn.energy.api.EnergyStorage;
import tesseract.api.fabric.TesseractLookups;
import tesseract.api.fabric.wrapper.ExtendedContainerWrapper;
import tesseract.fabric.TesseractImpl;

public class AntimatterImpl implements ModInitializer {
    @Override
    public void onInitialize() {
        initialize(!AntimatterAPI.isModLoaded(Ref.MOD_KJS));
    }

    public void initialize(boolean run){
        if (run){
            FabricLoader.getInstance().getEntrypointContainers("antimatter", IAntimatterRegistrarInitializer.class).forEach(r -> r.getEntrypoint().onRegistrarInit());
            AntimatterRegistration.onRegister();
            AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
            CommonHandler.setup();
            AntimatterFabricWorldgen.init();
            RecipeConditions.init();
            if (BlockFakeTile.TYPE != null){
                registerFakeTileLookups();
            }
            CraftingEvents.CRAFTING.register(Antimatter.INSTANCE::addCraftingLoaders);
            ProviderEvents.PROVIDERS.register(this::providers);
            ServerWorldEvents.UNLOAD.register((server, world) -> StructureCache.onWorldUnload(world));
            ItemCraftedCallback.EVENT.register(((player, crafted, container) -> CommonEvents.onItemCrafted(container, player)));
            ServerTickEvents.START_SERVER_TICK.register(server1 -> TileTicker.onServerWorldTick(server1, true));
            ServerTickEvents.END_SERVER_TICK.register(server1 -> TileTicker.onServerWorldTick(server1, false));
            CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> CommonEvents.tagsEvent());
            //TODO figure out variables to insert
            BlockPlaceCallback.EVENT.register(context -> {
                BlockPos placedOffPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
                BlockState placedOff = context.getLevel().getBlockState(placedOffPos);
                CommonEvents.placeBlock(placedOff, context.getPlayer(), context.getLevel(), context.getClickedPos(), context.getLevel().getBlockState(context.getClickedPos()));
                return InteractionResult.PASS;
            });
            PlayerTickEvents.START.register(player -> CommonEvents.getPLAYER_TICK_CALLBACKS().forEach(c -> c.onTick(false, player instanceof ServerPlayer, player)));
            PlayerTickEvents.END.register(player -> CommonEvents.getPLAYER_TICK_CALLBACKS().forEach(c -> c.onTick(true, player instanceof ServerPlayer, player)));
            RRPCallback.AFTER_VANILLA.register(resources -> AntimatterDynamics.addResourcePacks(resources::add));
            RRPCallback.BEFORE_USER.register(resources -> AntimatterDynamics.addDataPacks(resources::add));
            Antimatter.LOGGER.info("initializing");
            ServerLifecycleEvents.SERVER_STARTING.register(server -> Antimatter.LOGGER.info("server starting"));
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
                AntimatterDynamics.setInitialized();
            }
            AntimatterAPI.all(AntimatterFluid.class, f -> {
                Fluid source = f.getFluid();
                Fluid flowing = f.getFlowingFluid();
                FluidVariantAttributes.register(source, new FluidAttributesVariantWrapper(f.getAttributes()));
                FluidVariantAttributes.register(flowing, new FluidAttributesVariantWrapper(f.getAttributes()));
            });
        }
    }

    private void registerFakeTileLookups(){
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> {
            BlockEntityBasicMultiMachine<?> controller = be.getController();
            if (controller == null){
                return null;
            }
            if (!controller.allowsFakeTiles()) return null;
            return controller.fluidHandler.side(direction).map(f -> new FabricBlockFluidContainer(f, b -> {}, controller)).orElse(null);
        }, BlockFakeTile.TYPE);
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> {
            BlockEntityBasicMultiMachine<?> controller = be.getController();
            if (controller == null){
                return null;
            }
            if (!controller.allowsFakeTiles()) return null;
            return controller.itemHandler.side(direction).map(ExtendedContainerWrapper::new).orElse(null);
        }, BlockFakeTile.TYPE);
        TesseractLookups.ENERGY_HANDLER_SIDED.registerForBlockEntity((be, direction) -> {
            BlockEntityBasicMultiMachine<?> controller = be.getController();
            if (controller == null){
                return null;
            }
            if (!controller.allowsFakeTiles()) return null;
            ICover coverPresent = be.getCover(direction);
            if (!(coverPresent instanceof CoverDynamo || coverPresent instanceof CoverEnergy)) return null;
            return controller.energyHandler.side(direction).orElse(null);
        }, BlockFakeTile.TYPE);
        EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> {
            BlockEntityBasicMultiMachine<?> controller = be.getController();
            if (controller == null){
                return null;
            }
            if (!controller.allowsFakeTiles()) return null;
            ICover coverPresent = be.getCover(direction);
            if (!(coverPresent instanceof CoverDynamo || coverPresent instanceof CoverEnergy)) return null;
            return controller.rfHandler.side(direction).map(rf -> {
                return rf instanceof EnergyStorage storage ? storage : new FabricBlockEnergyContainer(rf, rf, be);
            }).orElse(null);
        }, BlockFakeTile.TYPE);
        if (AntimatterAPI.isModLoaded("modern_industrialization")) {
            TesseractImpl.registerMITile((be, direction) -> {
                BlockEntityBasicMultiMachine<?> controller = be.getController();
                if (controller == null){
                    return null;
                }
                if (!controller.allowsFakeTiles()) return null;
                ICover coverPresent = be.getCover(direction);
                if (!(coverPresent instanceof CoverDynamo || coverPresent instanceof CoverEnergy)) return null;
                return controller.energyHandler.side(direction).orElse(null);
            }, BlockFakeTile.TYPE);
        }
    }

    private void providers(ProvidersEvent ev) {
        Antimatter.INSTANCE.providers(ev);
        KubeJSRegistrar.providerEvent(ev);
    }
}
