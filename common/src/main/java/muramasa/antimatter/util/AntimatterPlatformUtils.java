package muramasa.antimatter.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.WorldGenEvent;
import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.Side;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AntimatterPlatformUtils {

    @ExpectPlatform
    public static LazyOptional<IEnergyHandler> getWrappedHandler(BlockEntity be, @Nullable Direction side){
        return LazyOptional.empty();
    }

    @ExpectPlatform
    public static boolean tileHasFEOrTRE(BlockEntity entity, Direction side){
        return false;
    }

    @ExpectPlatform
    public static CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier){
        return null;
    }

    @ExpectPlatform
    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return 0;
    }

    @ExpectPlatform
    public static int getFluidColor(Fluid fluid){
        return 0;
    }

    @ExpectPlatform
    public static SoundEvent getFluidSound(Fluid fluid, boolean fill){
        return null;
    }



    @ExpectPlatform
    public static boolean isServer(){
        return false;
    }

    @ExpectPlatform
    public static boolean isClient(){
        return false;
    }

    @ExpectPlatform
    public static Side getSide(){
        return null;
    }

    @ExpectPlatform
    public static String getActiveNamespace(){
        return "";
    }

    @ExpectPlatform
    public static void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter){
    }

    @ExpectPlatform
    public static boolean isFabric(){
        return false;
    }

    @ExpectPlatform
    public static boolean isForge(){
        return true;
    }

    @ExpectPlatform
    public static boolean blockExists(ResourceLocation id){
        return false;
    }

    @ExpectPlatform
    public static boolean itemExists(ResourceLocation id){
        return false;
    }

    @ExpectPlatform
    public static boolean fluidExists(ResourceLocation id){
        return false;
    }

    @ExpectPlatform
    public static Block getBlockFromId(ResourceLocation id){
        return null;
    }

    @ExpectPlatform
    public static Item getItemFromID(ResourceLocation id){
        return null;
    }

    @ExpectPlatform
    public static Fluid getFluidFromID(ResourceLocation id){
        return null;
    }

    @ExpectPlatform
    public static ResourceLocation getIdFromBlock(Block block){
        return null;
    }

    @ExpectPlatform
    public static ResourceLocation getIdFromItem(Item item){
        return null;
    }

    @ExpectPlatform
    public static ResourceLocation getIdFromFluid(Fluid fluid){
        return null;
    }

    @ExpectPlatform
    public static ResourceLocation getIdFromMenuType(MenuType<?> menuType){
        return null;
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
        return Collections.emptySet();
    }

    @ExpectPlatform
    public static CraftingEvent postCraftingEvent(IAntimatterRegistrar registrar){
        return null;
    }

    @ExpectPlatform
    public static void postLoaderEvent(IAntimatterRegistrar registrar, IRecipeRegistrate reg){
    }

    @ExpectPlatform
    public static MaterialEvent postMaterialEvent(IAntimatterRegistrar registrar, MaterialEvent materialEvent){
        return null;
    }

    @ExpectPlatform
    public static ProvidersEvent postProviderEvent(DataGenerator generator, Side side, IAntimatterRegistrar registrar){
        return null;
    }

    @ExpectPlatform
    public static WorldGenEvent postWorldEvent(IAntimatterRegistrar registrar){
        return null;
    }

    @ExpectPlatform
    public static InteractionResultHolder<ItemStack> postBucketUseEvent(Player player, Level world, ItemStack stack, BlockHitResult trace){
        return InteractionResultHolder.pass(stack);
    }

    @ExpectPlatform
    public static void writeFluidStack(FluidStack stack, FriendlyByteBuf buf) {

    }

    @ExpectPlatform
    public static FluidStack readFluidStack(FriendlyByteBuf buf) {
        return FluidStack.EMPTY;
    }
}
