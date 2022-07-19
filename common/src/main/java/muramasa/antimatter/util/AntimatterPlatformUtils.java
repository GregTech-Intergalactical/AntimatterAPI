package muramasa.antimatter.util;

import com.mojang.math.Matrix4f;
import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.WorldGenEvent;
import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.structure.Pattern;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AntimatterPlatformUtils {

    @ExpectPlatform
    public static LazyOptional<IEnergyHandler> getWrappedHandler(BlockEntity be, @Nullable Direction side){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean tileHasFEOrTRE(BlockEntity entity, Direction side){
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
    public static boolean isServer(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isClient(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isProduction(){
        throw new AssertionError();
    }

    public static Side getSide(){
        return isClient() ? Side.CLIENT : Side.SERVER;
    }

    @ExpectPlatform
    public static String getActiveNamespace(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter){
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
    public static ProvidersEvent postProviderEvent(DataGenerator generator, Side side, IAntimatterRegistrar registrar){
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
    public static void writeFluidStack(FluidStack stack, FriendlyByteBuf buf) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static FluidStack readFluidStack(FriendlyByteBuf buf) {
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
}
