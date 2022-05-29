package muramasa.antimatter.util.forge;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.client.forge.itemgroup.AntimatterItemGroup;
import muramasa.antimatter.event.forge.AntimatterCraftingEvent;
import muramasa.antimatter.event.forge.AntimatterLoaderEvent;
import muramasa.antimatter.event.forge.AntimatterProvidersEvent;
import muramasa.antimatter.event.forge.AntimatterWorldGenEvent;
import muramasa.antimatter.material.MaterialEvent;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.network.forge.AntimatterNetworkImpl;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.tesseract.forge.EnergyTileWrapper;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AntimatterPlatformUtilsImpl {

    public static LazyOptional<IEnergyHandler> getWrappedHandler(BlockEntity be, @Nullable Direction side){
        LazyOptional<IEnergyStorage> cap = be.getCapability(CapabilityEnergy.ENERGY, side);
        if (!cap.isPresent()) return LazyOptional.empty();
        return LazyOptional.of(() -> new EnergyTileWrapper(be, cap.orElse(null)));
    }


    public static CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier){
        return new AntimatterItemGroup(domain, id, iconSupplier);
    }

    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return ForgeHooks.getBurnTime(stack, recipeType);
    }

    public static int getFluidColor(Fluid fluid){
        return fluid.getAttributes().getColor();
    }

    public static SoundEvent getFluidSound(Fluid fluid, boolean fill){
        return fill ? fluid.getAttributes().getFillSound() : fluid.getAttributes().getEmptySound();
    }

    public static boolean isServer(){
        return FMLEnvironment.dist.isDedicatedServer() || EffectiveSide.get().isServer();
    }

    public static boolean isClient(){
        return FMLEnvironment.dist.isClient() || EffectiveSide.get().isClient();
    }

    public static Side getSide(){
        return isClient() ? Side.CLIENT : Side.SERVER;
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

    public static ResourceLocation getIdFromMenuType(MenuType<?> menuType){
        return ForgeRegistries.CONTAINERS.getKey(menuType);
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

    public static CraftingEvent postCraftingEvent(IAntimatterRegistrar registrar){
        CraftingEvent event = new CraftingEvent();
        AntimatterCraftingEvent ev = new AntimatterCraftingEvent(registrar, event);
        MinecraftForge.EVENT_BUS.post(ev);
        return event;
    }

    public static void postLoaderEvent(IAntimatterRegistrar registrar, IRecipeRegistrate reg){
        MinecraftForge.EVENT_BUS.post(new AntimatterLoaderEvent(registrar, reg));
    }

    public static void postMaterialEvent(IAntimatterRegistrar registrar, MaterialEvent materialEvent){

    }

    public static ProvidersEvent postProviderEvent(DataGenerator generator, Side side, IAntimatterRegistrar registrar){
        ProvidersEvent providerEvent = new ProvidersEvent(generator);
        AntimatterProvidersEvent ev = new AntimatterProvidersEvent(providerEvent, side == Side.CLIENT ? Dist.CLIENT : Dist.DEDICATED_SERVER, registrar);
        MinecraftForge.EVENT_BUS.post(ev);
        return providerEvent;
    }

    public static WorldGenEvent postWorldEvent(IAntimatterRegistrar registrar){
        WorldGenEvent event = new WorldGenEvent();
        AntimatterWorldGenEvent ev = new AntimatterWorldGenEvent(Antimatter.INSTANCE, event);
        MinecraftForge.EVENT_BUS.post(ev);
        return event;
    }

    public static InteractionResultHolder<ItemStack> postBucketUseEvent(Player player, Level world, ItemStack stack, BlockHitResult trace){
        return ForgeEventFactory.onBucketUse(player, world, stack, trace);
    }

    public static void writeFluidStack(FluidStack stack, FriendlyByteBuf buf) {
        buf.writeFluidStack(stack);
    }

    public static FluidStack readFluidStack(FriendlyByteBuf buf) {
        return buf.readFluidStack();
    }
}
