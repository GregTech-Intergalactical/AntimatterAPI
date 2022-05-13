package muramasa.antimatter.util.forge;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.function.Consumer;

public class AntimatterPlatformUtilsImpl {

    public static Capability<ICoverHandler<?>> getCoverCap(){
        return AntimatterCaps.COVERABLE_HANDLER_CAPABILITY;
    }
    public static Capability<IComponentHandler> getComponentCap(){
        return AntimatterCaps.COMPONENT_HANDLER_CAPABILITY;
    }
    public static Capability<MachineRecipeHandler<?>> getRecipeCap(){
        return AntimatterCaps.RECIPE_HANDLER_CAPABILITY;
    }

    public static boolean isServer(){
        return FMLEnvironment.dist.isDedicatedServer() || EffectiveSide.get().isServer();
    }

    public static boolean isClient(){
        return FMLEnvironment.dist.isClient() || EffectiveSide.get().isClient();
    }

    public static String getActiveNamespace(){
        return ModLoadingContext.get().getActiveNamespace();
    }

    public static void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter){
        NetworkHooks.openGui(player, containerSupplier, extraDataWriter);
    }

    public static boolean isFabric(){
        return false;
    }

    public static boolean isForge(){
        return true;
    }

    public static boolean blockExists(ResourceLocation id){
        return ForgeRegistries.BLOCKS.containsKey(id);
    }

    public static boolean itemExists(ResourceLocation id){
        return ForgeRegistries.ITEMS.containsKey(id);
    }

    public static boolean fluidExists(ResourceLocation id){
        return ForgeRegistries.FLUIDS.containsKey(id);
    }

    public static Block getBlockFromId(ResourceLocation id){
        return ForgeRegistries.BLOCKS.getValue(id);
    }

    public static Item getItemFromID(ResourceLocation id){
        return ForgeRegistries.ITEMS.getValue(id);
    }

    public static Fluid getFluidFromID(ResourceLocation id){
        return ForgeRegistries.FLUIDS.getValue(id);
    }

    public static ResourceLocation getIdFromBlock(Block block){
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public static ResourceLocation getIdFromItem(Item item){
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static ResourceLocation getIdFromFluid(Fluid fluid){
        return ForgeRegistries.FLUIDS.getKey(fluid);
    }

    public static Block getBlockFromId(String domain, String id){
        return getBlockFromId(new ResourceLocation(domain, id));
    }

    public static Item getItemFromID(String domain, String id){
        return getItemFromID(new ResourceLocation(domain, id));
    }

    public static Fluid getFluidFromID(String domain, String id){
        return getFluidFromID(new ResourceLocation(domain, id));
    }

    public static Collection<Item> getAllItems(){
        return ForgeRegistries.ITEMS.getValues();
    }
}
