package muramasa.antimatter;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.recipe.RecipeMap;
import muramasa.antimatter.registration.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class AntimatterAPI {

    private static final Object2ObjectMap<Class<?>, Object2ObjectMap<String, Object>> OBJECTS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<String, List<Runnable>> CALLBACKS = new Object2ObjectOpenHashMap<>();
    private static final Int2ObjectOpenHashMap<Material> MATERIAL_HASH_LOOKUP = new Int2ObjectOpenHashMap<>();
    private static final Set<RegistrationEvent> REGISTRATION_EVENTS_HANDLED = new ObjectOpenHashSet<>();
    private static final Queue<Runnable> DEFERRED_QUEUE = new LinkedList<>();
    private static final List<IBlockUpdateEvent> BLOCK_UPDATE_HANDLERS = new ArrayList<>();

    private static IAntimatterRegistrar INTERNAL_REGISTRAR;

    private static void registerInternal(Class<?> c, String id, Object o, boolean checkDuplicates) {
        OBJECTS.putIfAbsent(c, new Object2ObjectLinkedOpenHashMap<>());
        if (checkDuplicates && OBJECTS.get(c).containsKey(id)) throw new IllegalStateException("Object: " + id + " for class " + c.getName() + " has already been registered by " + OBJECTS.get(c).get(id));
        OBJECTS.get(c).put(id, o);
    }

    public static void register(Class<?> c, String id, Object o) {
        registerInternal(c, id, o, true);
        if (o instanceof Item && !hasObjectBeenRegistered(Item.class, id)) registerInternal(Item.class, id, o, true);
        if (o instanceof Block && !hasObjectBeenRegistered(Block.class, id)) registerInternal(Block.class, id, o, true);
        if (o instanceof IRegistryEntryProvider && !hasObjectBeenRegistered(IRegistryEntryProvider.class, id)) registerInternal(IRegistryEntryProvider.class, id, o, true);
        if (o instanceof Material) MATERIAL_HASH_LOOKUP.put(((Material) o).getHash(), (Material) o);
    }

    public static void register(IAntimatterObject o) {
        register(o.getClass(), o.getId(), o);
    }

    private static boolean hasObjectBeenRegistered(Class<?> c, String id) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        return map != null && map.containsKey(id);
    }

    @Nullable
    public static <T> T get(Class<T> c, String id) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        return map != null ? c.cast(map.get(id)) : null;
    }

    public static <T> boolean has(Class<T> c, String id) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        return map != null && map.containsKey(id);
    }

    public static <T> List<T> all(Class<T> c) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        return map != null ? map.values().stream().map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static <T> void all(Class<T> c, Consumer<T> consumer) {
        all(c).forEach(consumer);
    }

    public static <T> void all(Class<T> c, String domain, Consumer<T> consumer) {
        all(c).stream().filter(o -> o instanceof IAntimatterObject && ((IAntimatterObject) o).getDomain().equals(domain)).forEach(consumer);
    }

    public static Material getMaterial(String id) {
        Material material = get(Material.class, id);
        return material != null ? material : Data.NULL;
    }

    @Nullable
    public static Material getMaterialById(int hash) {
        return MATERIAL_HASH_LOOKUP.get(hash);
    }

    public static void addToWorkQueue(Runnable runnable) {
        DEFERRED_QUEUE.add(runnable);
    }

    public static Queue<Runnable> getWorkQueue() {
        return DEFERRED_QUEUE;
    }

    /** Registrar Section **/
    public static void onRegistration(RegistrationEvent event) {
        if (REGISTRATION_EVENTS_HANDLED.contains(event)) throw new IllegalStateException("The RegistrationEvent " + event.name() + " has already been handled");
        REGISTRATION_EVENTS_HANDLED.add(event);
        INTERNAL_REGISTRAR.onRegistrationEvent(event);
        all(IAntimatterRegistrar.class, r -> r.onRegistrationEvent(event));
        if (CALLBACKS.containsKey(event.name())) CALLBACKS.get(event.name()).forEach(Runnable::run);
    }

    public static void onEvent(RegistrationEvent event, Runnable runnable) {
        CALLBACKS.computeIfAbsent(event.name(), k -> new ObjectArrayList<>()).add(runnable);
    }

    public static void addRegistrar(IAntimatterRegistrar registrar) {
        if (INTERNAL_REGISTRAR == null && registrar == Antimatter.INSTANCE) INTERNAL_REGISTRAR = registrar;
        else if (registrar.isEnabled() || AntimatterConfig.MOD_COMPAT.ENABLE_ALL_REGISTRARS) registerInternal(IAntimatterRegistrar.class, registrar.getId(), registrar, true);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistrationHelper::onRegistryEvent);
    }

    public static Optional<IAntimatterRegistrar> getRegistrar(String id) {
        return Optional.ofNullable(get(IAntimatterRegistrar.class, id));
    }

    public static boolean isRegistrarEnabled(String id) {
        return getRegistrar(id).map(IAntimatterRegistrar::isEnabled).orElse(false);
    }

//    @Nullable
//    public static Item getItem(String domain, String path) {
//        return Item.getByNameOrId(new ResourceLocation(domain, path).toString());
//    }
//
//    @Nullable
//    public static Block getBlock(String domain, String path) {
//        return Block.getBlockFromName(new ResourceLocation(domain, path).toString());
//    }

    /** Item Registry Section **/
    public static void addReplacement(MaterialType<?> type, Material material, ItemStack stack) {
        registerInternal(ItemStack.class, type.getId() + material.getId(), stack, true);
    }

    public static ItemStack getReplacement(MaterialType<?> type, Material material) {
        ItemStack stack = get(ItemStack.class, type.getId() + material.getId());
        return stack != null ? stack.copy() : ItemStack.EMPTY;
    }

    /** JEI Registry Section **/
    public static void registerJEICategory(RecipeMap map, GuiData gui) {
        if (ModList.get().isLoaded(Ref.MOD_JEI)) {
            //AntimatterJEIPlugin.registerCategory(map, gui);
        }
    }

    /** Fluid Cell Registry **/
    private final static Collection<ItemStack> FLUID_CELL_REGISTRY = new ObjectArrayList<>();

    public static void registerFluidCell(ItemStack stack) {
        //if (!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) return;
        //FLUID_CELL_REGISTRY.add(stack);
    }

    public static List<ItemStack> getFluidCells() {
        List<ItemStack> cells = new ObjectArrayList<>();
        FLUID_CELL_REGISTRY.forEach(c -> cells.add(c.copy()));
        return cells;
    }

    public static Collection<ItemStack> getFluidCells(Fluid fluid) {
        return getFluidCells(fluid, -1);
    }

    public static Collection<ItemStack> getFluidCells(Fluid fluid, int amount) {
        Collection<ItemStack> cells = getFluidCells();
//        for (ItemStack stack : cells) {
//            IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
//            if (fluidHandler == null) continue;
//            amount = amount != -1 ? amount : Integer.MAX_VALUE;
//            fluidHandler.fill(new FluidStack(fluid, amount), true);
//        }
        return cells;
    }

    /** Attempts to do smart interaction with a compatible Tile/Block **/
    public static boolean interact(TileEntity tile, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        Direction targetSide = Utils.getInteractSide(side, hitX, hitY, hitZ);
//        if (GregTechAPI.placeCover(tile, player, player.getHeldItem(hand), targetSide, hitX, hitY, hitZ)) return true;
//        if (tile.hasCapability(GTCapabilities.COVERABLE, targetSide)) {
//            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, targetSide);
//            if (coverHandler != null && coverHandler.onInteract(player, hand, targetSide, ToolType.get(player.getHeldItem(hand)))) return true;
//        }
//        if (tile.hasCapability(GTCapabilities.CONFIGURABLE, targetSide)) {
//            IInteractHandler interactHandler = tile.getCapability(GTCapabilities.CONFIGURABLE, targetSide);
//            if (interactHandler != null && interactHandler.onInteract(player, hand, targetSide, ToolType.get(player.getHeldItem(hand)))) return true;
//        }
        return false;
    }

    /** Attempts to place a cover on a tile at a given side **/
    public static boolean placeCover(TileEntity tile, PlayerEntity player, ItemStack stack, Direction side, float hitX, float hitY, float hitZ) {
//        if (stack.isEmpty()) return false;
//        ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, side);
//        if (coverHandler == null) return false;
//        Cover cover = GregTechAPI.getCoverFromCatalyst(stack);
//        if (cover == null) return false;
//        if (coverHandler.set(Utils.getInteractSide(side, hitX, hitY, hitZ), cover.onNewInstance(Utils.ca(1, stack)))) {
//            if (!player.isCreative()) stack.shrink(1);
//            return true;
//        }
        return false;
    }

    /** Attempts to remove a cover at a given side **/
    public static boolean removeCover(PlayerEntity player, ICoverHandler coverHandler, Direction side) {
        ItemStack toDrop = coverHandler.getCoverInstance(side).getCover().getDroppedStack();
        if (coverHandler.onPlace(side, Data.COVERNONE)) {
            if (!player.isCreative()) player.dropItem(toDrop, false);
            return true;
        }
        return false;
    }

    public static void registerBlockUpdateHandler(IBlockUpdateEvent handler) {
        BLOCK_UPDATE_HANDLERS.add(handler);
    }

    /**
     * COREMOD METHOD INSERTION: Runs every time when this is called:
     * @see ServerWorld#notifyBlockUpdate(BlockPos, BlockState, BlockState, int)
     */
    @SuppressWarnings("unused")
    public static void onNotifyBlockUpdate(ServerWorld world, BlockPos pos, BlockState oldState, BlockState newState) {
        BLOCK_UPDATE_HANDLERS.forEach(h -> h.onNotifyBlockUpdate(world, pos, oldState, newState));
    }

    public interface IBlockUpdateEvent {

        void onNotifyBlockUpdate(ServerWorld world, BlockPos pos, BlockState oldState, BlockState newState);
    }
}
