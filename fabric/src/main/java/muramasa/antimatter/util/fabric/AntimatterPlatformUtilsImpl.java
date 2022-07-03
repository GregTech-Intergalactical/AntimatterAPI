package muramasa.antimatter.util.fabric;

import com.mojang.math.Matrix4f;
import dev.architectury.registry.registries.Registries;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.WorldGenEvent;
import muramasa.antimatter.event.fabric.CraftingEvents;
import muramasa.antimatter.event.fabric.LoaderEvents;
import muramasa.antimatter.event.fabric.ProviderEvents;
import muramasa.antimatter.event.fabric.WorldGenEvents;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.Side;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.function.Supplier;

public class AntimatterPlatformUtilsImpl {

    public static boolean isServer(){
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    public static boolean isClient(){
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static String getActiveNamespace(){
        return Ref.ID;
    }
    public static boolean isFabric(){
        return true;
    }

    public static boolean isForge(){
        return false;
    }

    public static String getModName(String modid){
        return FabricLoader.getInstance().getModContainer(modid).map(m -> m.getMetadata().getName()).orElse(modid);
    }

    public static boolean blockExists(ResourceLocation id){
        return Registry.BLOCK.containsKey(id);
    }

    public static boolean itemExists(ResourceLocation id){
        return Registry.ITEM.containsKey(id);
    }

    public static boolean fluidExists(ResourceLocation id){
        return Registry.FLUID.containsKey(id);
    }

    public static Block getBlockFromId(ResourceLocation id){
        return Registry.BLOCK.get(id);
    }

    public static Item getItemFromID(ResourceLocation id){
        return Registry.ITEM.get(id);
    }

    public static Fluid getFluidFromID(ResourceLocation id){
        return Registry.FLUID.get(id);
    }

    public static ResourceLocation getIdFromBlock(Block block){
        return Registry.BLOCK.getKey(block);
    }

    public static ResourceLocation getIdFromItem(Item item){
        return Registry.ITEM.getKey(item);
    }

    public static ResourceLocation getIdFromFluid(Fluid fluid){
        return Registry.FLUID.getKey(fluid);
    }

    public static ResourceLocation getIdFromMenuType(MenuType<?> menuType){
        return Registry.MENU.getKey(menuType);
    }

    public static Collection<Item> getAllItems(){
        return Registry.ITEM.stream().toList();
    }

    public static CraftingEvent postCraftingEvent(IAntimatterRegistrar registrar){
        CraftingEvent event = new CraftingEvent();
        CraftingEvents.CRAFTING.invoker().onMaterialRegister(event);
        return event;
    }

    public static void postLoaderEvent(IAntimatterRegistrar registrar, IRecipeRegistrate reg){
        LoaderEvents.LOADER.invoker().load(registrar, reg);
    }

    public static ProvidersEvent postProviderEvent(DataGenerator generator, Side side, IAntimatterRegistrar registrar){
        ProvidersEvent providerEvent = new ProvidersEvent(generator, side);
        ProviderEvents.PROVIDERS.invoker().onProvidersInit(providerEvent);
        return providerEvent;
    }

    public static WorldGenEvent postWorldEvent(IAntimatterRegistrar registrar){
        WorldGenEvent event = new WorldGenEvent();
        WorldGenEvents.WORLD_GEN.invoker().onWorldGen(event);
        return event;
    }

    public static CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier){
        return FabricItemGroupBuilder.build(new ResourceLocation(domain, id), iconSupplier);
    }

    public static Matrix4f createMatrix4f(float[] values){
        return new com.mojang.math.Matrix4f().setMValues(values);
    }

    public static boolean areCapsCompatible(ItemStack a, ItemStack b){
        return a.areCapsCompatible(b);
    }
}
