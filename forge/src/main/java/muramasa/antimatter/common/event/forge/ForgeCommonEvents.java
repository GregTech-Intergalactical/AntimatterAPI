package muramasa.antimatter.common.event.forge;

import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.forge.energy.ForgeEnergyContainer;
import earth.terrarium.botarium.forge.fluid.ForgeFluidContainer;
import earth.terrarium.botarium.forge.fluid.ForgeFluidHandler;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.Holder;
import muramasa.antimatter.capability.forge.AntimatterCaps;
import muramasa.antimatter.common.event.CommonEvents;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.cover.CoverEnergy;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.item.IFluidItem;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.pipe.TileEntityCable;
import muramasa.antimatter.tile.pipe.TileEntityFluidPipe;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tesseract.api.forge.Provider;
import tesseract.api.forge.TesseractCaps;
import tesseract.api.forge.wrapper.ExtendedContainerWrapper;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.api.rf.IRFNode;

import java.util.Optional;

import static muramasa.antimatter.material.Material.NULL;
import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

@Mod.EventBusSubscriber(modid = Ref.ID)
public class ForgeCommonEvents {

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open ev) {
        CommonEvents.onContainerOpen(ev.getPlayer(), ev.getContainer());
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent e) {
        CommonEvents.onItemCrafted(e.getInventory(), e.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event){
        CommonEvents.placeBlock(event.getPlacedAgainst(), event.getEntity(), event.getWorld(), event.getPos(), event.getPlacedBlock());
    }

    @SubscribeEvent
    public static void onAnvilUpdated(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (CommonEvents.anvilUpdate(left, right)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLootTableLoad(LootTableLoadEvent event) {
        CommonEvents.lootTableLoad(event.getTable(), event.getName());
    }

    @SubscribeEvent
    public static void remapMissingBlocks(final RegistryEvent.MissingMappings<Block> event) {
        event.getMappings(Ref.MOD_KJS).forEach(map -> {
            String domain = map.key.getNamespace();
            String id = map.key.getPath();
            if (id.startsWith("block_")) {
                Material mat = Material.get(id.replace("block_", ""));
                if (mat != NULL) {
                    map.remap(AntimatterMaterialTypes.BLOCK.get().get(mat).asBlock());
                    return;
                }
            }
            if (id.startsWith("ore_")) {
                Block replacement = AntimatterAPI.get(BlockOre.class, id);
                if (replacement != null) {
                    map.remap(replacement);
                    return;
                }
            }
            Block replacement = AntimatterAPI.get(Block.class, id, Ref.SHARED_ID);
            if (replacement != null) {
                map.remap(replacement);
            }
        });
        event.getMappings(Ref.SHARED_ID).forEach(map -> {
            String id = map.key.getPath();
            if (id.equals("basalt")){
                map.remap(Blocks.BASALT);
                return;
            }
            String replacement = "";
            if (id.startsWith("fluid_")){
                replacement = id.replace("fluid_", "fluid_pipe_");
            } else if (id.startsWith("item_")){
                replacement = id.replace("item_", "item_pipe_");
            } else if (id.contains("vanilla_basalt")){
                replacement = id.replace("vanilla_basalt", "basalt");
            }
            if (!replacement.isEmpty()) {
                Block replacementBlock = AntimatterAPI.get(Block.class, replacement, Ref.SHARED_ID);
                if (replacementBlock != null){
                    map.remap(replacementBlock);
                }
            }
        });
    }

    @SubscribeEvent
    public static void remapMissingBlockEntities(final RegistryEvent.MissingMappings<BlockEntityType<?>> event) {
        event.getMappings(Ref.SHARED_ID).forEach(map -> {
            String id = map.key.getPath();
            String replacement = "";
            if (id.startsWith("fluid_")){
                replacement = id.replace("fluid_", "fluid_pipe_");
            } else if (id.startsWith("item_")){
                replacement = id.replace("item_", "item_pipe_");
            }
            if (!replacement.isEmpty()) {
                BlockEntityType<?> replacementBlock = AntimatterAPI.get(BlockEntityType.class, replacement, Ref.SHARED_ID);
                if (replacementBlock != null){
                    map.remap(replacementBlock);
                }
            }
        });
    }

    @SubscribeEvent
    public static void remapMissingItems(final RegistryEvent.MissingMappings<Item> event) {
        event.getMappings(Ref.ID).forEach(map -> {
            Item replacement = AntimatterAPI.get(Item.class, map.key.getPath(), Ref.SHARED_ID);
            if (replacement != null) {
                map.remap(replacement);
            }
        });

        event.getMappings(Ref.SHARED_ID).forEach(map -> {
            String id = map.key.getPath();
            if (id.equals("basalt")){
                map.remap(Items.BASALT);
                return;
            }
            if (id.contains("crushed_centrifuged")){
                Item replacement = AntimatterAPI.get(Item.class, id.replace("centrifuged", "refined"), Ref.SHARED_ID);
                if (replacement != null) {
                    map.remap(replacement);
                    return;
                }
            }
            String replacement = "";
            if (id.startsWith("fluid_")){
                replacement = id.replace("fluid_", "fluid_pipe_");
            } else if (id.startsWith("item_")){
                replacement = id.replace("item_", "item_pipe_");
            } else if (id.contains("vanilla_basalt")){
                replacement = id.replace("vanilla_basalt", "basalt");
            }
            if (!replacement.isEmpty()) {
                Item replacementBlock = AntimatterAPI.get(Item.class, replacement, Ref.SHARED_ID);
                if (replacementBlock != null){
                    map.remap(replacementBlock);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<BlockEntity> event){
        if (event.getObject() instanceof TileEntityFakeBlock fakeBlock){
            event.addCapability(new ResourceLocation(Ref.ID, "fake_block"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
                    TileEntityBasicMultiMachine<?> controller = fakeBlock.getController();
                    if (controller == null){
                        return LazyOptional.empty();
                    }
                    ICover coverPresent = fakeBlock.covers.get(side);
                    if (!controller.allowsFakeTiles()) return LazyOptional.empty();
                    if (capability == ITEM_HANDLER_CAPABILITY && controller.itemHandler.isPresent()) {
                        return fromItemHolder(controller.itemHandler, side).cast();
                    } else if (capability == FLUID_HANDLER_CAPABILITY && controller.fluidHandler.isPresent()) {
                        return fromFluidHolder(controller.fluidHandler, side).cast();
                    } else if (capability == TesseractCaps.ENERGY_HANDLER_CAPABILITY && controller.energyHandler.isPresent()
                            && (coverPresent instanceof CoverDynamo || coverPresent instanceof CoverEnergy)) {
                        return fromHolder(controller.energyHandler, side).cast();
                    } else if (capability == CapabilityEnergy.ENERGY && controller.rfHandler.isPresent()
                            && (coverPresent instanceof CoverDynamo || coverPresent instanceof CoverEnergy)) {
                        return LazyOptional.of(() -> new ForgeEnergyContainer<>(controller.rfHandler.side(side).get(), fakeBlock)).cast();
                    }
                    return LazyOptional.empty();
                }
            });
        }
        if (event.getObject() instanceof TileEntityMachine<?> machine){
            event.addCapability(new ResourceLocation(Ref.ID, "machine"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (machine instanceof TileEntityBasicMultiMachine<?> multiMachine) {
                        if (cap == AntimatterCaps.COMPONENT_HANDLER_CAPABILITY && multiMachine.componentHandler.isPresent()) {
                            return LazyOptional.of(() -> multiMachine.componentHandler.get()).cast();
                        }
                    }
                    if (machine instanceof TileEntityHatch<?> hatch) {
                        if (cap == AntimatterCaps.COMPONENT_HANDLER_CAPABILITY && hatch.componentHandler.isPresent()) {
                            return LazyOptional.of(() -> hatch.componentHandler.get()).cast();
                        }
                    }
                    if (cap == AntimatterCaps.COVERABLE_HANDLER_CAPABILITY && machine.coverHandler.isPresent()) {
                        return fromHolder(machine.coverHandler, side).cast();
                    }
                    if (side == machine.getFacing() && !machine.allowsFrontIO()) return LazyOptional.empty();
                    if (machine.blocksCapability(AntimatterCaps.CAP_MAP.inverse().get(cap), side)) return LazyOptional.empty();
                    if (cap == ITEM_HANDLER_CAPABILITY && machine.itemHandler.isPresent()) {
                        //return LazyOptional.of(() -> new InvWrapper(machine.itemHandler.side(side).resolve().get())).cast();
                        return fromItemHolder(machine.itemHandler, side).cast();
                    }
                    if (cap == AntimatterCaps.RECIPE_HANDLER_CAPABILITY && machine.recipeHandler.isPresent()) {
                        return fromHolder(machine.recipeHandler, side).cast();
                    } else if (cap == FLUID_HANDLER_CAPABILITY && machine.fluidHandler.isPresent()) {
                        //return LazyOptional.of(() -> new ForgeFluidContainer(machine.fluidHandler.side(side).resolve().get())).cast()
                        return fromFluidHolder(machine.fluidHandler, side).cast();
                    } else if (cap == TesseractCaps.ENERGY_HANDLER_CAPABILITY || cap == CapabilityEnergy.ENERGY) {
                        if (machine.energyHandler.isPresent()) {
                            return fromHolder(machine.energyHandler, side).cast();
                        } else if (cap == CapabilityEnergy.ENERGY && machine.rfHandler.isPresent()){
                            LazyOptional<T> opt = LazyOptional.of(() -> new ForgeEnergyContainer<>(machine.rfHandler.side(side).orElseThrow(), machine)).cast();
                            machine.rfHandler.addListener(side, opt::invalidate);
                            return opt;
                        }
                    }
                    return LazyOptional.empty();
                }
            });
        }
        if (event.getObject() instanceof TileEntityPipe<?> pipe){
            event.addCapability(new ResourceLocation(Ref.ID, "pipe"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
                    if (side == null) return LazyOptional.empty();
                    if (capability == AntimatterCaps.COVERABLE_HANDLER_CAPABILITY && pipe.coverHandler.isPresent()) return LazyOptional.of(() -> pipe.coverHandler.get()).cast();
                    if (!pipe.connects(side)) return LazyOptional.empty();
                    if (capability == CapabilityEnergy.ENERGY) {
                        if (pipe instanceof TileEntityCable<?>) {
                            return fromHolder(pipe.getPipeCapHolder(), side).cast();
                        }
                        if (pipe.getCapClass() == IRFNode.class){
                            return fromEnergyHolder(pipe.getPipeCapHolder(), side, pipe).cast();
                        }
                    }
                    if (capability == FLUID_HANDLER_CAPABILITY && pipe.getCapClass() == FluidContainer.class){
                        return fromFluidHolder(pipe.getPipeCapHolder(), side).cast();
                    }
                    if (capability == ITEM_HANDLER_CAPABILITY && pipe.getCapClass() == ExtendedItemContainer.class){
                        return fromItemHolder(pipe.getPipeCapHolder(), side).cast();
                    }
                    try {
                        if (capability == AntimatterCaps.CAP_MAP.get(pipe.getCapClass())){
                            return fromHolder(pipe.getPipeCapHolder(), side);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        return LazyOptional.empty();
                    }

                    return LazyOptional.empty();
                }
            });
        }
    }

    public static <T> LazyOptional<T> fromHolder(Holder<T, ?> holder, Direction side){
        if (!holder.isPresent()) return LazyOptional.empty();
        LazyOptional<T> opt = LazyOptional.of(() -> holder.side(side).get());
        holder.addListener(side, opt::invalidate);
        return opt;
    }

    private static LazyOptional<IItemHandler> fromItemHolder(Holder<ExtendedItemContainer, ?> holder, Direction side){
        if (!holder.isPresent()) return LazyOptional.empty();
        LazyOptional<IItemHandler> opt = LazyOptional.of(() -> new ExtendedContainerWrapper(holder.side(side).get()));
        holder.addListener(side, opt::invalidate);
        return opt;
    }

    private static LazyOptional<IFluidHandler> fromFluidHolder(Holder<FluidContainer, ?> holder, Direction side){
        if (!holder.isPresent()) return LazyOptional.empty();
        LazyOptional<IFluidHandler> opt = LazyOptional.of(() -> new ForgeFluidContainer(holder.side(side).get()));
        holder.addListener(side, opt::invalidate);
        return opt;
    }

    private static LazyOptional<IEnergyStorage> fromEnergyHolder(Holder<IRFNode, ?> holder, Direction side, BlockEntity block){
        if (!holder.isPresent()) return LazyOptional.empty();
        LazyOptional<IEnergyStorage> opt = LazyOptional.of(() -> new ForgeEnergyContainer<>(holder.side(side).get(), block));
        holder.addListener(side, opt::invalidate);
        return opt;
    }

    /**
     * Recipe event for local servers, builds recipes.
     * @param ev forge event callback.
     */
    @SubscribeEvent
    public static void recipeEvent(RecipesUpdatedEvent ev) {
       CommonEvents.recipeEvent(ev.getRecipeManager());
    }

    /**
     * Recipe event for online server, builds recipes.
     * @param ev forge event callback.
     */
    @SubscribeEvent
    public static void tagsEvent(TagsUpdatedEvent ev) {
        CommonEvents.tagsEvent();
    }

    @SubscribeEvent
    public static void biomeLoadEvent(BiomeLoadingEvent event){
        AntimatterWorldGenerator.reloadEvent(event.getName(),  event.getClimate(), event.getCategory(), event.getEffects(), event.getGeneration(), event.getSpawns());
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event){
        StructureCache.onWorldUnload(event.getWorld());
    }

}
