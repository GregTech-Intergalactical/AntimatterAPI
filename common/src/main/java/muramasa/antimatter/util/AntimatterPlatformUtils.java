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
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;


public interface AntimatterPlatformUtils {
    AntimatterPlatformUtils INSTANCE = ImplLoader.load(AntimatterPlatformUtils.class);

    void markAndNotifyBlock(Level level, BlockPos arg, @Nullable LevelChunk levelchunk, BlockState blockstate, BlockState arg2, int j, int k);

    CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier);

    int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType);

    int getFlammability(BlockState state, Level level, BlockPos pos, Direction face);

    void setBurnTime(Item item, int burnTime);

    void setFlammability(Block block, int burn, int spread);

    boolean isProduction();

    String getActiveNamespace();

    Map<Item, Integer> getAllBurnables();

    void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter);

    MinecraftServer getCurrentServer();

    boolean isFabric();

    boolean isForge();

    String getModName(String modid);

    boolean blockExists(ResourceLocation id);

    boolean itemExists(ResourceLocation id);

    boolean fluidExists(ResourceLocation id);

    Block getBlockFromId(ResourceLocation id);

    Item getItemFromID(ResourceLocation id);

    Fluid getFluidFromID(ResourceLocation id);

    ResourceLocation getIdFromBlock(Block block);

    ResourceLocation getIdFromItem(Item item);

    ResourceLocation getIdFromFluid(Fluid fluid);

    ResourceLocation getIdFromMenuType(MenuType<?> menuType);

    default Block getBlockFromId(String domain, String id){
        return getBlockFromId(new ResourceLocation(domain, id));
    }

    default Item getItemFromID(String domain, String id){
        return getItemFromID(new ResourceLocation(domain, id));
    }

    default Fluid getFluidFromID(String domain, String id){
        return getFluidFromID(new ResourceLocation(domain, id));
    }

    default FluidHolder fromTag(CompoundTag tag){
        if (isForge()){
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

    Collection<Item> getAllItems();

    Collection<Fluid> getAllFluids();

    CraftingEvent postCraftingEvent(IAntimatterRegistrar registrar);

    void postLoaderEvent(IAntimatterRegistrar registrar, IRecipeRegistrate reg);

    ProvidersEvent postProviderEvent(Side side, IAntimatterRegistrar registrar);

    WorldGenEvent postWorldEvent(IAntimatterRegistrar registrar);

    InteractionResultHolder<ItemStack> postBucketUseEvent(Player player, Level world, ItemStack stack, BlockHitResult trace);

    void addMultiMachineInfo(BasicMultiMachine<?> machine, List<Pattern> patterns);

    Matrix4f createMatrix4f(float[] values);

    boolean isRepairable(ItemStack stack);

    void addPool(LootTable table, LootPool pool);

    ResourceLocation getLootTableID(LootTable table);

    boolean areCapsCompatible(ItemStack a, ItemStack b);

    Path getConfigDir();

    ConfigHandler createConfig(String modid, Config config);

    ConfigHandler createConfig(String modid, Config config, ConfigSettings settings);

    <T extends AbstractContainerMenu> MenuType<T> create(TriFunction<Integer, Inventory, FriendlyByteBuf, T> factory);

    Item.Properties getToolProperties(CreativeModeTab group, boolean repairable);

    boolean isCorrectTierForDrops(Tier tier, BlockState state);

    BlockState onToolUse(BlockState originalState, UseOnContext context, String action);

    boolean onUseHoe(UseOnContext context);

    void requestModelDataRefresh(BlockEntity tile);

    void popExperience(Block block, ServerLevel level, BlockPos pos, int exp);

    boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player);

    int onBlockBreakEvent(Level world, GameType gameType, ServerPlayer player, BlockPos pos);

    boolean isCorrectToolForDrops(BlockState state, Player player);
}
