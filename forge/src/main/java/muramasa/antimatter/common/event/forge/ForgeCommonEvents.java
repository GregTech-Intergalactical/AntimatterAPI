package muramasa.antimatter.common.event.forge;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.forge.AntimatterCaps;
import muramasa.antimatter.common.event.CommonEvents;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.pipe.TileEntityCable;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tesseract.api.forge.TesseractCaps;

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
        for (RegistryEvent.MissingMappings.Mapping<Block> map : event.getMappings(Ref.MOD_KJS)) {
            String domain = map.key.getNamespace();
            String id = map.key.getPath();
            if (id.startsWith("block_")) {
                Material mat = Material.get(id.replace("block_", ""));
                if (mat != NULL) {
                    map.remap(AntimatterMaterialTypes.BLOCK.get().get(mat).asBlock());
                    continue;
                }
            }
            if (id.startsWith("ore_")) {
                Block replacement = AntimatterAPI.get(BlockOre.class, id);
                if (replacement != null) {
                    map.remap(replacement);
                    continue;
                }
            }
            Block replacement = AntimatterAPI.get(Block.class, id, Ref.SHARED_ID);
            if (replacement != null) {
                map.remap(replacement);
            }
        }
    }

    @SubscribeEvent
    public static void remapMissingItems(final RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> map : event.getMappings(Ref.ID)) {
            Item replacement = AntimatterAPI.get(Item.class, map.key.getPath(), Ref.SHARED_ID);
            if (replacement != null) {
                map.remap(replacement);
            }
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<BlockEntity> event){
        if (event.getObject() instanceof TileEntityFakeBlock fakeBlock){
            event.addCapability(new ResourceLocation(Ref.ID, "fake_block"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
                    if (fakeBlock.controllerPos != null) {
                        fakeBlock.controllerPos.forEach(t -> fakeBlock.controllers.add((TileEntityBasicMultiMachine<?>) fakeBlock.getLevel().getBlockEntity(t)));
                        fakeBlock.controllerPos = null;
                    }
                    for (TileEntityBasicMultiMachine<?> controller : fakeBlock.controllers) {
                        LazyOptional<T> opt = controller.getCapabilityFromFake((Class<T>) AntimatterCaps.CAP_MAP.inverse().get(capability), fakeBlock.getBlockPos(), side, fakeBlock.covers.get(side));
                        if (opt.isPresent())
                            return opt;
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
                            return multiMachine.componentHandler.cast();
                        }
                    }
                    if (machine instanceof TileEntityHatch<?> hatch) {
                        if (cap == AntimatterCaps.COMPONENT_HANDLER_CAPABILITY && hatch.componentHandler.isPresent()) {
                            return hatch.componentHandler.cast();
                        }
                    }
                    if (cap == AntimatterCaps.COVERABLE_HANDLER_CAPABILITY && machine.coverHandler.isPresent()) return machine.coverHandler.side(side).cast();
                    if (side == machine.getFacing() && !machine.allowsFrontIO()) return LazyOptional.empty();
                    if (machine.blocksCapability(AntimatterCaps.CAP_MAP.inverse().get(cap), side)) return LazyOptional.empty();
                    if (cap == ITEM_HANDLER_CAPABILITY && machine.itemHandler.isPresent()) return machine.itemHandler.side(side).cast();
                    if (cap == AntimatterCaps.RECIPE_HANDLER_CAPABILITY && machine.recipeHandler.isPresent()) return machine.recipeHandler.side(side).cast();

                    else if (cap == FLUID_HANDLER_CAPABILITY && machine.fluidHandler.isPresent()) return machine.fluidHandler.side(side).cast();
                    else if ((cap == TesseractCaps.ENERGY_HANDLER_CAPABILITY || cap == CapabilityEnergy.ENERGY) && machine.energyHandler.isPresent())
                        return machine.energyHandler.side(side).cast();
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
                    if (capability == AntimatterCaps.COVERABLE_HANDLER_CAPABILITY && pipe.coverHandler.isPresent()) return pipe.coverHandler.cast();
                    if (!pipe.connects(side)) return LazyOptional.empty();
                    try {
                        if (capability == AntimatterCaps.CAP_MAP.get(pipe.getCapClass())){
                            return pipe.getPipeCapHolder().side(side).cast();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        return LazyOptional.empty();
                    }
                    if (capability == CapabilityEnergy.ENERGY && pipe instanceof TileEntityCable<?>){
                        return pipe.getPipeCapHolder().side(side).cast();
                    }
                    return LazyOptional.empty();
                }
            });
        }
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
