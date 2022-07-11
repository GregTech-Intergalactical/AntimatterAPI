package muramasa.antimatter.util.fabric;

import com.mojang.math.Matrix4f;
import dev.architectury.registry.registries.Registries;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.WorldGenEvent;
import muramasa.antimatter.event.fabric.CraftingEvents;
import muramasa.antimatter.event.fabric.LoaderEvents;
import muramasa.antimatter.event.fabric.ProviderEvents;
import muramasa.antimatter.event.fabric.WorldGenEvents;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.mixin.fabric.LootTableExtension;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.structure.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.client.NetworkHooks;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AntimatterPlatformUtilsImpl {
    //TODO
    /*public static LazyOptional<IEnergyHandler> getWrappedHandler(BlockEntity be, @Nullable Direction side){
        LazyOptional<IEnergyStorage> cap = be.getCapability(CapabilityEnergy.ENERGY, side);
        if (!cap.isPresent()) return LazyOptional.empty();
        return LazyOptional.of(() -> new EnergyTileWrapper(be, cap.orElse(null)));
    }

    public static boolean tileHasFEOrTRE(BlockEntity entity, Direction side){
        return entity.getCapability(CapabilityEnergy.ENERGY, side).isPresent();
    }*/

    public static CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier){
        return FabricItemGroupBuilder.build(new ResourceLocation(domain, id), iconSupplier);
    }

    //TODO
    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return 0;
        //return ForgeHooks.getBurnTime(stack, recipeType);
    }

    public static boolean isServer(){
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    public static boolean isClient(){
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static boolean isProduction(){
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static String getActiveNamespace(){
        return Ref.ID;
    }

    //TODO
    public static void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter){
        //NetworkHooks.openGui(player, containerSupplier, extraDataWriter);
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

    public static Collection<Fluid> getAllFluids(){
        return Registry.FLUID.stream().toList();
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

    //TODO
    /*public static InteractionResultHolder<ItemStack> postBucketUseEvent(Player player, Level world, ItemStack stack, BlockHitResult trace){
        return ForgeEventFactory.onBucketUse(player, world, stack, trace);
    }*/

    public static void writeFluidStack(FluidStack stack, FriendlyByteBuf buf) {
        FluidStack.writeToPacket(stack, buf);
    }

    public static FluidStack readFluidStack(FriendlyByteBuf buf) {
        return FluidStack.readFromPacket(buf);
    }

    //TODO
    public static void addMultiMachineInfo(BasicMultiMachine<?> machine, List<Pattern> patterns){
        /*if (AntimatterAPI.isModLoaded(Ref.MOD_JEI)){
            MultiMachineInfoCategory.addMultiMachine(new MultiMachineInfoPage(machine, patterns));
        }*/
    }

    public static Matrix4f createMatrix4f(float[] values){
        return new com.mojang.math.Matrix4f().setMValues(values);
    }

    //TODO
    public static boolean isRepairable(ItemStack stack){
        return true;
        //return stack.isRepairable();
    }

    public static void addPool(LootTable table, LootPool pool){
        ((LootTableExtension)table).addPool(pool);
    }

    public static ResourceLocation getLootTableID(LootTable table){
        return ((LootTableExtension)table).getLootTableId();
    }

    public static boolean areCapsCompatible(ItemStack a, ItemStack b){
        return a.areCapsCompatible(b);
    }
}
