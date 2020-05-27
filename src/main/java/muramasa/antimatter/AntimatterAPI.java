package muramasa.antimatter;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.forge.ForgeDummyTagProviders;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.datagen.resources.ResourceMethod;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.recipe.RecipeMap;
import muramasa.antimatter.registration.*;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static muramasa.antimatter.util.Utils.getConventionalMaterialType;

public final class AntimatterAPI {

    private static final Object2ObjectMap<Class<?>, Object2ObjectMap<String, IAntimatterObject>> OBJECTS = new Object2ObjectOpenHashMap<>();
    private static final EnumMap<RegistrationEvent, List<Runnable>> CALLBACKS = new EnumMap<>(RegistrationEvent.class);
    private static final EnumSet<RegistrationEvent> REGISTRATION_EVENTS_HANDLED = EnumSet.noneOf(RegistrationEvent.class);
    private static final Object2ObjectOpenHashMap<String, List<Function<DataGenerator, IAntimatterProvider>>> PROVIDERS = new Object2ObjectOpenHashMap<>();
    private static final ObjectList<IBlockUpdateEvent> BLOCK_UPDATE_HANDLERS = new ObjectArrayList<>();
    private static final Int2ObjectMap<Item> REPLACEMENTS = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<Deque<Runnable>> DEFERRED_QUEUE = new Int2ObjectOpenHashMap<>();

    private static IAntimatterRegistrar INTERNAL_REGISTRAR;

    /** Internal Registry Section **/

    private static void registerInternal(Class<?> c, String id, IAntimatterObject o) {
        OBJECTS.putIfAbsent(c, new Object2ObjectLinkedOpenHashMap<>());
        IAntimatterObject key = OBJECTS.get(c).get(id);
        if (key != null) {
            throw new IllegalStateException(String.join("", "Class ", c.getName(), "'s object: ", id, " has already been registered by: ", key.getId()));
        }
        OBJECTS.get(c).put(id, o);
    }

    public static void register(Class<?> clazz, String id, IAntimatterObject o) {
        registerInternal(clazz, id, o);
        if (o instanceof Block && isObjectFresh(Block.class, id)) registerInternal(Block.class, id, o);
        else if (o instanceof Item && isObjectFresh(Item.class, id)) registerInternal(Item.class, id, o);
        else if (o instanceof IRegistryEntryProvider && isObjectFresh(IRegistryEntryProvider.class, id)) registerInternal(IRegistryEntryProvider.class, id, o);
    }

    public static void register(Class<?> clazz, IAntimatterObject o) {
        register(clazz, o.getId(), o);
    }

    public static void register(String id, IAntimatterObject o) {
        register(o.getClass(), id, o);
    }

    public static void register(IAntimatterObject o) {
        register(o.getClass(), o.getId(), o);
    }

    private static boolean isObjectFresh(Class<?> c, String id) {
        Object2ObjectMap<String, IAntimatterObject> map = OBJECTS.get(c);
        return map == null || !map.containsKey(id);
    }

    @Nullable
    public static <T> T get(Class<T> c, String id) {
        Object2ObjectMap<String, IAntimatterObject> map = OBJECTS.get(c);
        return map != null ? c.cast(map.get(id)) : null;
    }

    public static <T> boolean has(Class<T> c, String id) {
        Object2ObjectMap<String, IAntimatterObject> map = OBJECTS.get(c);
        return map != null && map.containsKey(id);
    }

    public static <T> List<T> all(Class<T> c) {
        Object2ObjectMap<String, IAntimatterObject> map = OBJECTS.get(c);
        return map != null ? map.values().stream().map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static <T> List<T> all(Class<T> c, String domain) {
        Object2ObjectMap<String, IAntimatterObject> map = OBJECTS.get(c);
        return map != null ? map.values().stream().filter(o -> o.getDomain().equals(domain)).map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static <T> void all(Class<T> c, Consumer<T> consumer) {
        all(c).forEach(consumer);
    }

    public static <T> void all(Class<T> c, String domain, Consumer<T> consumer) {
        all(c).stream().filter(o -> ((IAntimatterObject) o).getDomain().equals(domain)).forEach(consumer);
    }

    /**
     *  Providers and Dynamic Resource Pack Section
     *
     * TODO: Client/Server separate? Together? Common?
     */
    public static void addProvider(String domain, Function<DataGenerator, IAntimatterProvider> providerFunc) {
        PROVIDERS.computeIfAbsent(domain, k -> new ObjectArrayList<>()).add(providerFunc);
    }

    public static void onProviderInit(String domain, DataGenerator gen) {
        PROVIDERS.getOrDefault(domain, Collections.emptyList()).forEach(f -> gen.addProvider(f.apply(gen)));
    }

    public static void runBackgroundProviders() {
        Antimatter.LOGGER.debug("We do not condone these practices.");
        Ref.BACKGROUND_DATA_GENERATOR.addProviders(ForgeDummyTagProviders.DUMMY_FORGE_PROVIDERS);
        try {
            Ref.BACKGROUND_DATA_GENERATOR.run();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runProvidersDynamically(ResourceMethod method) {
        // Optimise by loading straight into DynamicResourcePack::add methods, instead of running -> looping through ran resources -> add to another map
        if (method != ResourceMethod.DYNAMIC_PACK) return;
        PROVIDERS.forEach((k, v) -> v.forEach(f -> {
            IAntimatterProvider prov = f.apply(Ref.DUMMY_GENERATOR);
            LogManager.getLogger().debug("Running " + prov.getName());
            prov.run();
            if (prov instanceof BlockStateProvider) {
                BlockStateProvider stateProv = (BlockStateProvider) prov;
                stateProv.models().generatedModels.forEach(DynamicResourcePack::addBlock);
                if (prov instanceof AntimatterBlockStateProvider) {
                    ((AntimatterBlockStateProvider) stateProv).getRegisteredBlocks().forEach((b, s) -> DynamicResourcePack.addState(b.getRegistryName(), s));
                }
            } else if (prov instanceof ItemModelProvider) {
                ((ItemModelProvider) prov).generatedModels.forEach(DynamicResourcePack::addItem);
            }
        }));
    }

    /** DeferredWorkQueue Section **/

    public static Optional<Deque<Runnable>> getCommonDeferredQueue() {
        return Optional.ofNullable(DEFERRED_QUEUE.get(0));
    }

    public static Optional<Deque<Runnable>> getClientDeferredQueue() {
        return Optional.ofNullable(DEFERRED_QUEUE.get(1));
    }

    public static Optional<Deque<Runnable>> getServerDeferredQueue() {
        return Optional.ofNullable(DEFERRED_QUEUE.get(2));
    }

    public static void runLaterCommon(Runnable... r) {
        DEFERRED_QUEUE.computeIfAbsent(0, q -> new LinkedList<>(Arrays.asList(r))).addAll(Arrays.asList(r));
    }

    public static void runLaterClient(Runnable... r) {
        DEFERRED_QUEUE.computeIfAbsent(1, q -> new LinkedList<>(Arrays.asList(r))).addAll(Arrays.asList(r));
    }

    public static void runLaterServer(Runnable... r) {
        DEFERRED_QUEUE.computeIfAbsent(2, q -> new LinkedList<>(Arrays.asList(r))).addAll(Arrays.asList(r));
    }

    /** Registrar Section **/

    public static void onRegistration(RegistrationEvent event) {
        if (REGISTRATION_EVENTS_HANDLED.contains(event)) throw new IllegalStateException("The RegistrationEvent " + event.name() + " has already been handled");
        REGISTRATION_EVENTS_HANDLED.add(event);
        INTERNAL_REGISTRAR.onRegistrationEvent(event);
        all(IAntimatterRegistrar.class, r -> r.onRegistrationEvent(event));
        if (CALLBACKS.containsKey(event)) CALLBACKS.get(event).forEach(Runnable::run);
    }

    public static void runOnEvent(RegistrationEvent event, Runnable runnable) {
        CALLBACKS.computeIfAbsent(event, k -> new ObjectArrayList<>()).add(runnable);
    }

    public static void addRegistrar(IAntimatterRegistrar registrar) {
        if (INTERNAL_REGISTRAR == null && registrar == Antimatter.INSTANCE) INTERNAL_REGISTRAR = registrar;
        else if (registrar.isEnabled() || AntimatterConfig.MOD_COMPAT.ENABLE_ALL_REGISTRARS) registerInternal(IAntimatterRegistrar.class, registrar.getId(), registrar);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Registration::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Registration::onRegister);
    }

    public static Optional<IAntimatterRegistrar> getRegistrar(String id) {
        return Optional.ofNullable(get(IAntimatterRegistrar.class, id));
    }

    public static boolean isRegistrarEnabled(String id) {
        return getRegistrar(id).map(IAntimatterRegistrar::isEnabled).orElse(false);
    }

    public static Item getReplacement(MaterialType<?> type, Material material, String... namespaces) {
        // Tag<Item> tag = ItemTags.getCollection().get(new ResourceLocation("forge", String.join("", getConventionalMaterialType(type), "/", material.getId())));
        Tag<Item> tag = Utils.getForgeItemTag(String.join("", getConventionalMaterialType(type), "/", material.getId()));
        return getReplacement(null, tag, namespaces);
    }

    /**
     * This must run after DataGenerators have ran OR when the tag jsons are acknowledged. Otherwise this is useless!
     *
     * @param originalItem  Item that wants a replacement, may be null if only the tag should be the only query
     * @param tag           Tag that wants a replacement (as the originalItem may have multiple tags to search from)
     * @param namespaces    Namespaces of the tags to check against, by default this only checks against 'minecraft' if no namespaces are defined
     * @return originalItem if there's nothing found, null if there is no originalItem, or an replacement
     */
    public static Item getReplacement(@Nullable Item originalItem, Tag<Item> tag, String... namespaces) {
        if (tag != null) {
            if (REPLACEMENTS.containsKey(tag.hashCode())) return REPLACEMENTS.get(tag.hashCode());
            Set<String> checks = Sets.newHashSet(namespaces);
            if (checks.isEmpty()) checks.add("minecraft");
            return tag.getAllElements().stream().filter(i -> checks.contains(Objects.requireNonNull(i.getRegistryName()).getNamespace()))
                    .findAny().map(i -> {
                        REPLACEMENTS.put(tag.hashCode(), i);
                        return i;
                    }).orElse(originalItem);
        }
        else throw new IllegalArgumentException("AntimatterAPI#getReplacement received a null tag!");
    }

    /** JEI Registry Section **/
    public static void registerJEICategory(RecipeMap map, GuiData gui) {
        if (ModList.get().isLoaded(Ref.MOD_JEI)) {
            //AntimatterJEIPlugin.registerCategory(map, gui);
        }
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
        ItemStack toDrop = coverHandler.getCover(side).getDroppedStack();
        if (coverHandler.onPlace(side, Data.COVER_NONE)) {
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
