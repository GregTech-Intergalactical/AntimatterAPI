package muramasa.antimatter.util.forge;

import carbonconfiglib.CarbonConfig;
import carbonconfiglib.config.Config;
import carbonconfiglib.config.ConfigHandler;
import carbonconfiglib.config.ConfigSettings;
import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;
import com.terraformersmc.terraform.utils.TerraformFlammableBlockRegistry;
import com.terraformersmc.terraform.utils.TerraformFuelRegistry;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.forge.itemgroup.AntimatterItemGroup;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.WorldGenEvent;
import muramasa.antimatter.event.forge.AntimatterCraftingEvent;
import muramasa.antimatter.event.forge.AntimatterLoaderEvent;
import muramasa.antimatter.event.forge.AntimatterProvidersEvent;
import muramasa.antimatter.event.forge.AntimatterWorldGenEvent;
import muramasa.antimatter.integration.jei.category.MultiMachineInfoCategory;
import muramasa.antimatter.integration.jei.category.MultiMachineInfoPage;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.structure.Pattern;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AntimatterPlatformUtilsImpl implements AntimatterPlatformUtils {

    private static ImmutableMap<Item, Integer> FUEL_LIST = null;

    @Override
    public void markAndNotifyBlock(Level level, BlockPos arg, @Nullable LevelChunk levelchunk, BlockState blockstate, BlockState arg2, int j, int k){
        level.markAndNotifyBlock(arg, levelchunk, blockstate, arg2, j, k);
    }

    @Override
    public CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier){
        return new AntimatterItemGroup(domain, id, iconSupplier);
    }

    @Override
    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return ForgeHooks.getBurnTime(stack, recipeType);
    }

    @Override
    public int getFlammability(BlockState state, Level level, BlockPos pos, Direction face) {
        return state.getFlammability(level, pos, face);
    }

    @Override
    public void setBurnTime(Item item, int burnTime){
        TerraformFuelRegistry.addFuel(item, burnTime);
    }

    @Override
    public void setFlammability(Block block, int burn, int spread){
        TerraformFlammableBlockRegistry.addFlammableBlock(block, burn, spread);
    }

    @Override
    public Map<Item, Integer> getAllBurnables(){
        if (FUEL_LIST == null){
            ForgeHooks.updateBurns();
            ImmutableMap.Builder<Item, Integer> builder = ImmutableMap.builder();
            AntimatterPlatformUtils.INSTANCE.getAllItems().forEach(i -> {
                int burnTime = getBurnTime(i.getDefaultInstance(), null);
                if (burnTime > 0){
                    builder.put(i, burnTime);
                }
            });
            FUEL_LIST = builder.build();
        }
        return FUEL_LIST;
    }

    @Override
    public MinecraftServer getCurrentServer(){
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public boolean isProduction(){
        return FMLEnvironment.production;
    }

    @Override
    public String getActiveNamespace(){
        return ModLoadingContext.get().getActiveNamespace();
    }

    @Override
    public void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter){
        NetworkHooks.openGui(player, containerSupplier, extraDataWriter);
    }

    @Override
    public boolean isFabric(){
        return false;
    }

    @Override
    public boolean isForge(){
        return true;
    }

    @Override
    public String getModName(String modid){
        return ModList.get().getModContainerById(modid).map(m -> m.getModInfo().getDisplayName()).orElse(modid);
    }

    @Override
    public boolean blockExists(ResourceLocation id){
        return ForgeRegistries.BLOCKS.containsKey(id);
    }

    @Override
    public boolean itemExists(ResourceLocation id){
        return ForgeRegistries.ITEMS.containsKey(id);
    }

    @Override
    public boolean fluidExists(ResourceLocation id){
        return ForgeRegistries.FLUIDS.containsKey(id);
    }

    @Override
    public Block getBlockFromId(ResourceLocation id){
        return ForgeRegistries.BLOCKS.getValue(id);
    }

    @Override
    public Item getItemFromID(ResourceLocation id){
        return ForgeRegistries.ITEMS.getValue(id);
    }

    @Override
    public Fluid getFluidFromID(ResourceLocation id){
        return ForgeRegistries.FLUIDS.getValue(id);
    }

    @Override
    public ResourceLocation getIdFromBlock(Block block){
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    @Override
    public ResourceLocation getIdFromItem(Item item){
        return ForgeRegistries.ITEMS.getKey(item);
    }

    @Override
    public ResourceLocation getIdFromFluid(Fluid fluid){
        return ForgeRegistries.FLUIDS.getKey(fluid);
    }

    @Override
    public ResourceLocation getIdFromMenuType(MenuType<?> menuType){
        return ForgeRegistries.CONTAINERS.getKey(menuType);
    }

    @Override
    public Block getBlockFromId(String domain, String id){
        return getBlockFromId(new ResourceLocation(domain, id));
    }

    @Override
    public Item getItemFromID(String domain, String id){
        return getItemFromID(new ResourceLocation(domain, id));
    }

    @Override
    public Fluid getFluidFromID(String domain, String id){
        return getFluidFromID(new ResourceLocation(domain, id));
    }

    @Override
    public Collection<Item> getAllItems(){
        return ForgeRegistries.ITEMS.getValues();
    }

    @Override
    public Collection<Fluid> getAllFluids(){
        return ForgeRegistries.FLUIDS.getValues();
    }

    @Override
    public CraftingEvent postCraftingEvent(IAntimatterRegistrar registrar){
        CraftingEvent event = new CraftingEvent();
        AntimatterCraftingEvent ev = new AntimatterCraftingEvent(registrar, event);
        ModLoader.get().postEvent(ev);
        return event;
    }

    @Override
    public void postLoaderEvent(IAntimatterRegistrar registrar, IRecipeRegistrate reg){
        MinecraftForge.EVENT_BUS.post(new AntimatterLoaderEvent(registrar, reg));
    }

    @Override
    public ProvidersEvent postProviderEvent(Side side, IAntimatterRegistrar registrar){
        ProvidersEvent providerEvent = new ProvidersEvent(side);
        AntimatterProvidersEvent ev = new AntimatterProvidersEvent(providerEvent, registrar);
        ModLoader.get().postEvent(ev);
        //boolean bool = MinecraftForge.EVENT_BUS.post(ev);
        return providerEvent;
    }

    @Override
    public WorldGenEvent postWorldEvent(IAntimatterRegistrar registrar){
        WorldGenEvent event = new WorldGenEvent();
        AntimatterWorldGenEvent ev = new AntimatterWorldGenEvent(Antimatter.INSTANCE, event);
        MinecraftForge.EVENT_BUS.post(ev);
        return event;
    }

    @Override
    public InteractionResultHolder<ItemStack> postBucketUseEvent(Player player, Level world, ItemStack stack, BlockHitResult trace){
        return ForgeEventFactory.onBucketUse(player, world, stack, trace);
    }

    @Override
    public void addMultiMachineInfo(BasicMultiMachine<?> machine, List<Pattern> patterns){
        /*if (AntimatterAPI.isModLoaded(Ref.MOD_JEI)){
            MultiMachineInfoCategory.addMultiMachine(new MultiMachineInfoPage(machine, patterns));
        }*/
    }

    @Override
    public Matrix4f createMatrix4f(float[] values){
        return new Matrix4f(values);
    }

    @Override
    public boolean isRepairable(ItemStack stack){
        return stack.isRepairable();
    }

    @Override
    public void addPool(LootTable table, LootPool pool){
        table.addPool(pool);
    }

    @Override
    public ResourceLocation getLootTableID(LootTable table){
        return table.getLootTableId();
    }

    @Override
    public boolean areCapsCompatible(ItemStack a, ItemStack b){
        return a.areCapsCompatible(b);
    }

    @Override
    public Path getConfigDir(){
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public ConfigHandler createConfig(String modid, Config config){
        return CarbonConfig.CONFIGS.createConfig(config);
    }

    @Override
    public ConfigHandler createConfig(String modid, Config config, ConfigSettings settings){
        return CarbonConfig.CONFIGS.createConfig(config, settings);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> create(TriFunction<Integer, Inventory, FriendlyByteBuf, T> factory) {
        return IForgeMenuType.create(factory::apply);
    }

    @Override
    public Item.Properties getToolProperties(CreativeModeTab group, boolean repairable){
        Item.Properties properties = new Item.Properties().tab(group);
        if (!repairable) properties.setNoRepair();
        return properties;
    }

    @Override
    public boolean isCorrectTierForDrops(Tier tier, BlockState state){
        return TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }

    @Override
    public BlockState onToolUse(BlockState originalState, UseOnContext context, String action){
        return ForgeEventFactory.onToolUse(originalState, context, ToolAction.get(action), false);
    }

    @Override
    public boolean onUseHoe(UseOnContext context){
        return MinecraftForge.EVENT_BUS.post(new UseHoeEvent(context));
    }

    @Override
    public void popExperience(Block block, ServerLevel level, BlockPos pos, int exp){
        block.popExperience(level, pos, exp);
    }

    @Override
    public void requestModelDataRefresh(BlockEntity tile){
        ModelDataManager.requestModelDataRefresh(tile);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state, Player player){
        return ForgeHooks.isCorrectToolForDrops(state, player);
    }

    @Override
    public int onBlockBreakEvent(Level world, GameType gameType, ServerPlayer player, BlockPos pos){
        return ForgeHooks.onBlockBreakEvent(world, gameType, player, pos);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player){
        return state.canHarvestBlock(level, pos, player);
    }
}
