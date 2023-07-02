package muramasa.antimatter.util.forge;

import com.mojang.math.Matrix4f;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.forge.itemgroup.AntimatterItemGroup;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.WorldGenEvent;
import muramasa.antimatter.event.forge.*;
import muramasa.antimatter.integration.jei.category.MultiMachineInfoCategory;
import muramasa.antimatter.integration.jei.category.MultiMachineInfoPage;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.structure.Pattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AntimatterPlatformUtilsImpl {

    public static void markAndNotifyBlock(Level level, BlockPos arg, @Nullable LevelChunk levelchunk, BlockState blockstate, BlockState arg2, int j, int k){
        level.markAndNotifyBlock(arg, levelchunk, blockstate, arg2, j, k);
    }

    public static CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier){
        return new AntimatterItemGroup(domain, id, iconSupplier);
    }

    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return ForgeHooks.getBurnTime(stack, recipeType);
    }

    public static boolean isServer(){
        return FMLEnvironment.dist.isDedicatedServer() || EffectiveSide.get().isServer();
    }

    public static MinecraftServer getCurrentServer(){
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static boolean isClient(){
        return FMLEnvironment.dist.isClient() || EffectiveSide.get().isClient();
    }

    public static boolean isProduction(){
        return FMLEnvironment.production;
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

    public static String getModName(String modid){
        return ModList.get().getModContainerById(modid).map(m -> m.getModInfo().getDisplayName()).orElse(modid);
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

    public static Collection<Fluid> getAllFluids(){
        return ForgeRegistries.FLUIDS.getValues();
    }

    public static CraftingEvent postCraftingEvent(IAntimatterRegistrar registrar){
        CraftingEvent event = new CraftingEvent();
        AntimatterCraftingEvent ev = new AntimatterCraftingEvent(registrar, event);
        ModLoader.get().postEvent(ev);
        return event;
    }

    public static void postLoaderEvent(IAntimatterRegistrar registrar, IRecipeRegistrate reg){
        MinecraftForge.EVENT_BUS.post(new AntimatterLoaderEvent(registrar, reg));
    }

    public static ProvidersEvent postProviderEvent(Side side, IAntimatterRegistrar registrar){
        ProvidersEvent providerEvent = new ProvidersEvent(side);
        AntimatterProvidersEvent ev = new AntimatterProvidersEvent(providerEvent, registrar);
        ModLoader.get().postEvent(ev);
        //boolean bool = MinecraftForge.EVENT_BUS.post(ev);
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

    public static void addMultiMachineInfo(BasicMultiMachine<?> machine, List<Pattern> patterns){
        if (AntimatterAPI.isModLoaded(Ref.MOD_JEI)){
            MultiMachineInfoCategory.addMultiMachine(new MultiMachineInfoPage(machine, patterns));
        }
    }

    public static Matrix4f createMatrix4f(float[] values){
        return new Matrix4f(values);
    }

    public static boolean isRepairable(ItemStack stack){
        return stack.isRepairable();
    }

    public static void addPool(LootTable table, LootPool pool){
        table.addPool(pool);
    }

    public static ResourceLocation getLootTableID(LootTable table){
        return table.getLootTableId();
    }

    public static boolean areCapsCompatible(ItemStack a, ItemStack b){
        return a.areCapsCompatible(b);
    }

    public static Path getConfigDir(){
        return FMLPaths.CONFIGDIR.get();
    }
}
