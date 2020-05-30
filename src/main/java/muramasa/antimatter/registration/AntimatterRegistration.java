package muramasa.antimatter.registration;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.gui.MenuHandlerCover;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.recipe.condition.ConfigCondition;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public final class AntimatterRegistration {

    private static final List<BlockItem> INTERNAL_ITEM_BLOCKS = new ObjectArrayList<>();

    public static void setup(final RegistryEvent.NewRegistry e) {
        final String currentDomain = ModLoadingContext.get().getActiveNamespace();
        if (currentDomain.equals(Ref.ID)) {
            Antimatter.LOGGER.info("AntimatterAPI Data Initialization Stage...");
            AntimatterAPI.onRegistration(RegistrationEvent.DATA_INIT);
        }
        if (currentDomain.equals(Ref.ID)) {
            Antimatter.LOGGER.info("AntimatterAPI Registry Objects Initialization Stage...");
            AntimatterAPI.onRegistration(RegistrationEvent.REGISTRY_BUILD);
        }
        AntimatterAPI.all(IRegistryEntryProvider.class, currentDomain, p -> p.onRegistryBuild(currentDomain, null));
    }

    @SuppressWarnings("unchecked")
    public static void onRegister(final RegistryEvent.Register<?> e) {
        final String currentDomain = ModLoadingContext.get().getActiveNamespace();
        if (currentDomain.equals(Ref.ID)) {
            Antimatter.LOGGER.info("AntimatterAPI Data Post-Initialization Stage...");
            AntimatterAPI.onRegistration(RegistrationEvent.DATA_POST_INIT);
        }
        if (e.getRegistry() == ForgeRegistries.BLOCKS) onBlockRegister((IForgeRegistry<Block>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.ITEMS) onItemRegister((IForgeRegistry<Item>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.TILE_ENTITIES) onTileEntityRegister((IForgeRegistry<TileEntityType<?>>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.FLUIDS) onFluidRegister((IForgeRegistry<Fluid>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.CONTAINERS) onContainerRegister((IForgeRegistry<ContainerType<?>>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.SOUND_EVENTS) onSoundRegister((IForgeRegistry<SoundEvent>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.FEATURES) onFeatureRegister((IForgeRegistry<Feature<?>>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.RECIPE_SERIALIZERS) onCraftingSerializerRegister((IForgeRegistry<IRecipeSerializer<?>>) e.getRegistry(), currentDomain);
    }

    private static void onBlockRegister(IForgeRegistry<Block> blocks, final String currentDomain) {
        for (Block block : AntimatterAPI.all(Block.class, currentDomain)) {
            if (block instanceof IAntimatterObject) block.setRegistryName(currentDomain, ((IAntimatterObject) block).getId());
            INTERNAL_ITEM_BLOCKS.add(block instanceof IItemBlockProvider ? ((IItemBlockProvider) block).getItemBlock() : new AntimatterItemBlock(block));
            blocks.register(block);
        }
        for (AntimatterFluid fluid : AntimatterAPI.all(AntimatterFluid.class, currentDomain)) {
            blocks.register(fluid.getFluidBlock().setRegistryName(currentDomain, fluid.getId()));
        }
    }

    private static void onItemRegister(IForgeRegistry<Item> items, final String currentDomain) {
        for (Item item : INTERNAL_ITEM_BLOCKS) {
            if (item.getRegistryName() != null && item.getRegistryName().getNamespace().equals(currentDomain)) items.register(item);
        }
        for (Item item : AntimatterAPI.all(Item.class, currentDomain)) {
            if (item instanceof IAntimatterObject) item.setRegistryName(currentDomain, ((IAntimatterObject) item).getId());
            items.register(item);
        }
        for (AntimatterToolType type : AntimatterAPI.all(AntimatterToolType.class, currentDomain)) {
            if (type.isPowered()) {
                for (IAntimatterTool i : type.instantiatePoweredTools(currentDomain)) {
                    items.register(i.asItem().setRegistryName(currentDomain, i.getId()));
                }
            }
            else {
                IAntimatterTool i = type.instantiateTools(currentDomain);
                items.register(i.asItem().setRegistryName(currentDomain, i.getId()));
            }
        }
        for (AntimatterFluid fluid : AntimatterAPI.all(AntimatterFluid.class, currentDomain)) {
            items.register(fluid.getContainerItem().setRegistryName(currentDomain, fluid.getId() + "_bucket"));
        }
    }

    private static void onTileEntityRegister(IForgeRegistry<TileEntityType<?>> tiles, final String currentDomain) {
        for (Machine<?> machine : AntimatterAPI.all(Machine.class, currentDomain)) {
            tiles.register(machine.getTileType().setRegistryName(currentDomain, machine.getId()));
        }
        for (PipeType<?> pipe : AntimatterAPI.all(PipeType.class, currentDomain)) {
            tiles.register(pipe.getTileType().setRegistryName(currentDomain, pipe.getId() + "_" + pipe.getMaterial()));
        }
    }

    private static void onFluidRegister(IForgeRegistry<Fluid> fluids, final String currentDomain) {
        for (AntimatterFluid fluid : AntimatterAPI.all(AntimatterFluid.class, currentDomain)) {
            fluids.register(fluid.getFluid().setRegistryName(currentDomain, fluid.getId()));
            fluids.register(fluid.getFlowingFluid().setRegistryName(currentDomain, "flowing_".concat(fluid.getId())));
        }
    }

    private static void onContainerRegister(IForgeRegistry<ContainerType<?>> containers, final String currentDomain) {
        AntimatterAPI.all(MenuHandlerMachine.class, currentDomain, h -> containers.register(h.getContainerType()));
        AntimatterAPI.all(MenuHandlerCover.class, currentDomain, h -> containers.register(h.getContainerType()));
    }

    private static void onSoundRegister(IForgeRegistry<SoundEvent> sounds, final String currentDomain) {
        if (!currentDomain.equals(Ref.ID)) return;
        sounds.registerAll(Ref.DRILL.setRegistryName(Ref.ID, "drill"), Ref.WRENCH.setRegistryName(Ref.ID, "wrench"));
    }

    private static void onFeatureRegister(IForgeRegistry<Feature<?>> features, final String currentDomain) { }

    private static void onCraftingSerializerRegister(IForgeRegistry<IRecipeSerializer<?>> serializers, final String currentDomain) {
        if (!currentDomain.equals(Ref.ID)) return;
        CraftingHelper.register(ConfigCondition.Serializer.INSTANCE);
    }

}
