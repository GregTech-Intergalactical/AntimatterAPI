package muramasa.antimatter.fabric;

import earth.terrarium.botarium.fabric.energy.FabricBlockEnergyContainer;
import io.github.fabricators_of_create.porting_lib.event.common.BlockPlaceCallback;
import io.github.fabricators_of_create.porting_lib.event.common.ItemCraftedCallback;
import muramasa.antimatter.*;
import muramasa.antimatter.block.BlockFakeTile;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.fabric.AntimatterLookups;
import muramasa.antimatter.common.event.CommonEvents;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.fabric.CraftingEvents;
import muramasa.antimatter.event.fabric.ProviderEvents;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.fluid.fabric.FluidAttributesVariantWrapper;
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
import muramasa.antimatter.material.*;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.recipe.fabric.RecipeConditions;
import muramasa.antimatter.registration.IAntimatterRegistrarInitializer;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.fabric.AntimatterRegistration;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.worldgen.fabric.AntimatterFabricWorldgen;
import net.devtech.arrp.api.RRPCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.items.IItemHandler;
import team.reborn.energy.api.EnergyStorage;
import tesseract.api.fabric.TesseractLookups;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.rf.IRFNode;
import tesseract.fabric.TesseractImpl;

import java.util.Map;

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
            FluidStorage.SIDED.registerForBlockEntity((be, direction) -> getCap(IFluidHandler.class, be, direction), BlockFakeTile.TYPE);
            ItemStorage.SIDED.registerForBlockEntity((be, direction) -> getCap(IItemHandler.class, be, direction), BlockFakeTile.TYPE);
            TesseractLookups.ENERGY_HANDLER_SIDED.registerForBlockEntity((be, direction) -> getCap(IEnergyHandler.class, be, direction), BlockFakeTile.TYPE);
            EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> {
                IEnergyHandler handler = getCap(IEnergyHandler.class, be, direction);
                if (handler != null) return (EnergyStorage) handler;
                IRFNode node = getCap(IRFNode.class, be, direction);
                if (node != null) return node instanceof EnergyStorage storage ? storage : new FabricBlockEnergyContainer(node, node, be);
                return null;
            }, BlockFakeTile.TYPE);
            if (AntimatterAPI.isModLoaded("modern_industrialization")) {
                TesseractImpl.registerMITile((be, direction) -> getCap(IEnergyHandler.class, be, direction), BlockFakeTile.TYPE);
            }
            AntimatterLookups.COVER_HANDLER_SIDED.registerForBlockEntity((be, direction) -> getCap(ICoverHandler.class, be, direction), BlockFakeTile.TYPE);
            AntimatterAPI.all(Material.class).forEach(m -> {
                Map<MaterialType<?>, Integer> map = MaterialTags.FURNACE_FUELS.getMap(m);
                if (map != null){
                    map.forEach((t, i) -> {
                        if (t instanceof MaterialTypeItem<?> typeItem){
                            FuelRegistry.INSTANCE.add(typeItem.get(m), i);
                        } else if (t instanceof MaterialTypeBlock<?> typeBlock && typeBlock.get() instanceof MaterialTypeBlock.IBlockGetter blockGetter){
                            FuelRegistry.INSTANCE.add(blockGetter.get(m).asItem(), i);
                        }
                    });
                }

            });
            CraftingEvents.CRAFTING.register(Antimatter.INSTANCE::addCraftingLoaders);
            ProviderEvents.PROVIDERS.register(this::providers);
            ServerWorldEvents.UNLOAD.register((server, world) -> StructureCache.onWorldUnload(world));
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
            RRPCallback.BEFORE_USER.register(resources -> AntimatterDynamics.addDataPacks(resources::add));
            Antimatter.LOGGER.info("initializing");
            ServerLifecycleEvents.SERVER_STARTING.register(server -> Antimatter.LOGGER.info("server starting"));
            AntimatterDynamics.setInitialized();
            AntimatterAPI.all(AntimatterFluid.class, f -> {
                Fluid source = f.getFluid();
                Fluid flowing = f.getFlowingFluid();
                FluidVariantAttributes.register(source, new FluidAttributesVariantWrapper(f.getAttributes()));
                FluidVariantAttributes.register(flowing, new FluidAttributesVariantWrapper(f.getAttributes()));
            });
        }
    }

    private <T> T getCap(Class<T> clazz, TileEntityFakeBlock fakeBlock, Direction side){
        if (fakeBlock.controllerPos != null) {
            if (fakeBlock.getLevel().getBlockEntity(fakeBlock.controllerPos) instanceof TileEntityBasicMultiMachine<?> basicMultiMachine && basicMultiMachine.allowsFakeTiles()){
                fakeBlock.setController(basicMultiMachine);
            }
            fakeBlock.controllerPos = null;
        }
        if (fakeBlock.controller == null){
            return null;
        }
        LazyOptional<T> opt = fakeBlock.controller.getCapabilityFromFake(clazz, fakeBlock.getBlockPos(), side, fakeBlock.covers.get(side));
        if (opt.isPresent()) {
            return opt.orElse(null);
        }
        return null;
    }

    private void providers(ProvidersEvent ev) {
        Antimatter.INSTANCE.providers(ev);
        KubeJSRegistrar.providerEvent(ev);
    }
}
