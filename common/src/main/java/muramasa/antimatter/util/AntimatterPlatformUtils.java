package muramasa.antimatter.util;

import carbonconfiglib.config.Config;
import carbonconfiglib.config.ConfigHandler;
import carbonconfiglib.config.ConfigSettings;
import com.mojang.math.Matrix4f;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.WorldGenEvent;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.structure.Pattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

public class AntimatterPlatformUtils {

    @ExpectPlatform
    public static void markAndNotifyBlock(Level level, BlockPos arg, @Nullable LevelChunk levelchunk, BlockState blockstate, BlockState arg2, int j, int k){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getFlammability(BlockState state, Level level, BlockPos pos, Direction face) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void setBurnTime(Item item, int burnTime){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void setFlammability(Block block, int burn, int spread){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isProduction(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getActiveNamespace(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Map<Item, Integer> getAllBurnables(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static MinecraftServer getCurrentServer(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isFabric(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isForge(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getModName(String modid){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean blockExists(ResourceLocation id){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean itemExists(ResourceLocation id){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean fluidExists(ResourceLocation id){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Block getBlockFromId(ResourceLocation id){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Item getItemFromID(ResourceLocation id){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Fluid getFluidFromID(ResourceLocation id){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceLocation getIdFromBlock(Block block){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceLocation getIdFromItem(Item item){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceLocation getIdFromFluid(Fluid fluid){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceLocation getIdFromMenuType(MenuType<?> menuType){
        throw new AssertionError();
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

    public static FluidHolder fromTag(CompoundTag tag){
        if (AntimatterPlatformUtils.isForge()){
            if (tag == null) {
                return FluidHooks.emptyFluid();
            }
            if (!tag.contains("FluidName", Tag.TAG_STRING)) {
                return FluidHooks.fluidFromCompound(tag);
            }

            ResourceLocation fluidName = new ResourceLocation(tag.getString("FluidName"));
            Fluid fluid = getFluidFromID(fluidName);
            if (fluid == null) {
                return FluidHooks.emptyFluid();
            }
            FluidHolder stack = FluidHooks.newFluidHolder(fluid, tag.getInt("Amount"), null);
            if (tag.contains("Tag", Tag.TAG_COMPOUND)) {
                stack.setCompound(tag.getCompound("Tag"));
            }
            return stack;
        }
        return FluidHooks.fluidFromCompound(tag);
    }

    @ExpectPlatform
    public static Collection<Item> getAllItems(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Collection<Fluid> getAllFluids(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CraftingEvent postCraftingEvent(IAntimatterRegistrar registrar){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void postLoaderEvent(IAntimatterRegistrar registrar, IRecipeRegistrate reg){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void postMaterialEvent(IAntimatterRegistrar registrar, MaterialEvent materialEvent){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ProvidersEvent postProviderEvent(Side side, IAntimatterRegistrar registrar){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static WorldGenEvent postWorldEvent(IAntimatterRegistrar registrar){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static InteractionResultHolder<ItemStack> postBucketUseEvent(Player player, Level world, ItemStack stack, BlockHitResult trace){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addMultiMachineInfo(BasicMultiMachine<?> machine, List<Pattern> patterns){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Matrix4f createMatrix4f(float[] values){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isRepairable(ItemStack stack){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addPool(LootTable table, LootPool pool){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceLocation getLootTableID(LootTable table){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean areCapsCompatible(ItemStack a, ItemStack b){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigDir(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ConfigHandler createConfig(String modid, Config config){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ConfigHandler createConfig(String modid, Config config, ConfigSettings settings){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends AbstractContainerMenu> MenuType<T> create(TriFunction<Integer, Inventory, FriendlyByteBuf, T> factory) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Item.Properties getToolProperties(CreativeModeTab group, boolean repairable){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCorrectTierForDrops(Tier tier, BlockState state){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static BlockState onToolUse(BlockState originalState, UseOnContext context, String action){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean onUseHoe(UseOnContext context){
        throw new AssertionError();
    }
}
