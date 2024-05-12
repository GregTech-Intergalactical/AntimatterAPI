package muramasa.antimatter.util.fabric;

import carbonconfiglib.CarbonConfig;
import carbonconfiglib.config.Config;
import carbonconfiglib.config.ConfigHandler;
import carbonconfiglib.config.ConfigSettings;
import com.mojang.math.Matrix4f;
import io.github.fabricators_of_create.porting_lib.util.Matrix4fHelper;
import io.github.fabricators_of_create.porting_lib.util.NetworkUtil;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import muramasa.antimatter.Ref;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.WorldGenEvent;
import muramasa.antimatter.event.fabric.CraftingEvents;
import muramasa.antimatter.event.fabric.LoaderEvents;
import muramasa.antimatter.event.fabric.ProviderEvents;
import muramasa.antimatter.event.fabric.WorldGenEvents;
import muramasa.antimatter.fabric.LootTableExtension;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.structure.Pattern;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AntimatterPlatformUtilsImpl {

    public static void markAndNotifyBlock(Level level, BlockPos arg, @Nullable LevelChunk levelchunk, BlockState blockstate, BlockState arg2, int j, int k){
        level.markAndNotifyBlock(arg, levelchunk, blockstate, arg2, j, k);
    }

    public static CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier){
        return FabricItemGroupBuilder.build(new ResourceLocation(domain, id), iconSupplier);
    }

    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        Integer burn = FuelRegistry.INSTANCE.get(stack.getItem());
        return burn == null ? 0 : burn;
    }

    public static int getFlammability(BlockState state, Level level, BlockPos pos, Direction face) {
        return FlammableBlockRegistry.getDefaultInstance().get(state.getBlock()).getBurnChance();
    }

    public static void setBurnTime(Item item, int burnTime){
        FuelRegistry.INSTANCE.add(item, burnTime);
    }

    public static void setFlammability(Block block, int burn, int spread){
        FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
    }

    public static Map<Item, Integer> getAllBurnables(){
        return ((FuelRegistryImpl)FuelRegistry.INSTANCE).getFuelTimes();
    }

    public static boolean isProduction(){
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static String getActiveNamespace(){
        return Ref.ID;
    }

    public static void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter){
        NetworkUtil.openGui(player, containerSupplier, extraDataWriter);
    }

    public static MinecraftServer getCurrentServer(){
        return ServerLifecycleHooks.getCurrentServer();
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

    public static ProvidersEvent postProviderEvent(Side side, IAntimatterRegistrar registrar){
        ProvidersEvent providerEvent = new ProvidersEvent(side);
        ProviderEvents.PROVIDERS.invoker().onProvidersInit(providerEvent);
        return providerEvent;
    }

    public static WorldGenEvent postWorldEvent(IAntimatterRegistrar registrar){
        WorldGenEvent event = new WorldGenEvent();
        WorldGenEvents.WORLD_GEN.invoker().onWorldGen(event);
        return event;
    }

    //TODO
    public static InteractionResultHolder<ItemStack> postBucketUseEvent(Player player, Level world, ItemStack stack, BlockHitResult trace){
        return null;
        //return ForgeEventFactory.onBucketUse(player, world, stack, trace);
    }

    //TODO
    public static void addMultiMachineInfo(BasicMultiMachine<?> machine, List<Pattern> patterns){
        /*if (AntimatterAPI.isModLoaded(Ref.MOD_JEI)){
            MultiMachineInfoCategory.addMultiMachine(new MultiMachineInfoPage(machine, patterns));
        }*/
    }

    public static Matrix4f createMatrix4f(float[] values){
        return Matrix4fHelper.fromFloatArray(values);
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
        return table.getLootTableId();
    }

    public static boolean areCapsCompatible(ItemStack a, ItemStack b){
        return true; //TODO figure out compat for future forge abstraction layers
    }

    public static Path getConfigDir(){
        return FabricLoader.getInstance().getConfigDir();
    }

    public static ConfigHandler createConfig(String modid, Config config){
        return CarbonConfig.createConfig(modid, config);
    }

    public static ConfigHandler createConfig(String modid, Config config, ConfigSettings settings){
        return CarbonConfig.createConfig(modid, config, settings);
    }
}
