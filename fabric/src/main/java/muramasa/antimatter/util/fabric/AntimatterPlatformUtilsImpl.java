package muramasa.antimatter.util.fabric;

import carbonconfiglib.CarbonConfig;
import carbonconfiglib.config.Config;
import carbonconfiglib.config.ConfigHandler;
import carbonconfiglib.config.ConfigSettings;
import com.mojang.math.Matrix4f;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.BlockAccessor;
import io.github.fabricators_of_create.porting_lib.util.*;
import muramasa.antimatter.Ref;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.MaterialEvent;
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
import muramasa.antimatter.tool.IAbstractToolMethods;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AntimatterPlatformUtilsImpl implements AntimatterPlatformUtils {

    @Override
    public void markAndNotifyBlock(Level level, BlockPos arg, @Nullable LevelChunk levelchunk, BlockState blockstate, BlockState arg2, int j, int k){
        LevelUtil.markAndNotifyBlock(level, arg, levelchunk, blockstate, arg2, j, k);
    }

    @Override
    public CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier){
        return FabricItemGroupBuilder.build(new ResourceLocation(domain, id), iconSupplier);
    }

    @Override
    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        Integer burn = FuelRegistry.INSTANCE.get(stack.getItem());
        return burn == null ? 0 : burn;
    }

    @Override
    public int getFlammability(BlockState state, Level level, BlockPos pos, Direction face) {
        return FlammableBlockRegistry.getDefaultInstance().get(state.getBlock()).getBurnChance();
    }

    @Override
    public void setBurnTime(Item item, int burnTime){
        FuelRegistry.INSTANCE.add(item, burnTime);
    }

    @Override
    public void setFlammability(Block block, int burn, int spread){
        FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
    }

    @Override
    public Map<Item, Integer> getAllBurnables(){
        return ((FuelRegistryImpl)FuelRegistry.INSTANCE).getFuelTimes();
    }

    @Override
    public boolean isProduction(){
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public String getActiveNamespace(){
        return Ref.ID;
    }

    @Override
    public void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter){
        NetworkUtil.openGui(player, containerSupplier, extraDataWriter);
    }

    @Override
    public MinecraftServer getCurrentServer(){
        return ServerLifecycleHooks.getCurrentServer();
    }
    @Override
    public boolean isFabric(){
        return true;
    }

    @Override
    public boolean isForge(){
        return false;
    }

    @Override
    public String getModName(String modid){
        return FabricLoader.getInstance().getModContainer(modid).map(m -> m.getMetadata().getName()).orElse(modid);
    }

    @Override
    public boolean blockExists(ResourceLocation id){
        return Registry.BLOCK.containsKey(id);
    }

    @Override
    public boolean itemExists(ResourceLocation id){
        return Registry.ITEM.containsKey(id);
    }

    @Override
    public boolean fluidExists(ResourceLocation id){
        return Registry.FLUID.containsKey(id);
    }

    @Override
    public Block getBlockFromId(ResourceLocation id){
        return Registry.BLOCK.get(id);
    }

    @Override
    public Item getItemFromID(ResourceLocation id){
        return Registry.ITEM.get(id);
    }

    @Override
    public Fluid getFluidFromID(ResourceLocation id){
        return Registry.FLUID.get(id);
    }

    @Override
    public ResourceLocation getIdFromBlock(Block block){
        return Registry.BLOCK.getKey(block);
    }

    @Override
    public ResourceLocation getIdFromItem(Item item){
        return Registry.ITEM.getKey(item);
    }

    @Override
    public ResourceLocation getIdFromFluid(Fluid fluid){
        return Registry.FLUID.getKey(fluid);
    }

    @Override
    public ResourceLocation getIdFromMenuType(MenuType<?> menuType){
        return Registry.MENU.getKey(menuType);
    }

    @Override
    public Collection<Item> getAllItems(){
        return Registry.ITEM.stream().toList();
    }

    @Override
    public Collection<Fluid> getAllFluids(){
        return Registry.FLUID.stream().toList();
    }

    @Override
    public CraftingEvent postCraftingEvent(IAntimatterRegistrar registrar){
        CraftingEvent event = new CraftingEvent();
        CraftingEvents.CRAFTING.invoker().onMaterialRegister(event);
        return event;
    }

    @Override
    public void postLoaderEvent(IAntimatterRegistrar registrar, IRecipeRegistrate reg){
        LoaderEvents.LOADER.invoker().load(registrar, reg);
    }

    @Override
    public ProvidersEvent postProviderEvent(Side side, IAntimatterRegistrar registrar){
        ProvidersEvent providerEvent = new ProvidersEvent(side);
        ProviderEvents.PROVIDERS.invoker().onProvidersInit(providerEvent);
        return providerEvent;
    }

    @Override
    public WorldGenEvent postWorldEvent(IAntimatterRegistrar registrar){
        WorldGenEvent event = new WorldGenEvent();
        WorldGenEvents.WORLD_GEN.invoker().onWorldGen(event);
        return event;
    }

    //TODO
    @Override
    public InteractionResultHolder<ItemStack> postBucketUseEvent(Player player, Level world, ItemStack stack, BlockHitResult trace){
        return null;
        //return ForgeEventFactory.onBucketUse(player, world, stack, trace);
    }

    //TODO
    @Override
    public void addMultiMachineInfo(BasicMultiMachine<?> machine, List<Pattern> patterns){
        /*if (AntimatterAPI.isModLoaded(Ref.MOD_JEI)){
            MultiMachineInfoCategory.addMultiMachine(new MultiMachineInfoPage(machine, patterns));
        }*/
    }

    @Override
    public Matrix4f createMatrix4f(float[] values){
        return Matrix4fHelper.fromFloatArray(values);
    }

    //TODO
    @Override
    public boolean isRepairable(ItemStack stack){
        return true;
        //return stack.isRepairable();
    }

    @Override
    public void addPool(LootTable table, LootPool pool){
        ((LootTableExtension)table).addPool(pool);
    }

    @Override
    public ResourceLocation getLootTableID(LootTable table){
        return table.getLootTableId();
    }

    @Override
    public boolean areCapsCompatible(ItemStack a, ItemStack b){
        return true; //TODO figure out compat for future forge abstraction layers
    }

    @Override
    public Path getConfigDir(){
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public ConfigHandler createConfig(String modid, Config config){
        return CarbonConfig.createConfig(modid, config);
    }

    @Override
    public ConfigHandler createConfig(String modid, Config config, ConfigSettings settings){
        return CarbonConfig.createConfig(modid, config, settings);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> create(TriFunction<Integer, Inventory, FriendlyByteBuf, T> factory) {
        return new ExtendedScreenHandlerType<>(factory::apply);
    }

    @Override
    public Item.Properties getToolProperties(CreativeModeTab group, boolean repairable){
        FabricItemSettings properties = new FabricItemSettings().group(group);
        properties.customDamage(IAbstractToolMethods::damageItemStatic);
        return properties;
    }

    @Override
    public boolean isCorrectTierForDrops(Tier tier, BlockState state){
        return TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }

    //TODO make this use fabric events, if they exist
    @Override
    public BlockState onToolUse(BlockState originalState, UseOnContext context, String action){
        return originalState;
    }

    @Override
    public boolean onUseHoe(UseOnContext context){
        return false;
    }

    @Override
    public void popExperience(Block block, ServerLevel level, BlockPos pos, int exp){
        ((BlockAccessor)block).port_lib$popExperience(level, pos, exp);
    }

    @Override
    public void requestModelDataRefresh(BlockEntity tile){
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state, Player player){
        if (!state.requiresCorrectToolForDrops()){
            //TODO
            //return ForgeEventFactory.doPlayerHarvestCheck(player, state, true);
        }

        return player.hasCorrectToolForDrops(state);
    }

    @Override
    public int onBlockBreakEvent(Level level, GameType gameType, ServerPlayer entityPlayer, BlockPos pos)
    {
        return PortingHooks.onBlockBreakEvent(level, gameType, entityPlayer, pos);
    }

    //TODO
    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player){
        return isCorrectToolForDrops(state, player);
        //return state.canHarvestBlock(level, pos, player);
    }
}
