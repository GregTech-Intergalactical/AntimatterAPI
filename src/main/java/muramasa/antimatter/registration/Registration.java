package muramasa.antimatter.registration;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.gui.MenuHandlerCover;
import muramasa.antimatter.gui.MenuHandlerMachine;
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
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class Registration {

    private static final List<BlockItem> INTERNAL_ITEM_BLOCKS = new ObjectArrayList<>();

    public static void beforeRegister(final RegistryEvent.NewRegistry e) {
        final String currentDomain = ModLoadingContext.get().getActiveNamespace();
        if (currentDomain.equals(Ref.ID)) AntimatterAPI.onRegistration(RegistrationEvent.DATA_INIT);
        AntimatterAPI.all(IRegistryEntryProvider.class, currentDomain, p -> p.onRegistryBuild(currentDomain, null));
    }

    @SuppressWarnings("unchecked")
    public static void onRegister(final RegistryEvent.Register<?> e) {
        final String currentDomain = ModLoadingContext.get().getActiveNamespace();
        AntimatterAPI.all(IRegistryEntryProvider.class, currentDomain, p -> p.onRegistryBuild(currentDomain, e.getRegistry()));
        if (e.getRegistry() == ForgeRegistries.BLOCKS) onBlockRegister((IForgeRegistry<Block>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.ITEMS) onItemRegister((IForgeRegistry<Item>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.TILE_ENTITIES) onTileEntityRegister((IForgeRegistry<TileEntityType<?>>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.FLUIDS) onFluidRegister((IForgeRegistry<Fluid>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.CONTAINERS) onContainerRegister((IForgeRegistry<ContainerType<?>>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.SOUND_EVENTS) onSoundRegister((IForgeRegistry<SoundEvent>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.FEATURES) onFeatureRegister((IForgeRegistry<Feature<?>>) e.getRegistry(), currentDomain);
        else if (e.getRegistry() == ForgeRegistries.RECIPE_SERIALIZERS) onCraftingSerializerRegister((IForgeRegistry<IRecipeSerializer<?>>) e.getRegistry(), currentDomain);
    }

    public static void onBlockRegister(IForgeRegistry<Block> blocks, final String currentDomain) {
        for (Block block : AntimatterAPI.all(Block.class, currentDomain)) {
            if (block instanceof IAntimatterObject) block.setRegistryName(currentDomain, ((IAntimatterObject) block).getId());
            INTERNAL_ITEM_BLOCKS.add(block instanceof IItemBlockProvider ? ((IItemBlockProvider) block).getItemBlock() : new AntimatterItemBlock(block));
            blocks.register(block);
        }
    }

    public static void onItemRegister(IForgeRegistry<Item> items, final String currentDomain) {
        for (Item item : INTERNAL_ITEM_BLOCKS) {
            if (item.getRegistryName() != null && item.getRegistryName().getNamespace().equals(currentDomain)) {
                items.register(item);
            }
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
    }

    public static void onTileEntityRegister(IForgeRegistry<TileEntityType<?>> tiles, final String currentDomain) {
        // if (!ModLoadingContext.get().getActiveNamespace().equals(Ref.ID)) return;
    }

    public static void onFluidRegister(IForgeRegistry<Fluid> fluids, final String currentDomain) {

    }

    public static void onContainerRegister(IForgeRegistry<ContainerType<?>> containers, final String currentDomain) {
        AntimatterAPI.all(MenuHandlerMachine.class, h -> containers.register(h.getContainerType()));
        AntimatterAPI.all(MenuHandlerCover.class, h -> containers.register(h.getContainerType()));
    }

    public static void onSoundRegister(IForgeRegistry<SoundEvent> sounds, final String currentDomain) {
        if (!currentDomain.equals(Ref.ID)) return;
        sounds.registerAll(Ref.DRILL, Ref.WRENCH);
    }

    public static void onFeatureRegister(IForgeRegistry<Feature<?>> features, final String currentDomain) {
        // if (currentDomain.equals(Ref.ID)) AntimatterAPI.onRegistration(RegistrationEvent.WORLDGEN_INIT);
        // for (AntimatterFeature<?> feature : AntimatterAPI.all(AntimatterFeature.class, currentDomain)) {
            // features.register(feature.setRegistryName(currentDomain, feature.getId()));
        // }
    }

    public static void onCraftingSerializerRegister(IForgeRegistry<IRecipeSerializer<?>> serializers, final String currentDomain) {
        if (!currentDomain.equals(Ref.ID)) return;
        CraftingHelper.register(ConfigCondition.Serializer.INSTANCE);
    }

}
